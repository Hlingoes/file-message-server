package cn.henry.study.common.utils;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.csource.common.IniFileReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * description: 基于zookeeper的开源客户端Curator实现分布式锁
 * <p>
 * Curator内部是通过InterProcessMutex（可重入锁）来在zookeeper中创建临时有序节点实现的，
 * 如果通过临时节点及watch机制实现锁的话，这种方式存在一个比较大的问题：
 * 所有取锁失败的进程都在等待、监听创建的节点释放，很容易发生"羊群效应"，zookeeper的压力是比较大的，
 * 而临时有序节点就很好的避免了这个问题，Curator内部就是创建的临时有序节点。
 * 基本原理：
 * 创建临时有序节点，每个线程均能创建节点成功，但是其序号不同，只有序号最小的可以拥有锁，其它线程只需要监听比自己序号小的节点状态即可
 * 基本思路如下：
 * 1、在你指定的节点下创建一个锁目录lock；
 * 2、线程X进来获取锁在lock目录下，并创建临时有序节点；
 * 3、线程X获取lock目录下所有子节点，并获取比自己小的兄弟节点，如果不存在比自己小的节点，说明当前线程序号最小，顺利获取锁；
 * 4、此时线程Y进来创建临时节点并获取兄弟节点 ，判断自己是否为最小序号节点，发现不是，
 * 于是设置监听（watch）比自己小的节点（这里是为了发生上面说的羊群效应）；
 * 5、线程X执行完逻辑，删除自己的节点，线程Y监听到节点有变化，进一步判断自己是已经是最小节点，顺利获取锁。
 * <p>
 * 备注：
 * 业务层使用时要记得释放锁。要特别注意的是 interProcessMutex.acquire(2, TimeUnit.SECONDS)方法，可以设定等待时间，
 * 加上重试的次数，即排队等待时间= acquire × 次数，这两个值一定要设置好，
 * 因为使用了分布式锁之后，接口的TPS就下降了，没获取到锁的接口都在等待/重试，
 * 如果这里设置的最大等待时间4s，这时并发进来1000个请求，4秒内处理不完1000个请求怎么办呢？
 * 所以一定要设置好这个重试次数及单次等待时间，根据自己的压测接口设置合理的阈值，避免业务流转发生问题！
 *
 * @author Hlingoes
 * @date 2020/5/4 20:11
 * @citation https://blog.csdn.net/fanrenxiang/article/details/81704691
 */
public class ZkDistributedLock {
    public static Logger logger = LoggerFactory.getLogger(ZkDistributedLock.class);

    /**
     * 可重入排它锁
     */
    private InterProcessMutex interProcessMutex;
    /**
     * 竞争资源标志
     */
    private String lockName;
    /**
     * 根节点
     */
    private static String ROOT_PATH;
    private static String ZK_URL;
    private static CuratorFramework curatorFramework;

    static {
        // 读取resource下的文件
        Properties props = new Properties();
        InputStream in = IniFileReader.loadFromOsFileSystemOrClasspathAsStream("zookeeper-client.properties");
        if (in != null) {
            try {
                props.load(in);
                ROOT_PATH = props.getProperty("zookeeper.rootPath");
                ZK_URL = props.getProperty("zookeeper.host");
                curatorFramework = CuratorFrameworkFactory.newClient(ZK_URL, new ExponentialBackoffRetry(1000, 3));
                curatorFramework.start();
            } catch (IOException e) {
                logger.error("zookeeper-client.properties配置文件初始化失败", e);
            }
        }
    }

    /**
     * 实例化
     *
     * @param lockName
     */
    public ZkDistributedLock(String lockName) {
        try {
            this.lockName = lockName;
            interProcessMutex = new InterProcessMutex(curatorFramework, ROOT_PATH + lockName);
        } catch (Exception e) {
            logger.error("initial InterProcessMutex exception=" + e);
        }
    }

    /**
     * 获取锁
     */
    public void acquireLock() {
        int flag = 0;
        try {
            //重试2次，每次最大等待2s，也就是最大等待4s
            while (!interProcessMutex.acquire(2, TimeUnit.SECONDS)) {
                flag++;
                // 重试两次
                if (flag > 1) {
                    break;
                }
            }
        } catch (Exception e) {
            logger.error("distributed lock acquire exception=" + e);
        }
        if (flag > 1) {
            logger.info("Thread:" + Thread.currentThread().getId() + " acquire distributed lock  busy");
        } else {
            logger.info("Thread:" + Thread.currentThread().getId() + " acquire distributed lock  success");
        }
    }

    /**
     * 释放锁
     */
    public void releaseLock() {
        try {
            if (interProcessMutex != null && interProcessMutex.isAcquiredInThisProcess()) {
                interProcessMutex.release();
                curatorFramework.delete().inBackground().forPath(ROOT_PATH + lockName);
                logger.info("Thread:" + Thread.currentThread().getId() + " release distributed lock  success");
            }
        } catch (Exception e) {
            logger.info("Thread:" + Thread.currentThread().getId() + " release distributed lock  exception=" + e);
        }
    }
}

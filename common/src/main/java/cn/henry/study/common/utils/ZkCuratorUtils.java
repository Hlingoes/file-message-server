package cn.henry.study.common.utils;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.Stat;
import org.csource.common.IniFileReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
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
public class ZkCuratorUtils {
    public static Logger logger = LoggerFactory.getLogger(ZkCuratorUtils.class);

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
                // 重试策略
                RetryPolicy retryPolicy = new RetryNTimes(3, 5000);
                curatorFramework = CuratorFrameworkFactory.builder()
                        .connectString(ZK_URL)
                        .sessionTimeoutMs(10000).retryPolicy(retryPolicy)
                        // 指定命名空间后，client的所有路径操作都会以/workspace开头
                        .namespace(ROOT_PATH).build();
                curatorFramework.start();
            } catch (IOException e) {
                logger.error("zookeeper-client.properties配置文件初始化失败", e);
            }
        }
    }

    /**
     * description: 实例化可重入锁
     *
     * @param lockName
     * @return cn.henry.study.common.utils.ZkCuratorUtils.DistributedLock
     * @author Hlingoes 2020/5/4
     */
    public static DistributedLock instanceDistributedLock(String lockName) {
        return new DistributedLock(lockName);
    }

    public static class DistributedLock {
        /**
         * 可重入排它锁
         */
        private InterProcessMutex interProcessMutex;
        /**
         * 竞争资源标志
         */
        private String lockName;

        public DistributedLock(String lockName) {
            this.lockName = lockName;
            try {
                this.interProcessMutex = new InterProcessMutex(curatorFramework, ROOT_PATH + lockName);
            } catch (Exception e) {
                logger.error("initial InterProcessMutex exception", e);
            }
        }

        /**
         * 获取锁
         */
        public void acquireLock(int repeats, int waitTime) {
            int flag = 0;
            try {
                // 重试2次，每次最大等待2s，也就是最大等待4s
                while (!this.interProcessMutex.acquire(waitTime, TimeUnit.SECONDS)) {
                    flag++;
                    // 重试两次
                    if (flag > repeats) {
                        break;
                    }
                }
            } catch (Exception e) {
                logger.error("distributed lock acquire exception", e);
            }
            if (flag > repeats) {
                logger.info("Thread: {}, acquire distributed lock  busy", Thread.currentThread().getId());
            } else {
                logger.info("Thread: {}, acquire distributed lock  success", Thread.currentThread().getId());
            }
        }

        /**
         * 释放锁
         */
        public void releaseLock() {
            try {
                if (this.interProcessMutex != null && this.interProcessMutex.isAcquiredInThisProcess()) {
                    this.interProcessMutex.release();
                    curatorFramework.delete().inBackground().forPath(ROOT_PATH + lockName);
                    logger.info("Thread: {}, release distributed lock  success", Thread.currentThread().getId());
                }
            } catch (Exception e) {
                logger.info("Thread: {}, release distributed lock  exception", Thread.currentThread().getId(), e);
            }
        }
    }

    /**
     * description: 获取当前zookeeper的状态
     *
     * @param
     * @return org.apache.curator.framework.imps.CuratorFrameworkState
     * @author Hlingoes 2020/5/4
     */
    public static CuratorFrameworkState getStatus() {
        CuratorFrameworkState state = curatorFramework.getState();
        logger.info("服务是否已经启动: {}", (state == CuratorFrameworkState.STARTED));
        return state;
    }

    /**
     * description: 创建节点并写入数据
     *
     * @param nodePath
     * @param data
     * @return void
     * @author Hlingoes 2020/5/4
     */
    public static void createNodes(String nodePath, String data) throws Exception {
        curatorFramework.create().creatingParentsIfNeeded()
                // 节点类型
                .withMode(CreateMode.PERSISTENT)
                .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                .forPath(nodePath, data.getBytes("UTF-8"));
    }

    /**
     * description: 获取节点数据
     *
     * @param nodePath
     * @return java.lang.String
     * @author Hlingoes 2020/5/4
     */
    public static String getNodeData(String nodePath) throws Exception {
        Stat stat = new Stat();
        byte[] bytes = curatorFramework.getData().storingStatIn(stat).forPath(nodePath);
        String data = new String(bytes, "UTF-8");
        logger.info("节点version: {}", stat.getVersion());
        logger.info("节点信息: {}, 节点数据: {}", stat.toString(), data);
        return data;
    }

    /**
     * description: 获取该节点的所有子节点
     *
     * @param nodePath
     * @return java.util.List<java.lang.String>
     * @author Hlingoes 2020/5/4
     */
    public static List<String> getChildrenNodes(String nodePath) throws Exception {
        List<String> childNodes = curatorFramework.getChildren().forPath(nodePath);
        for (String s : childNodes) {
            logger.info("节点名: {}", s);
        }
        return childNodes;
    }

    /**
     * description: 更新节点
     *
     * @param version
     * @param nodePath
     * @param data
     * @return void
     * @author Hlingoes 2020/5/4
     */
    public static void updateNode(int version, String nodePath, String data) throws Exception {
        // 传入版本号，如果版本号错误则拒绝更新操作,并抛出BadVersion异常
        curatorFramework.setData().withVersion(version).forPath(nodePath, data.getBytes("UTF-8"));
    }

    /**
     * description: 删除节点
     *
     * @param version
     * @param nodePath
     * @return void
     * @author Hlingoes 2020/5/4
     */
    public static void deleteNodes(int version, String nodePath) throws Exception {
        curatorFramework.delete()
                // 如果删除失败，那么在会继续执行，直到成功
                .guaranteed()
                // 如果有子节点，则递归删除
                .deletingChildrenIfNeeded()
                // 传入版本号，如果版本号错误则拒绝删除操作,并抛出BadVersion异常
                .withVersion(version)
                .forPath(nodePath);
    }

    /**
     * description: 判断节点是否存在
     *
     * @param nodePath
     * @return boolean
     * @author Hlingoes 2020/5/4
     */
    public static boolean existNode(String nodePath) throws Exception {
        // 如果节点存在则返回其状态信息，如果不存在则为null
        Stat stat = curatorFramework.checkExists().forPath(nodePath);
        boolean exist = (null != stat);
        logger.info("节点是否存在: {}", exist);
        return exist;
    }

    /**
     * description: 使用usingWatcher注册的监听是一次性的,即监听只会触发一次，监听完毕后就销毁
     *
     * @param nodePath
     * @return void
     * @author Hlingoes 2020/5/4
     */
    public static void disposableWatch(String nodePath) throws Exception {
        curatorFramework.getData().usingWatcher(new CuratorWatcher() {
            @Override
            public void process(WatchedEvent event) {
                logger.info("节点: {}, 发生了事件: {}", event.getPath(), event.getType());
            }
        }).forPath(nodePath);
    }

    /**
     * description: 注册永久监听，供参考，需要在回调中写业务实现
     *
     * @param nodePath
     * @return void
     * @author Hlingoes 2020/5/4
     */
    public static void permanentWatch(String nodePath) throws Exception {
        // 使用NodeCache包装节点，对其注册的监听作用于节点，且是永久性的
        NodeCache nodeCache = new NodeCache(curatorFramework, nodePath);
        // 通常设置为true, 代表创建nodeCache时,就去获取对应节点的值并缓存
        nodeCache.start(true);
        nodeCache.getListenable().addListener(new NodeCacheListener() {
            @Override
            public void nodeChanged() {
                ChildData currentData = nodeCache.getCurrentData();
                if (currentData != null) {
                    logger.info("节点路径: {}，数据: {}", currentData.getPath(), new String(currentData.getData()));
                }
            }
        });
    }

    /**
     * description: 监听子节点的操作，供参考，需要在回调中写业务实现
     *
     * @param nodePath
     * @return void
     * @author Hlingoes 2020/5/4
     */
    public static void permanentChildrenNodesWatch(String nodePath) throws Exception {
        // 第三个参数代表除了节点状态外，是否还缓存节点内容
        PathChildrenCache childrenCache = new PathChildrenCache(curatorFramework, nodePath, true);
        /*
         * StartMode代表初始化方式:
         *    NORMAL: 异步初始化
         *    BUILD_INITIAL_CACHE: 同步初始化
         *    POST_INITIALIZED_EVENT: 异步并通知,初始化之后会触发事件
         */
        childrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
        List<ChildData> childDataList = childrenCache.getCurrentData();
        childDataList.forEach(x -> logger.info("当前数据节点的子节点列表: {}", x.getPath()));
        childrenCache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) {
                switch (event.getType()) {
                    case INITIALIZED:
                        logger.info("childrenCache初始化完成");
                        break;
                    case CHILD_ADDED:
                        // 需要注意的是: 即使是之前已经存在的子节点，也会触发该监听，因为会把该子节点加入childrenCache缓存中
                        logger.info("增加子节点: {}", event.getData().getPath());
                        break;
                    case CHILD_REMOVED:
                        logger.info("删除子节点: {}", event.getData().getPath());
                        break;
                    case CHILD_UPDATED:
                        logger.info("被修改的子节点的路径: {}", event.getData().getPath());
                        logger.info("修改后的数据: {}", new String(event.getData().getData()));
                        break;
                }
            }
        });
    }

    /**
     * description: 关闭连接
     *
     * @param
     * @return void
     * @author Hlingoes 2020/5/4
     */
    public static void destroy() {
        if (curatorFramework != null) {
            curatorFramework.close();
        }
    }
}

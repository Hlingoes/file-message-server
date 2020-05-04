package cn.henry.study.common;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * description:
 *
 * @author Hlingoes
 * @date 2020/5/4 17:06
 * @citation https://github.com/heibaiying/BigData-Notes
 */
public class ZkBasicOperationTest {
    private static Logger logger = LoggerFactory.getLogger(ZkBasicOperationTest.class);
    private CuratorFramework client = null;
    private static String zkServerPath = "localhost:2181";
    private static String nodePath = "/henry/test_zookeeper";
    private static String namespace = "workspace";

    @Before
    public void prepare() {
        // 重试策略
        RetryPolicy retryPolicy = new RetryNTimes(3, 5000);
        client = CuratorFrameworkFactory.builder()
                .connectString(zkServerPath)
                .sessionTimeoutMs(10000).retryPolicy(retryPolicy)
                // 指定命名空间后，client的所有路径操作都会以/workspace开头
                .namespace(namespace).build();
        client.start();
    }

    /**
     * 获取当前zookeeper的状态
     */
    @Test
    public void getStatus() {
        CuratorFrameworkState state = client.getState();
        logger.info("服务是否已经启动: {}", (state == CuratorFrameworkState.STARTED));
    }

    /**
     * 创建节点(s)
     */
    @Test
    public void createNodes() throws Exception {
        byte[] data = "abcdefg".getBytes();
        client.create().creatingParentsIfNeeded()
                // 节点类型
                .withMode(CreateMode.PERSISTENT)
                .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                .forPath(nodePath, data);
    }

    /**
     * 获取节点信息
     */
    @Test
    public void getNode() throws Exception {
        Stat stat = new Stat();
        byte[] data = client.getData().storingStatIn(stat).forPath(nodePath);
        logger.info("节点数据: {}", new String(data));
        logger.info("节点version: {}", stat.getVersion());
        logger.info("节点信息: {}", stat.toString());
    }

    /**
     * 获取该节点的所有子节点
     */
    @Test
    public void getChildrenNodes() throws Exception {
        List<String> childNodes = client.getChildren().forPath("/" + namespace);
        for (String s : childNodes) {
            logger.info("节点名: {}", s);
        }
    }

    /**
     * 更新节点
     */
    @Test
    public void updateNode() throws Exception {
        byte[] newData = "ssssss".getBytes();
        // 传入版本号，如果版本号错误则拒绝更新操作,并抛出BadVersion异常
        client.setData().withVersion(0)
                .forPath(nodePath, newData);
    }

    /**
     * 删除节点
     */
    @Test
    public void deleteNodes() throws Exception {
        client.delete()
                // 如果删除失败，那么在会继续执行，直到成功
                .guaranteed()
                // 如果有子节点，则递归删除
                .deletingChildrenIfNeeded()
                // 传入版本号，如果版本号错误则拒绝删除操作,并抛出BadVersion异常
                .withVersion(0)
                .forPath(nodePath);
    }

    /**
     * 判断节点是否存在
     */
    @Test
    public void existNode() throws Exception {
        // 如果节点存在则返回其状态信息如果不存在则为null
        Stat stat = client.checkExists().forPath(nodePath + "aa/bb/cc");
        logger.info("节点是否存在: {}", !(stat == null));
    }


    /**
     * 使用usingWatcher注册的监听是一次性的,即监听只会触发一次，监听完毕后就销毁
     */
    @Test
    public void DisposableWatch() throws Exception {
        client.getData().usingWatcher(new CuratorWatcher() {
            public void process(WatchedEvent event) {
                logger.info("节点: {}, 发生了事件: {}", event.getPath(), event.getType());
            }
        }).forPath(nodePath);
        // 休眠以观察测试效果
        Thread.sleep(1000 * 1000);
    }

    /**
     * 注册永久监听
     */
    @Test
    public void permanentWatch() throws Exception {
        // 使用NodeCache包装节点，对其注册的监听作用于节点，且是永久性的
        NodeCache nodeCache = new NodeCache(client, nodePath);
        // 通常设置为true, 代表创建nodeCache时,就去获取对应节点的值并缓存
        nodeCache.start(true);
        nodeCache.getListenable().addListener(new NodeCacheListener() {
            public void nodeChanged() {
                ChildData currentData = nodeCache.getCurrentData();
                if (currentData != null) {
                    logger.info("节点路径: {}，数据: {}", currentData.getPath(), new String(currentData.getData()));
                }
            }
        });
        // 休眠以观察测试效果
        Thread.sleep(1000 * 1000);
    }

    /**
     * 监听子节点的操作
     */
    @Test
    public void permanentChildrenNodesWatch() throws Exception {
        // 第三个参数代表除了节点状态外，是否还缓存节点内容
        PathChildrenCache childrenCache = new PathChildrenCache(client, "/" + namespace, true);
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
        // 休眠以观察测试效果
        Thread.sleep(1000 * 1000);
    }

    @After
    public void destroy() {
        if (client != null) {
            client.close();
        }
    }
}

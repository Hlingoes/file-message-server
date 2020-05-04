package cn.henry.study.common;

import cn.henry.study.common.utils.ZkCuratorUtils;
import org.junit.After;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * description:
 *
 * @author Hlingoes
 * @date 2020/5/4 17:06
 * @citation https://github.com/heibaiying/BigData-Notes
 */
public class ZkUtilsTest {
    private static Logger logger = LoggerFactory.getLogger(ZkUtilsTest.class);

    /**
     * 获取当前zookeeper的状态
     */
    @Test
    public void getStatus() {
        ZkCuratorUtils.getStatus();
    }

    /**
     * 创建节点(s)
     */
    @Test
    public void createNodes() throws Exception {
        ZkCuratorUtils.createNodes("/test/fun", "hello zookeeper");
    }

    /**
     * 获取节点信息
     */
    @Test
    public void getNode() throws Exception {
        ZkCuratorUtils.getNodeData("/test/fun");
    }

    /**
     * 获取该节点的所有子节点
     */
    @Test
    public void getChildrenNodes() throws Exception {
        ZkCuratorUtils.getChildrenNodes("/test/fun");
    }

    /**
     * 更新节点
     */
    @Test
    public void updateNode() throws Exception {
        ZkCuratorUtils.updateNode(0, "/test/fun", "see you again");
    }

    /**
     * 删除节点
     */
    @Test
    public void deleteNodes() throws Exception {
        ZkCuratorUtils.deleteNodes(0, "/test/fun");
    }

    /**
     * 判断节点是否存在
     */
    @Test
    public void existNode() throws Exception {
        ZkCuratorUtils.existNode("/test/fun");
    }


    /**
     * 使用usingWatcher注册的监听是一次性的,即监听只会触发一次，监听完毕后就销毁
     */
    @Test
    public void disposableWatch() throws Exception {
        ZkCuratorUtils.disposableWatch("/test/fun");
        // 休眠以观察测试效果
        Thread.sleep(1000 * 1000);
    }

    /**
     * 注册永久监听
     */
    @Test
    public void permanentWatch() throws Exception {
        ZkCuratorUtils.permanentWatch("/test/fun");
        // 休眠以观察测试效果
        Thread.sleep(1000 * 1000);
    }

    /**
     * 监听子节点的操作
     */
    @Test
    public void permanentChildrenNodesWatch() throws Exception {
        ZkCuratorUtils.permanentChildrenNodesWatch("/test/fun");
        // 休眠以观察测试效果
        Thread.sleep(1000 * 1000);
    }

    @After
    public void destroy() {
        ZkCuratorUtils.destroy();
    }
}

package cn.henry.study.common;

import cn.henry.study.common.utils.ZkCuratorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

/**
 * description:
 *
 * @author Hlingoes
 * @date 2020/6/13 22:55
 */
public class ZkLockThread implements Runnable {
    private Logger logger = LoggerFactory.getLogger(ZkLockThread.class);

    private static String nodePath = "/test/five_thread_update";

    @Override
    public void run() {
        while (true) {
            try {
                plus();
                Thread.sleep(randomNumber());
                minus();
            } catch (Exception e) {
                logger.error("zookeeper error", e);
            }
        }
    }

    private void minus() throws Exception {
        String data = ZkCuratorUtils.getNodeData(nodePath);
        int count = Integer.parseInt(data);
        String modifyData = (count - 1) + "";
        ZkCuratorUtils.updateNode(nodePath, modifyData);
        logger.info("minus: nodePath = {}, data = {}", nodePath, modifyData);
    }

    private long randomNumber() {
        Random rand = new Random();
        int max = 1000;
        int min = 300;
        return rand.nextInt(max - min + 1) + min;
    }

    private void plus() throws Exception {
        if (ZkCuratorUtils.existNode(nodePath)) {
            String data = ZkCuratorUtils.getNodeData(nodePath);
            int count = Integer.parseInt(data);
            if (count >= 5) {
                logger.info("同时连接了5个，请等待");
            } else {
                String modifyData = (count + 1) + "";
                ZkCuratorUtils.updateNode(nodePath, modifyData);
                logger.info("plus: nodePath = {}, data = {}", nodePath, modifyData);
            }
        } else {
            ZkCuratorUtils.createNodes(nodePath, "1");
        }
    }
}

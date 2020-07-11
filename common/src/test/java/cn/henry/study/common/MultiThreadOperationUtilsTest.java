package cn.henry.study.common;

import cn.henry.study.common.utils.ThreadPoolExecutorUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * description: 多线程业务分治归并处理测试
 *
 * @author Hlingoes
 * @date 2020/5/23 0:23
 */
public class MultiThreadOperationUtilsTest {
    private static Logger logger = LoggerFactory.getLogger(MultiThreadOperationUtilsTest.class);

    @Test
    public void testZkLocks() {
        ThreadPoolExecutor executor = ThreadPoolExecutorUtils.getExecutorPool();
        for (int i = 0; i < 10; i++) {
            executor.execute(new ZkLockThread());
        }
        ThreadPoolExecutorUtils.closeAfterComplete(executor);
    }

}

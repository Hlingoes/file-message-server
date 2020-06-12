package cn.henry.study.common;

import cn.henry.study.common.utils.MultiThreadOperationUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * description: 多线程业务分治归并处理测试
 *
 * @author Hlingoes
 * @date 2020/5/23 0:23
 */
public class MultiThreadOperationUtilsTest {
    private static Logger logger = LoggerFactory.getLogger(MultiThreadOperationUtilsTest.class);

    @Test
    public void testBatchExecute() {
        try {
            Object object = MultiThreadOperationUtils.batchExecute(new MultiThreadServiceTest(), 10, new Object[]{"test"});
            logger.info("测试完成: {}", object.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

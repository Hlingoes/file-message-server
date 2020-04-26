package cn.henry.study.web;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * description: java基础知识测试
 *
 * @author Hlingoes
 * @date 2019/12/22 1:13
 */
public class FundamentalTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(FundamentalTest.class);

    public void testFormat() {
        LOGGER.info(String.format("http-demo-%d", 5));
    }

    @Test
    public void encodingPath() {
        String path = "a//b\\\\c\\d/e";
        LOGGER.info(path.replace("\\", "/").replaceAll("//", "/"));
    }
}

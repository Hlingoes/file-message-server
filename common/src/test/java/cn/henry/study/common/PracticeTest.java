package cn.henry.study.common;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.OptionHelper;
import cn.henry.study.common.enums.LogNameEnum;
import cn.henry.study.common.utils.LoggerUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * description:
 *
 * @author Hlingoes
 * @date 2020/5/22 23:45
 */
public class PracticeTest {
    private static Logger logger = LoggerFactory.getLogger(PracticeTest.class);
    private static Logger testLogger = LoggerUtils.getLogger(LogNameEnum.TEST, PracticeTest.class);

    @Test
    public void loggerUtilsTest() {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        /**
         *  <property scope="context" name="LOG_HOME" value="log"/>
         *  <property scope="context" name="LOG_NAME_PREFIX" value="common"/>
         */
        String oph = OptionHelper.substVars("${LOG_HOME}/${LOG_NAME_PREFIX}/test-log.log", context);
        logger.info("logger默认配置的日志输出");
        testLogger.info("testLogger#####{}####", oph);
        testLogger.info("testLogger看到这条信息就是info");
        testLogger.error("testLogger看到这条信息就是error");
    }

}

package cn.henry.study.common;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.OptionHelper;
import cn.henry.study.common.enums.LogNameEnum;
import cn.henry.study.common.utils.LoggerUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * description:
 *
 * @author Hlingoes
 * @date 2020/5/22 23:45
 */
public class PracticeTest {
    private Logger logger = LoggerFactory.getLogger(PracticeTest.class);


    @Test
    public void testList() {
        int size = 50;
        List<String> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            list.add("test_" + i);
        }
        String insertSql = "insert into test (id) values ";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            if ((i + 1) % 5 == 0 || (i + 1) == size) {
                sb.append("('" + list.get(i) + "')");
                System.out.println(insertSql + sb.toString());
                sb = new StringBuilder();
            } else {
                sb.append("('" + list.get(i) + "'),");
            }
        }
    }

    @Test
    public void loggerUtilsTest() {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        String oph = OptionHelper.substVars("${LOG_HOME}/test-log-.log", context);
        logger.info("这个就没问题");
        LoggerUtils.getLogger(LogNameEnum.TEST, PracticeTest.class).info("#####{}####", oph);
        LoggerUtils.getLogger(LogNameEnum.TEST, PracticeTest.class).info("看到这条信息就是info");
        LoggerUtils.getLogger(LogNameEnum.TEST, PracticeTest.class).error("看到这条信息就是error");
    }

}

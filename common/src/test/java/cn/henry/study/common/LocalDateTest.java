package cn.henry.study.common;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Objects;

/**
 * description: 测试java8的时间API
 *
 * @author Hlingoes
 * @date 2020/5/16 17:51
 */
public class LocalDateTest {
    private Logger logger = LoggerFactory.getLogger(LocalDateTest.class);

    @Test
    public void testMonth() {
        LocalDate today = LocalDate.now();
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMM");
        LocalDate date = LocalDate.now();
        date = date.plusMonths(1);
        logger.info("格式化日期格: {}", df.format(date));
        // 本月的第一天
        LocalDate firstDay = LocalDate.of(today.getYear(), today.getMonth(), 1);
        // 本月的最后一天
        LocalDate lastDay = today.with(TemporalAdjusters.lastDayOfMonth());
        logger.info("本月的第一天: {}", firstDay);
        logger.info("本月的最后一天: {}", lastDay);
        logger.info("今天是不是本月的最后一天: {}", Objects.equals(today, lastDay));
        logger.info("今天是不是本月的第一天: {}", Objects.equals(today, firstDay));
    }
}

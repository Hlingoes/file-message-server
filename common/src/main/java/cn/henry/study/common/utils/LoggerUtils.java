package cn.henry.study.common.utils;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.filter.LevelFilter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import ch.qos.logback.core.spi.FilterReply;
import ch.qos.logback.core.util.FileSize;
import ch.qos.logback.core.util.OptionHelper;
import cn.henry.study.common.enums.LogNameEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

/**
 * description: 自定义的日志工具类，
 * 需要在logback.xml,logback-spring.xml或自定义的logback-custom.xml中写入基础配置
 *
 * @author Hlingoes
 * @date 2020/6/10 19:38
 */
public class LoggerUtils {
    private static String consoleAppenderName = "serve-console";
    private static String maxFileSize = "50MB";
    private static String totalSizeCap = "10GB";
    private static int maxHistory = 30;
    private static ConsoleAppender defaultConsoleAppender = null;

    static {
        Map<String, Appender<ILoggingEvent>> appenderMap = allAppenders();
        appenderMap.forEach((key, appender) -> {
            if (appender instanceof ConsoleAppender) {
                defaultConsoleAppender = (ConsoleAppender) appender;
                return;
            }
        });
    }


    /**
     * description: 获取自定义的logger日志
     *
     * @param logNameEnum
     * @param clazz
     * @return org.slf4j.Logger
     * @author Hlingoes 2020/6/10
     */
    public static Logger getLogger(LogNameEnum logNameEnum, Class clazz) {
        ch.qos.logback.classic.Logger logger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(clazz);
        LoggerContext loggerContext = logger.getLoggerContext();
        RollingFileAppender errorAppender = createAppender(logNameEnum.getLogName(), Level.ERROR, loggerContext);
        RollingFileAppender infoAppender = createAppender(logNameEnum.getLogName(), Level.INFO, loggerContext);
        Optional<ConsoleAppender> consoleAppender = Optional.ofNullable(defaultConsoleAppender);
        ConsoleAppender realConsoleAppender = consoleAppender.orElse(createConsoleAppender(loggerContext));
        // 设置不向上级打印信息
        logger.setAdditive(false);
        logger.addAppender(errorAppender);
        logger.addAppender(infoAppender);
        logger.addAppender(realConsoleAppender);
        return logger;
    }

    /**
     * description: 创建日志文件的file appender
     *
     * @param name
     * @param level
     * @return ch.qos.logback.core.rolling.RollingFileAppender
     * @author Hlingoes 2020/6/10
     */
    private static RollingFileAppender createAppender(String name, Level level, LoggerContext loggerContext) {
        RollingFileAppender appender = new RollingFileAppender();
        // 这里设置级别过滤器
        appender.addFilter(createLevelFilter(level));
        // 设置上下文，每个logger都关联到logger上下文，默认上下文名称为default。
        // 但可以使用<scope="context">设置成其他名字，用于区分不同应用程序的记录。一旦设置，不能修改。
        appender.setContext(loggerContext);
        // appender的name属性
        appender.setName(name.toUpperCase() + "-" + level.levelStr.toUpperCase());
        // 设置文件名
        String logPath = OptionHelper.substVars("${logPath}-" + name + "-" + level.levelStr.toLowerCase() + ".log", loggerContext);
        appender.setFile(logPath);
        appender.setAppend(true);
        appender.setPrudent(false);
        // 加入下面两个节点
        appender.setRollingPolicy(createRollingPolicy(name, level, loggerContext, appender));
        appender.setEncoder(createEncoder(loggerContext));
        appender.start();
        return appender;
    }

    /**
     * description: 创建窗口输入的appender
     *
     * @param
     * @return ch.qos.logback.core.ConsoleAppender
     * @author Hlingoes 2020/6/10
     */
    private static ConsoleAppender createConsoleAppender(LoggerContext loggerContext) {
        ConsoleAppender appender = new ConsoleAppender();
        appender.setContext(loggerContext);
        appender.setName(consoleAppenderName);
        appender.addFilter(createLevelFilter(Level.DEBUG));
        appender.setEncoder(createEncoder(loggerContext));
        appender.start();
        return appender;
    }

    /**
     * description: 设置日志的滚动策略
     *
     * @param name
     * @param level
     * @param context
     * @param appender
     * @return ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy
     * @author Hlingoes 2020/6/10
     */
    private static TimeBasedRollingPolicy createRollingPolicy(String name, Level level, LoggerContext context, FileAppender appender) {
        String fp = OptionHelper.substVars("${logPath}/${LOG_NAME_PREFIX}-" + name + "-" + level.levelStr.toLowerCase() + "_%d{yyyy-MM-dd}_%i.log", context);
        TimeBasedRollingPolicy rollingPolicyBase = new TimeBasedRollingPolicy<>();
        // 设置上下文，每个logger都关联到logger上下文，默认上下文名称为default。
        // 但可以使用<scope="context">设置成其他名字，用于区分不同应用程序的记录。一旦设置，不能修改。
        rollingPolicyBase.setContext(context);
        // 设置父节点是appender
        rollingPolicyBase.setParent(appender);
        // 设置文件名模式
        rollingPolicyBase.setFileNamePattern(fp);
        SizeAndTimeBasedFNATP sizeAndTimeBasedFNATP = new SizeAndTimeBasedFNATP();
        // 最大日志文件大小
        sizeAndTimeBasedFNATP.setMaxFileSize(FileSize.valueOf(maxFileSize));
        rollingPolicyBase.setTimeBasedFileNamingAndTriggeringPolicy(sizeAndTimeBasedFNATP);
        // 设置最大历史记录为30条
        rollingPolicyBase.setMaxHistory(maxHistory);
        rollingPolicyBase.start();

        return rollingPolicyBase;
    }

    /**
     * description: 设置日志的输出格式
     *
     * @param context
     * @return ch.qos.logback.classic.encoder.PatternLayoutEncoder
     * @author Hlingoes 2020/6/10
     */
    private static PatternLayoutEncoder createEncoder(LoggerContext context) {
        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        // 设置上下文，每个logger都关联到logger上下文，默认上下文名称为default。
        // 但可以使用<scope="context">设置成其他名字，用于区分不同应用程序的记录。一旦设置，不能修改。
        encoder.setContext(context);
        // 设置格式
        String pattern = OptionHelper.substVars("${pattern}", context);
        encoder.setPattern(pattern);
        encoder.setCharset(Charset.forName("utf-8"));
        encoder.start();
        return encoder;
    }

    /**
     * description: 设置打印日志的级别
     *
     * @param level
     * @return ch.qos.logback.core.filter.Filter
     * @author Hlingoes 2020/6/10
     */
    private static Filter createLevelFilter(Level level) {
        LevelFilter levelFilter = new LevelFilter();
        levelFilter.setLevel(level);
        levelFilter.setOnMatch(FilterReply.ACCEPT);
        levelFilter.setOnMismatch(FilterReply.DENY);
        levelFilter.start();
        return levelFilter;
    }

    /**
     * description: 读取logback配置文件中的所有appender
     *
     * @param
     * @return java.util.Map<java.lang.String, ch.qos.logback.core.Appender < ch.qos.logback.classic.spi.ILoggingEvent>>
     * @author Hlingoes 2020/6/10
     */
    private static Map<String, Appender<ILoggingEvent>> allAppenders() {
        Map<String, Appender<ILoggingEvent>> appenderMap = new HashMap<>();
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        for (ch.qos.logback.classic.Logger logger : context.getLoggerList()) {
            for (Iterator<Appender<ILoggingEvent>> index = logger.iteratorForAppenders(); index.hasNext(); ) {
                Appender<ILoggingEvent> appender = index.next();
                appenderMap.put(appender.getName(), appender);
            }
        }
        return appenderMap;
    }

    public static Logger getLogger(String jobName, Class<?> cls) {
        ch.qos.logback.classic.Logger logger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(cls);
        LoggerContext loggerContext = logger.getLoggerContext();
        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(loggerContext);
        encoder.setPattern("%d{yyyy-MM-dd HH:mm:ss} [%thread] %level %logger{35}:%line - %msg%n");
        encoder.start();

        RollingFileAppender appender = new RollingFileAppender();
        appender.setContext(loggerContext);
        TimeBasedRollingPolicy rollingPolicyBase = new TimeBasedRollingPolicy<>();
        rollingPolicyBase.setContext(loggerContext);
        rollingPolicyBase.setParent(appender);
        rollingPolicyBase.setFileNamePattern((String.format("%s/test/test-%s", jobName, jobName) + ".%d{yyyy-MM-dd}.%i.log"));
        SizeAndTimeBasedFNATP sizeAndTimeBasedFNATP = new SizeAndTimeBasedFNATP();
        sizeAndTimeBasedFNATP.setMaxFileSize(FileSize.valueOf("10MB"));
        rollingPolicyBase.setTimeBasedFileNamingAndTriggeringPolicy(sizeAndTimeBasedFNATP);
        rollingPolicyBase.setMaxHistory(10);
        rollingPolicyBase.start();

        appender.setFile("E:\\hulin_workspace\\file-message-server\\rabbitmq-server\\logs\\" + jobName + ".log");
        appender.setEncoder(encoder);
        appender.setRollingPolicy(rollingPolicyBase);
        appender.start();

        logger.setAdditive(false);
        logger.addAppender(appender);
        logger.addAppender(defaultConsoleAppender);

        return logger;
    }
}
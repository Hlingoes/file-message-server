#### Java 程序中使用 Logback，需要依赖三个 jar 包，分别是 slf4j-api，logback-core，logback-classic,在 maven 项目中依赖如下：
````
<!-- springboot项目默认了logback的依赖，无需手动添加 -->
 <dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-api</artifactId>
    <version>1.7.5</version>
</dependency>
<dependency>
    <groupId>ch.qos.logback</groupId>
    <artifactId>logback-core</artifactId>
    <version>1.0.11</version>
</dependency>
<dependency>
    <groupId>ch.qos.logback</groupId>
    <artifactId>logback-classic</artifactId>
    <version>1.0.11</version>
</dependency>
````
##### Logback 在启动时，根据以下步骤寻找配置文件：
>1. 在 classpath 中寻找 logback-test.xml文件
>2. 如果找不到 logback-test.xml，则在 classpath 中寻找 logback.groovy 文件
>3. 如果找不到 logback.groovy，则在 classpath 中寻找 logback.xml文件
>4. 如果上述的文件都找不到，则 logback 会使用 JDK 的 SPI 机制查找 META-INF/services/ch.qos.logback.classic.spi.Configurator 中的 logback 配置实现类，
这个实现类必须实现 Configuration 接口，使用它的实现来进行配置
>5. 如果上述操作都不成功，logback 就会使用它自带的 BasicConfigurator 来配置，并将日志输出到 console
##### logback的变量作用于有三种：local，context，system 
>1. local 作用域在配置文件内有效；
>2. context 作用域的有效范围延伸至 logger context；
>3. system 作用域的范围最广，整个 JVM 内都有效
##### logback 在替换变量时，首先搜索 local 变量，然后搜索 context，然后搜索 system，在spring项目中，应将变量的作用域设置为context，并交给spring控制
````
## application.yml文件配置
spring:
  profiles:
    active: dev
  application:
    name: msg-consumer
logging:
  ## 自定义logback配置文件名，交给spring
  config: classpath:logback-custom.xml
logback:
  ## 在配置文件中指定日志路径
  logHome: logs
````
````
<!--  logback-custom.xml文件配置，放在resources目录下，与application.yml同级 -->
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <!-- logback.xml和logback-test.xml会被logback组件直接读取 -->
    <!-- 如果要交给spring管理，需要修改配置文件名为logback-spring.xml -->
    <!-- springProfile标签可以为不同的环境使用不同的配置，设置scope="context"，则在项目上下文中可以使用该变量 -->
    <springProperty scope="context" name="LOG_HOME" source="logback.logHome" defaultValue="log"/>
    <springProperty scope="context" name="LOG_NAME_PREFIX" source="spring.application.name" defaultValue=""/>
    <!-- %m输出的信息,%p日志级别,%t线程名,%d日期,%c类的全名,%i索引【从数字0开始递增】,,, -->
    <property scope="context" name="pattern" value="%d{yyyy-MM-dd HH:mm:ss} [%thread] %level %logger{35}:%line - %msg%n"/>
    <timestamp scope="context" key="bySecond" datePattern="yyyyMMddHHmmss"/>
    <property scope="context" name="logPath" value="${LOG_HOME}/${LOG_NAME_PREFIX}"/>
    <!-- appender是configuration的子节点，是负责写日志的组件。 -->
    <!-- ch.qos.logback.core.ConsoleAppender：把日志输出到控制台 -->
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <!-- pattern节点，用来设置日志的输入格式 -->
            <pattern>${pattern}</pattern>
            <!-- 记录日志的编码 -->
            <charset>UTF-8</charset>
        </encoder>
    </appender>
    <!-- RollingFileAppender：滚动记录文件，先将日志记录到指定文件，当符合某个条件时，将日志记录到其他文件 -->
    <appender name="ALL" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <File>${logPath}-all.log</File>
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <!-- 活动文件的名字会根据fileNamePattern的值，每隔一段时间改变一次，文件名：logger/sys.2020-03-28.0.logger -->
            <fileNamePattern>${logPath}/${LOG_NAME_PREFIX}-all.%d.%i.log</fileNamePattern>
            <maxFileSize>50MB</maxFileSize>
            <maxHistory>30</maxHistory>
            <totalSizeCap>2GB</totalSizeCap>
            <cleanHistoryOnStart>true</cleanHistoryOnStart>
        </rollingPolicy>
        <encoder>
            <pattern>${pattern}</pattern>
            <charset>UTF-8</charset>
        </encoder>
    </appender>

    <appender name="INFO" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <File>${logPath}-info.log</File>
        <!--只输出INFO-->
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <!--过滤 INFO-->
            <level>INFO</level>
            <!--匹配到就禁止-->
            <onMatch>ACCEPT</onMatch>
            <!--没有匹配到就允许-->
            <onMismatch>DENY</onMismatch>
        </filter>
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <fileNamePattern>${logPath}/${LOG_NAME_PREFIX}-info.%d.%i.log</fileNamePattern>
            <maxFileSize>50MB</maxFileSize>
            <maxHistory>30</maxHistory>
            <totalSizeCap>2GB</totalSizeCap>
            <cleanHistoryOnStart>true</cleanHistoryOnStart>
        </rollingPolicy>
        <encoder>
            <pattern>${pattern}</pattern>
            <charset>UTF-8</charset>
        </encoder>
    </appender>

    <appender name="ERROR" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <File>${logPath}-error.log</File>
        <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
            <!--设置日志级别,过滤掉info日志,只输入error日志-->
            <level>ERROR</level>
        </filter>
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <fileNamePattern>${logPath}/${LOG_NAME_PREFIX}-error.%d.%i.log</fileNamePattern>
            <maxFileSize>50MB</maxFileSize>
            <maxHistory>30</maxHistory>
            <totalSizeCap>2GB</totalSizeCap>
            <cleanHistoryOnStart>true</cleanHistoryOnStart>
        </rollingPolicy>
        <encoder>
            <pattern>${pattern}</pattern>
            <charset>UTF-8</charset>
        </encoder>
    </appender>

    <!-- 记录sql -->
    <appender name="SQL" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <File>${logPath}sql.log</File>
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <fileNamePattern>${logPath}/${LOG_NAME_PREFIX}-sql.%d.%i.log</fileNamePattern>
            <maxFileSize>50MB</maxFileSize>
            <maxHistory>30</maxHistory>
            <totalSizeCap>2GB</totalSizeCap>
            <cleanHistoryOnStart>true</cleanHistoryOnStart>
        </rollingPolicy>
        <encoder>
            <pattern>${pattern}</pattern>
            <charset>UTF-8</charset>
        </encoder>
    </appender>
    
    <!-- 按发布环境，控制激活的日志级别 -->
    <springProfile name="dev,test">
        <!-- 控制台输出日志级别 -->
        <root level="INFO">
            <appender-ref ref="STDOUT"/>
            <appender-ref ref="ALL"/>
        </root>

        <!-- 定项目中某个包,eg：cn.henry.study为根包，也就是只要是发生在这个根包下面的所有日志操作行为的权限都是INFO -->
        <!-- 级别依次为【从高到低】：FATAL > ERROR > WARN > INFO > DEBUG > TRACE  -->
        <logger name="cn.henry.study" level="INFO" additivity="false">
            <appender-ref ref="INFO"/>
            <appender-ref ref="ERROR"/>
            <appender-ref ref="STDOUT"/>
        </logger>
        
        <!-- mybatis loggers 可以按包的层级指定不同的日志级别 -->
        <logger name="cn.henry.study.web.mapper" level="DEBUG" additivity="false">
            <appender-ref ref="SQL"/>
            <appender-ref ref="STDOUT"/>
        </logger>
    </springProfile>

    <springProfile name="pro">
        <root level="INFO">
            <appender-ref ref="SERVICE_ALL"/>
            <appender-ref ref="STDOUT"/>
        </root>
    </springProfile>
</configuration>
````
##### 以上配置可满足日常开发的大部分需求，可以很方便的将info日志与error隔离开，并按照给定logger输出不同配置文件中。
##
#### 存在的问题
>1. 如果需要按照业务，将某些不同包下的日志，集中输出到指定的日志文件中，上述配置就难以实现；
>2. 上述xml文件会产生大量重复配置，如appender和logger的配置，添加非常的繁琐，造成配置文件庞大；
#### 解决方案
> 1. 通过logback的SiftingAppender，通过ThreadLocal的方式动态切换，这个方案在我之前的博客中有详细，与业务耦合较高
> 2. 在java代码中动态生成Appender，轻量，易拓展
````
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
 * @citation https://blog.csdn.net/lw656697752/article/details/84904938
 * @citation https://www.cnblogs.com/leohe/p/12117183.html
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
            // 如果logback配置文件中，已存在窗口输出的appender，则直接使用；不存在则重新生成
            if (appender instanceof ConsoleAppender) {
                defaultConsoleAppender = (ConsoleAppender) appender;
                return;
            }
        });
    }

    /**
     * description: 获取自定义的logger日志，在指定日志文件logNameEnum.getLogName()中输出日志
     * 日志中会包括所有线程及方法堆栈信息
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
        // 读取logback配置文件中的属性值，设置文件名
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
     * @return ch.qos.logback.core.rolling.TimeBasedRollingPolicy
     * @author Hlingoes 2020/6/10
     */
    private static TimeBasedRollingPolicy createRollingPolicy(String name, Level level, LoggerContext context, FileAppender appender) {
        // 读取logback配置文件中的属性值，设置文件名
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
        // 总大小限制
        rollingPolicyBase.setTotalSizeCap(FileSize.valueOf(totalSizeCap));
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

}
````
````
import org.apache.commons.lang3.StringUtils;

/**
 * description: 日志枚举类，防止随意生成日志文件
 *
 * @author Hlingoes 2020/6/10
 */
public enum LogNameEnum {
    COMMON("common"),
    WEB_SERVER("webServer"),
    TEST("test"),
    ;

    private String logName;

    LogNameEnum(String fileName) {
        this.logName = fileName;
    }

    public String getLogName() {
        return logName;
    }

    public void setLogName(String logName) {
        this.logName = logName;
    }

    /**
     * description: 获取枚举类
     *
     * @param value
     * @return cn.henry.study.common.enums.LogNameEnum
     * @author Hlingoes 2020/6/10
     */
    public static LogNameEnum getAwardTypeEnum(String value) {
        LogNameEnum[] arr = values();
        for (LogNameEnum item : arr) {
            if (null != item && StringUtils.isNotBlank(item.logName)) {
                return item;
            }
        }
        return null;
    }
}
````
````
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
    private Logger logger = LoggerFactory.getLogger(PracticeTest.class);

    @Test
    public void loggerUtilsTest() {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        /**
         *  <property scope="context" name="LOG_HOME" value="log"/>
         *  <property scope="context" name="LOG_NAME_PREFIX" value="common"/>
         */
        String oph = OptionHelper.substVars("${LOG_HOME}/${LOG_NAME_PREFIX}/test-log.log", context);
        // 在日志文件common-info.log中
        logger.info("默认配置的日志输出");
        // 在日志文件common-test-info.log中
        LoggerUtils.getLogger(LogNameEnum.TEST, PracticeTest.class).info("#####{}####", oph);
        LoggerUtils.getLogger(LogNameEnum.TEST, LoggerUtils.class).info("看到这条信息就是info");
        // 在日志文件common-test-error.log中
        LoggerUtils.getLogger(LogNameEnum.TEST, PracticeTest.class).error("看到这条信息就是error");
    }

}
输出结果：
2020-06-12 01:40:33 [main] INFO cn.henry.study.common.PracticeTest:24 - 默认配置的日志输出
2020-06-12 01:40:33 [main] INFO cn.henry.study.common.PracticeTest:25 - #####log/common/test-log.log####
2020-06-12 01:40:33 [main] INFO c.h.study.common.utils.LoggerUtils:26 - 看到这条信息就是info
2020-06-12 01:40:33 [main] ERROR cn.henry.study.common.PracticeTest:27 - 看到这条信息就是error
````
##### 代码在本人的git项目[file-message-server](https://github.com/Hlingoes/file-message-server)，汇总大家的实践和想法，共同完善最佳实践
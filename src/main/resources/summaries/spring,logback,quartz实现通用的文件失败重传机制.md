##### 问题背景
######对于频繁的数据采集和上报服务，数据发送端经常会遇到文件上传失败的情况。通常的做法
````
// 失败重试，最多尝试3次
Exception ex = null;
for (int i = 0; i < 3' i++) {
    try {
        service.upload(...);
        break;
    } catch (Exception e) {
        ex = e;
    }
}
if (ex != null) {
    writeFailFile(...);
    throw new RuntimeException（"something is fail", ex）；
}
````
对于服务恰好很长一段时间不可用，比如hbase正在做拆分，节点间歇性异常；FTP服务器磁盘空间不足，连接超限，连接超时等情况。
会存在大量文件失败，失败的临时文件夹中就很多文件，文件数据的重传是个很繁琐的问题。
##### 解决方案
>1. 抽离文件上传的接口(FileService)，指定实现类，方便spring托管；
>2. 文件上传失败后，抛出自定义异常(FailRetryException)，传入文件简介(MessageBrief)信息，便于重传；
>3. 利用AOP切面，代理service中FailRetryException异常，写入临时文件，同时将MessageBrief计入缓存；
>4. quartz定时任务定期拉去缓存队列BlockingQueue中的数据，执行指定class的upload方法；
>5. 利用logback的sift机制，将特定上报服务分日志输出，利用日志回滚机制，将之前失败的日志归类；
>6. 监听spring的启动和结束事件，初始化完成后将之前失败的日志到重新缓存队列中，程序结束前，将缓存队列输出到日志文件；
>7. 打开retry的调用，能够手动触发重传流程，核心处理方法在FileServiceFactory中
````
/**
 * description: 文件服务的通用接口
 */
public interface FileService {
    boolean upload(String rowKey, InputStream inputStream);
    Class<?> getEntityClazz();
}

/**
 * description: 处理通用的文件、消息发送失败的异常
 */
public class FailRetryException extends BaseException {
    public FailRetryException(FileService service, MessageBrief brief) {
        super();
        super.data = brief;
    }
}

/**
 * description: 通过AOP统一处理业务异常
 */
@Component
@Aspect
public class GlobalServiceExceptionAspect {
    public static final Logger LOGGER = LoggerFactory.getLogger(GlobalServiceExceptionAspect.class);

    @Autowired
    private FileServiceFactory fileServiceFactory;

    /**
     * 定义命名的切点
     */
    @Pointcut("execution(* cn.henry.study.service..*.*(..)) " )
    public void methodException() {

    }

    /**
     * 环绕通知方法
     */
    @Around("methodException()")
    public Object watchMethodException(ProceedingJoinPoint pjp) throws Throwable {
        Stopwatch stopwatch = Stopwatch.createStarted();
        Object result = null;
        try {
            result = pjp.proceed(pjp.getArgs());
            Long consumeTime = stopwatch.stop().elapsed(TimeUnit.MILLISECONDS);
            LOGGER.info("耗时: {}ms", consumeTime);
        } catch (FailRetryException ex) {
            fileServiceFactory.cacheFailData((MessageBrief) ex.getData());
            LOGGER.error(pjp.getSignature() + " 接口记录返回结果失败！，原因为：{}", ex.getMessage());
        }
        return result;
    }
}

/**
 * description: 文件服务处理工厂，实现主体逻辑
 */
@Component
public class FileServiceFactory {
    private static Logger logger = LoggerFactory.getLogger(FileServiceFactory.class);

    private int maxFailCacheSize = 1000;

    @Value("${logback.siftLogHome}")
    private String siftLogHome;

    /**
     * 用来缓存实例化的bean服务
     */
    private Map<String, DefaultFileService> serviceCacheMap = new HashMap<>();
    /**
     * 用来缓存发送失败的文件信息
     */
    private Map<String, BlockingQueue<MessageBrief>> failFilesCacheMap = new HashMap<>();

    @EventListener
    public void event(ApplicationReadyEvent event) {
        logger.info("the active profile: {}", event.getApplicationContext().getEnvironment().getActiveProfiles()[0]);
        Map<String, DefaultFileService> map = event.getApplicationContext().getBeansOfType(DefaultFileService.class);
        map.forEach((key, value) -> {
            if (null != value.getEntityClazz()) {
                String className = value.getEntityClazz().getSimpleName();
                this.serviceCacheMap.put(className, value);
                BlockingQueue<MessageBrief> failDataQueue = new LinkedBlockingQueue<>(this.maxFailCacheSize);
                String logName = className + HeaderConstants.SIFT_LOG_PREFIX;
                this.failFilesCacheMap.put(logName, failDataQueue);
                String logPath = logName + HeaderConstants.SIFT_LOG_SUFFIX;
                File preFailLog = FileUtils.getFile(MessageBrief.jarHome, this.siftLogHome, logPath);
                if (preFailLog.exists()) {
                    this.cacheFailData(preFailLog, logName);
                }
            }
        });
        this.serviceCacheMap.forEach((key, value) -> logger.info("key: {}, value: {}", key, value.getClass()));
        this.failFilesCacheMap.forEach((key, value) -> value.forEach(messageBrief -> logger.info("{}", messageBrief)));
    }

    @EventListener
    public void event(ContextClosedEvent event) {
        logger.info("application is closing", event.getApplicationContext().getEnvironment().getActiveProfiles()[0]);
        // 取出此次失败的缓存数据
        this.failFilesCacheMap.forEach((key, value) -> {
            List<MessageBrief> list = new ArrayList<>(this.maxFailCacheSize);
            value.drainTo(list);
            list.forEach(brief -> brief.writeRetryLog());
        });
    }

    /**
     * description: 将失败的数据写入文件，将数据简介写入缓存
     */
    public void cacheFailData(MessageBrief brief) {
        if (this.failFilesCacheMap.containsKey(brief.getLogName())) {
            if (!this.failFilesCacheMap.get(brief.getLogName()).offer(brief)) {
                brief.writeRetryLog();
            }
        }
    }

    /**
     * description: 获取实例化bean
     */
    public DefaultFileService getService(String clazz) {
        return this.serviceCacheMap.get(clazz);
    }

    /**
     * description: 将之前的失败日志读取到队列中
     */
    private void cacheFailData(File activeFile, String logName) {
        try {
            InputStreamReader read = new InputStreamReader(new FileInputStream(activeFile));
            BufferedReader bufferedReader = new BufferedReader(read);
            while (bufferedReader.ready()) {
                String line = bufferedReader.readLine();
                String messageBrief = StringUtils.substringAfter(line, MessageBrief.briefMark());
                if (StringUtils.isNotEmpty(messageBrief)) {
                    MessageBrief brief = JacksonUtils.str2Bean(messageBrief, MessageBrief.class);
                    File retryFile = new File(brief.getRetryPath());
                    if (retryFile.exists()) {
                        this.cacheFailData(brief);
                        logger.info("读取日志到队列, File: {}", activeFile.getAbsolutePath());
                    }
                }
            }
            bufferedReader.close();
            read.close();
        } catch (FileNotFoundException e) {
            logger.info("File: {}, 不存在", activeFile.getAbsolutePath());
        } catch (IOException e) {
            logger.info("读取日志到队列失败, File: {}", activeFile.getAbsolutePath(), e);
        }
    }

    /**
     * description: 重新上传文件，每次取出50个
     *
     * @param
     * @return void
     * @author Hlingoes 2020/3/29
     */
    public void reUploadFiles() {
        logger.info("开始失败重传任务");
        this.failFilesCacheMap.forEach((key, value) -> {
            List<MessageBrief> list = new ArrayList<>(50);
            value.drainTo(list);
            list.forEach(brief -> {
                String className = StringUtils.substringBefore(brief.getLogName(), HeaderConstants.SIFT_LOG_PREFIX);
                File retryFile = new File(brief.getRetryPath());
                if (retryFile.exists()) {
                    this.serviceCacheMap.get(className).upload(brief.getRowKey(), retryFile);
                    retryFile.delete();
                    logger.info("上传完成: {}, 删除文件: {}", brief, retryFile.getAbsolutePath());
                }
            });
        });
    }
}

/**
 * description: 文件或消息简介
 */
public class MessageBrief {
    private static Logger logger = LoggerFactory.getLogger(MessageBrief.class);

    public static File jarHome = new ApplicationHome(MessageBrief.class).getDir();

    public static String retryDir = "fail_upload_files";

    private String rowKey;

    private String logName;

    private String retryPath;

    /**
     * Jackson反序列化需要无参构造器
     */
    public MessageBrief() {

    }

    public MessageBrief(String rowKey, String logName) {
        this.rowKey = rowKey;
        this.logName = logName;
        this.retryPath = this.tempFile().getAbsolutePath();
    }

    public MessageBrief(Class clazz, String rowKey, InputStream inputStream) {
        this.rowKey = rowKey;
        this.logName = clazz.getSimpleName() + HeaderConstants.SIFT_LOG_PREFIX;
        this.retryPath = this.tempFile().getAbsolutePath();
        writeTempFile(inputStream);
    }

    /**
     * description: 将文件写入临时文件夹
     */
    public void writeTempFile(InputStream inputStream) {
        File file = this.tempFile();
        // 同一文件，多次失败，不需要重复写入
        if (file.exists()) {
            return;
        }
        try {
            FileUtils.writeByteArrayToFile(file, IOUtils.toByteArray(inputStream));
        } catch (IOException e) {
            logger.info("写临时文件失败: {}", this, e);
        }
    }

    /**
     * description: logback.xml中discriminator根据siftLogName这个key的value来决定
     * siftLogName的value通过这种方式设置， 这里设置的key-value对是保存在一个ThreadLocal<Map>中
     * 不会对其他线程中的siftLogName这个key产生影响
     */
    public void writeRetryLog() {
        MDC.put("siftLogName", this.logName);
        logger.error("{}{}", briefMark(), JacksonUtils.object2Str(this));
        // remember remove MDC
        MDC.remove(this.logName);
    }

    /**
     * description: 获取临时写入文件
     */
    public File tempFile() {
        String fileName = StringUtils.substringAfterLast(this.rowKey, "/");
        return FileUtils.getFile(jarHome, retryDir, fileName);
    }

    /**
     * description: 日志标记
     */
    public static String briefMark() {
        return MessageBrief.class.getSimpleName() + HeaderConstants.SIFT_LOG_PREFIX;
    }
}

/**
 * description: 触发器(FailuresRetryTriggeringPolicy)会在SIFT Appender第一次执行的时候，触发logback回滚策略，重命名文件
 * 加上flags，让触发器只在初始化后仅被触发一次
 */
public class FailuresRetryTriggeringPolicy<E> extends TriggeringPolicyBase<E> {
    private static Logger logger = LoggerFactory.getLogger(FailuresRetryTriggeringPolicy.class);

    private static List<String> flags = new ArrayList<>();

    @Override
    public boolean isTriggeringEvent(File activeFile, E event) {
        String logName = StringUtils.substringBeforeLast(activeFile.getName(), ".");
        if (!flags.contains(logName)) {
            flags.add(logName);
            logger.info("初始化执行回滚策略，将之前的失败日志读取到队列中, File: {}", activeFile.getAbsolutePath());
            return true;
        }
        return false;
    }
}

/**
 * description: spring-logback.xml的主配置
 */
 <?xml version="1.0" encoding="UTF-8"?>
 <configuration>
 
     <!-- logback.xml和logback-test.xml会被logback组件直接读取 -->
     <!-- 如果要交给spring管理，需要修改配置文件名为logback-spring.xml -->
     <!-- springProfile标签可以为不同的环境使用不同的配置 -->
 
     <springProperty scope="context" name="LOG_HOME" source="logback.logHome" defaultValue="log"/>
     <springProperty scope="context" name="SIFT_LOG_HOME" source="logback.siftLogHome" defaultValue="sift"/>
 
     <!-- %m输出的信息,%p日志级别,%t线程名,%d日期,%c类的全名,%i索引【从数字0开始递增】,,, -->
     <property name="pattern" value="%d{yyyy-MM-dd HH:mm:ss} [%thread] %level %logger{35} - %msg%n"/>
     <timestamp key="bySecond" datePattern="yyyyMMddHHmmss"/>
 
     <!-- appender是configuration的子节点，是负责写日志的组件。 -->
     <!-- ch.qos.logback.core.ConsoleAppender：把日志输出到控制台 -->
     <!-- 打印到控制台 -->
     <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
         <encoder>
             <!-- pattern节点，用来设置日志的输入格式 -->
             <pattern>${pattern}</pattern>
             <!-- 记录日志的编码 -->
             <charset>UTF-8</charset>
         </encoder>
     </appender>
 
     <!-- RollingFileAppender：滚动记录文件，先将日志记录到指定文件，当符合某个条件时，将日志记录到其他文件 -->
     <!-- 打印所有日志，保存到文件-->
     <appender name="FILE_ALL" class="ch.qos.logback.core.rolling.RollingFileAppender">
         <File>${LOG_HOME}/service_all.log</File>
         <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
             <!-- 活动文件的名字会根据fileNamePattern的值，每隔一段时间改变一次，文件名：log/sys.2020-03-28.0.log -->
             <fileNamePattern>${LOG_HOME}/service_all.%d.%i.log</fileNamePattern>
             <maxHistory>30</maxHistory>
             <timeBasedFileNamingAndTriggeringPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
                 <!-- maxFileSize:这是活动文件的大小，默认值是10MB -->
                 <maxFileSize>50MB</maxFileSize>
             </timeBasedFileNamingAndTriggeringPolicy>
         </rollingPolicy>
         <encoder>
             <!-- pattern节点，用来设置日志的输入格式 -->
             <pattern>${pattern}</pattern>
             <!-- 记录日志的编码 -->
             <charset>UTF-8</charset>
         </encoder>
     </appender>
 
     <!-- RollingFileAppender：滚动记录文件，先将日志记录到指定文件，当符合某个条件时，将日志记录到其他文件 -->
     <appender name="FILE_SERVICE" class="ch.qos.logback.core.rolling.RollingFileAppender">
         <File>${LOG_HOME}/fileService.log</File>
         <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
             <!-- 活动文件的名字会根据fileNamePattern的值，每隔一段时间改变一次，文件名：log/sys.2020-03-28.0.log -->
             <fileNamePattern>${LOG_HOME}/fileService.%d.%i.log</fileNamePattern>
             <maxHistory>30</maxHistory>
             <timeBasedFileNamingAndTriggeringPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
                 <!-- maxFileSize:这是活动文件的大小，默认值是10MB -->
                 <maxFileSize>50MB</maxFileSize>
             </timeBasedFileNamingAndTriggeringPolicy>
         </rollingPolicy>
         <encoder>
             <!-- pattern节点，用来设置日志的输入格式 -->
             <pattern>${pattern}</pattern>
             <!-- 记录日志的编码 -->
             <charset>UTF-8</charset>
         </encoder>
     </appender>
 
     <!-- 自定义的筛选日志 -->
     <appender name="SIFT" class="ch.qos.logback.classic.sift.SiftingAppender">
         <!--discriminator鉴别器，根据siftLogName这个key对应的value鉴别日志事件，然后委托给具体appender写日志 -->
         <discriminator>
             <!-- 代码使用使用MDC.put("siftLogName",value)即可 -->
             <key>siftLogName</key>
             <defaultValue>default</defaultValue>
         </discriminator>
         <sift>
             <!--具体的写日志appender，每一个siftLogName创建一个文件 -->
             <appender name="FILE_${siftLogName}" class="ch.qos.logback.core.rolling.RollingFileAppender">
                 <file>${SIFT_LOG_HOME}/${siftLogName}.log</file>
                 <append>true</append>
                 <encoder charset="UTF-8">
                     <pattern>${pattern}</pattern>
                 </encoder>
                 <!-- 自定义的日志滚动命名，配合trigger使用 -->
                 <rollingPolicy class="ch.qos.logback.core.rolling.FixedWindowRollingPolicy">
                     <FileNamePattern>${SIFT_LOG_HOME}/${siftLogName}.${bySecond}.%i.log</FileNamePattern>
                     <MinIndex>1</MinIndex>
                     <MaxIndex>10</MaxIndex>
                 </rollingPolicy>
                 <!-- 自定义的日志滚动触发器 -->
                 <triggeringPolicy class="cn.henry.study.logback.FailuresRetryTriggeringPolicy">
                 </triggeringPolicy>
             </appender>
         </sift>
     </appender>
 
 
     <!-- 记录sql -->
     <appender name="SQL" class="ch.qos.logback.core.rolling.RollingFileAppender">
         <File>${LOG_HOME}/sql.log</File>
         <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
             <!-- 活动文件的名字会根据fileNamePattern的值，每隔一段时间改变一次，文件名：log/sys.2020-03-28.0.log -->
             <fileNamePattern>${LOG_HOME}/sql.%d.%i.log</fileNamePattern>
             <maxHistory>30</maxHistory>
             <timeBasedFileNamingAndTriggeringPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
                 <!-- maxFileSize:这是活动文件的大小，默认值是10MB -->
                 <maxFileSize>50MB</maxFileSize>
             </timeBasedFileNamingAndTriggeringPolicy>
         </rollingPolicy>
         <encoder>
             <!-- pattern节点，用来设置日志的输入格式 -->
             <pattern>${pattern}</pattern>
             <!-- 记录日志的编码 -->
             <charset>UTF-8</charset>
         </encoder>
     </appender>
 
     <!--    <springProfile name="dev">-->
     <!--        &lt;!&ndash; 开发环境时激活 &ndash;&gt;-->
     <!--    </springProfile>-->
 
     <!--    <springProfile name="dev,test">-->
     <!--        &lt;!&ndash; 开发，测试的时候激活&ndash;&gt;-->
     <!--    </springProfile>-->
 
     <!--    <springProfile name="!prod">-->
     <!--        &lt;!&ndash; 当 "生产" 环境时，该配置不激活&ndash;&gt;-->
     <!--    </springProfile>-->
     <springProfile name="dev,test">
         <!-- 控制台输出日志级别 -->
         <root level="DEBUG">
             <appender-ref ref="STDOUT"/>
             <appender-ref ref="FILE_ALL"/>
         </root>
 
         <!-- mybatis loggers -->
         <logger name="cn.henry.study.mapper" level="DEBUG" additivity="false">
             <appender-ref ref="SQL"/>
         </logger>
 
         <!-- 指定项目中某个包，当有日志操作行为时的日志记录级别 -->
         <!-- com.henry为根包，也就是只要是发生在这个根包下面的所有日志操作行为的权限都是DEBUG -->
         <!-- 级别依次为【从高到低】：FATAL > ERROR > WARN > INFO > DEBUG > TRACE  -->
         <logger name="cn.henry.study" level="INFO" additivity="false">
             <appender-ref ref="FILE_SERVICE"/>
         </logger>
 
         <!-- 发送文件失败的动态日志 -->
         <logger name="cn.henry.study.entity.MessageBrief" level="ERROR" additivity="false">
             <appender-ref ref="SIFT"/>
         </logger>
     </springProfile>
 
     <springProfile name="pro">
         <root level="INFO">
             <appender-ref ref="FILE"/>
             <appender-ref ref="STDOUT"/>
         </root>
     </springProfile>
 </configuration>
 
 ---
 生成的主体日志，eg：
 FTPClient_fail_retry.20200330002121.1.log
 FTPClient_fail_retry.log
 HbaseService_fail_retry.20200330002121.1.log
 HbaseService_fail_retry.log
 HttpService_fail_retry.20200330002123.1.log
 HttpService_fail_retry.log
 ---
````
###### 重点关注的部分
>#####1. 统一的业务处理逻辑，提取为接口，利用spring的特性注册bean，获取bean实例
>#####2. 采用AOP可以很好的聚合同类型问题，面向切面和接口实现业务逻辑
>#####3. 使用logback日志框架的MDC特性，实现日志按照指定的key动态生成独立的日志文件
>#####4. 使用logback日状况的TriggeringPolicy，自定义日志滚动，规避数据的重复写入
>#####5. spring-boot-starter-quartz集成处理定时任务，减少了很多模板代码，更易用
---
######代码详情在github中 [Hlingoes/file-message-server](https://github.com/Hlingoes/file-message-server)
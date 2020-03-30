package cn.henry.study.base;

import cn.henry.study.constants.HeaderConstants;
import cn.henry.study.entity.MessageBrief;
import cn.henry.study.utils.JacksonUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * description: 文件服务处理工厂，实现主体逻辑
 *
 * @author Hlingoes
 * @date 2019/12/21 18:27
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
     *
     * @param brief
     * @return void
     * @author Hlingoes 2020/3/27
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
     *
     * @param
     * @return cn.henry.study.base.DefaultFileService
     * @author Hlingoes 2020/3/27
     */
    public DefaultFileService getService(String clazz) {
        return this.serviceCacheMap.get(clazz);
    }

    /**
     * description: 将之前的失败日志读取到队列中
     *
     * @param activeFile
     * @param logName
     * @return void
     * @author Hlingoes 2020/3/28
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

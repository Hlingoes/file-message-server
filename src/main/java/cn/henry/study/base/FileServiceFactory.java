package cn.henry.study.base;

import cn.henry.study.constants.HeaderConstants;
import cn.henry.study.entity.MessageBrief;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * description:
 *
 * @author Hlingoes
 * @date 2019/12/21 18:27
 */
@Component
public class FileServiceFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileServiceFactory.class);

    private int failDataSize = 100;
    private int failRetryDataSize = 1000;

    /**
     * 用来缓存实例化的bean服务
     */
    private Map<String, DefaultFileService> serviceCacheMap = new HashMap<>();
    /**
     * 用来缓存发送失败的文件信息
     */
    private Map<String, BlockingQueue<MessageBrief>> currentCacheMap = new HashMap<>();
    /**
     * 初始化获取之前上传失败的文件信息
     */
    private Map<String, BlockingQueue<MessageBrief>> preCacheMap = new HashMap<>();

    @EventListener
    public void event(ApplicationReadyEvent event) {
        LOGGER.info("the active profile: {}", event.getApplicationContext().getEnvironment().getActiveProfiles()[0]);
        Map<String, DefaultFileService> map = event.getApplicationContext().getBeansOfType(DefaultFileService.class);
        map.forEach((key, value) -> {
            if (null != value.getEntityClazz()) {
                String className = value.getEntityClazz().getSimpleName();
                this.serviceCacheMap.put(className, value);
                BlockingQueue<MessageBrief> failDataQueue = new LinkedBlockingQueue<>(this.failDataSize);
                BlockingQueue<MessageBrief> failRetryDataQueue = new LinkedBlockingQueue<>(this.failRetryDataSize);
                String logName = className + HeaderConstants.DATA_RETRY_SUFFIX;
                this.currentCacheMap.put(logName, failDataQueue);
                this.preCacheMap.put(logName, failRetryDataQueue);
            }
        });
        this.serviceCacheMap.forEach((key, value) -> LOGGER.info("key: {}, value: {}", key, value.getClass()));
        this.currentCacheMap.forEach((key, value) -> LOGGER.info("key: {}, valve: {}", key, value.getClass()));
        this.preCacheMap.forEach((key, value) -> LOGGER.info("key: {}, valve: {}", key, value.getClass()));
    }

    @EventListener
    public void event(ContextClosedEvent event) {
        LOGGER.info("application is closing", event.getApplicationContext().getEnvironment().getActiveProfiles()[0]);
        // 取出此次失败的缓存数据
        this.currentCacheMap.forEach((key, value) -> {
            List<MessageBrief> list = new ArrayList<>(this.failDataSize);
            value.drainTo(list);
            list.forEach(retryMessage -> retryMessage.writeRetryLog());
        });
        // 取出之前失败的，可能未消费完的缓存数据
        this.preCacheMap.forEach((key, value) -> {
            // 取出缓存的所有数据
            List<MessageBrief> list = new ArrayList<>(this.failRetryDataSize);
            value.drainTo(list);
            list.forEach(retryMessage -> retryMessage.writeRetryLog());
        });
    }

    /**
     * description: 将失败的数据写入文件，将数据简介写入缓存
     *
     * @param retryMessage
     * @return void
     * @author Hlingoes 2020/3/27
     */
    public void cacheFailData(RetryMessage retryMessage) {
        retryMessage.writeTempFile();
        String key = retryMessage.getMessageBrief().getLogName();
        if (this.currentCacheMap.containsKey(key)) {
            if (!this.currentCacheMap.get(key).offer(retryMessage.getMessageBrief())) {
                retryMessage.writeRetryLog();
            }
        }
    }

    /**
     * description: 将失败的文件参数写入缓存
     *
     * @param clazz
     * @param brief
     * @return void
     * @author Hlingoes 2020/3/27
     */
    public void cacheFailData(String clazz, MessageBrief brief) {
        String key = clazz + HeaderConstants.DATA_RETRY_SUFFIX;
        if (this.currentCacheMap.containsKey(key)) {
            if (!this.currentCacheMap.get(key).offer(brief)) {
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

}

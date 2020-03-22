package cn.henry.study.base;

import cn.henry.study.constants.HeaderConstants;
import com.alibaba.fastjson.JSONObject;
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
    private Map<Class, DefaultFileService> massFactory = new HashMap<>();

    private int failDataSize = 200;
    private int failRetryDataSize = 1000;
    /**
     * 用来缓存发送失败的文件信息
     */
    private Map<String, BlockingQueue<JSONObject>> failDataCacheMap = new HashMap<>();
    /**
     * 初始化获取之前上传失败的文件信息
     */
    private Map<String, BlockingQueue<JSONObject>> failRetryDataCacheMap = new HashMap<>();

    @EventListener
    public void event(ApplicationReadyEvent event) {
        LOGGER.info("the active profile: {}", event.getApplicationContext().getEnvironment().getActiveProfiles()[0]);
        Map<String, DefaultFileService> map = event.getApplicationContext().getBeansOfType(DefaultFileService.class);
        map.forEach((key, value) -> {
            if (null != value.getEntityClazz()) {
                String logName = value.getEntityClazz().getSimpleName() + HeaderConstants.DATA_RETRY_SUFFIX;
                massFactory.put(value.getEntityClazz(), value);
                BlockingQueue<JSONObject> failDataQueue = new LinkedBlockingQueue<>(failDataSize);
                BlockingQueue<JSONObject> failRetryDataQueue = new LinkedBlockingQueue<>(failRetryDataSize);
                failDataCacheMap.put(logName, failDataQueue);
                failRetryDataCacheMap.put(logName, failRetryDataQueue);
            }
        });
        massFactory.forEach((key, value) -> LOGGER.info("key: {}, value: {}", key, value.getClass()));
        failDataCacheMap.forEach((key, value) -> LOGGER.info("key: {}, valve: {}", key, value.getClass()));
        failRetryDataCacheMap.forEach((key, value) -> LOGGER.info("key: {}, valve: {}", key, value.getClass()));
    }

    @EventListener
    public void event(ContextClosedEvent event) {
        LOGGER.info("application is closing", event.getApplicationContext().getEnvironment().getActiveProfiles()[0]);
        // 取出此次失败的缓存数据
        failDataCacheMap.forEach((key, value) -> {
            List<JSONObject> list = new ArrayList<>(failDataSize);
            value.drainTo(list);
            list.forEach(jsonObject -> LoggerFactory.getLogger(key).info("{}", value.poll()));
        });
        // 取出之前失败的，可能未消费玩的缓存数据
        failRetryDataCacheMap.forEach((key, value) -> {
            // 取出缓存的所有数据
            List<JSONObject> list = new ArrayList<>(failRetryDataSize);
            value.drainTo(list);
            list.forEach(jsonObject -> LoggerFactory.getLogger(key).info("{}", value.poll()));
        });
    }

}

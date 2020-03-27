package cn.henry.study.logback;

import ch.qos.logback.core.rolling.TriggeringPolicyBase;
import cn.henry.study.base.FileServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * description: 每次应用程序启动时，将之前的失败日志读取到队列中，利用logback回滚策略，重命名文件，删除旧文件
 * 程序只在初始化启动的时候执行一次
 *
 * @author Hlingoes
 * @date 2020/3/22 14:13
 */
@Component
public class DataSendRetryTriggeringPolicy<E> extends TriggeringPolicyBase<E> {
    private static Logger logger = LoggerFactory.getLogger(DataSendRetryTriggeringPolicy.class);

    @Autowired
    private FileServiceFactory serviceFactory;

    @Override
    public boolean isTriggeringEvent(File activeFile, E event) {
        // 将服务sevice服务的日志写入到缓存的cacheMap中
        return false;
    }
}

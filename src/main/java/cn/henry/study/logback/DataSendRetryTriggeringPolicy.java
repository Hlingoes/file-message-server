package cn.henry.study.logback;

import ch.qos.logback.core.rolling.TriggeringPolicyBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * description: 每次应用程序启动时，将之前的失败日志读取到队列中，重新生成一次日志文件
 *
 * @author Hlingoes
 * @date 2020/3/22 14:13
 */
public class DataSendRetryTriggeringPolicy<E> extends TriggeringPolicyBase<E> {

    private static Logger logger = LoggerFactory.getLogger(DataSendRetryTriggeringPolicy.class);

    @Override
    public boolean isTriggeringEvent(File activeFile, E event) {
        // 将服务sevice服务的日志写入到缓存的cacheMap中
        return false;
    }
}

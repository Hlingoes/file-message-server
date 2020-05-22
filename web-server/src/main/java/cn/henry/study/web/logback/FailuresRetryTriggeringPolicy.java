package cn.henry.study.web.logback;

import ch.qos.logback.core.rolling.TriggeringPolicyBase;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * description: 触发器(FailuresRetryTriggeringPolicy)会在SIFT Appender第一次执行的时候，触发logback回滚策略，重命名文件
 * 加上flags，让触发器只在初始化后仅被触发一次
 *
 * @author Hlingoes
 * @date 2020/3/22 14:13
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

package cn.henry.study.web.entity;

import cn.henry.study.common.utils.FileHelpUtils;
import cn.henry.study.common.utils.JacksonUtils;
import cn.henry.study.web.constants.HeaderConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.File;

/**
 * description: 文件或消息简介
 *
 * @author Hlingoes
 * @date 2020/3/27 22:20
 */
public class MessageBrief {
    private static Logger logger = LoggerFactory.getLogger(MessageBrief.class);

    private String rowKey;

    private String logName;

    private File retryFile;

    /**
     * Jackson反序列化需要无参构造器
     */
    public MessageBrief() {

    }

    public MessageBrief(String rowKey, String logName) {
        this.rowKey = rowKey;
        this.logName = logName;
        this.retryFile = FileHelpUtils.findTempleFile(this.rowKey);
    }

    public MessageBrief(Class clazz, String rowKey, byte[] bytes) {
        this.rowKey = rowKey;
        this.logName = clazz.getSimpleName() + HeaderConstants.SIFT_LOG_PREFIX;
        this.retryFile = FileHelpUtils.findTempleFile(this.rowKey);
        FileHelpUtils.writeTempFile(this.retryFile, bytes);
    }

    /**
     * description: logback.xml中discriminator根据siftLogName这个key的value来决定
     * siftLogName的value通过这种方式设置， 这里设置的key-value对是保存在一个ThreadLocal<Map>中
     * 不会对其他线程中的siftLogName这个key产生影响
     *
     * @param
     * @return void
     * @author Hlingoes 2020/3/27
     */
    public void writeRetryLog() {
        MDC.put("siftLogName", this.logName);
        logger.error("{}{}", briefMark(), JacksonUtils.object2Str(this));
        // remember removeRouteKey MDC
        MDC.remove(this.logName);
    }

    /**
     * description: 日志标记
     *
     * @param
     * @return java.lang.String
     * @author Hlingoes 2020/3/28
     */
    public static String briefMark() {
        return MessageBrief.class.getSimpleName() + HeaderConstants.SIFT_LOG_PREFIX;
    }

    public String getRowKey() {
        return rowKey;
    }

    public void setRowKey(String rowKey) {
        this.rowKey = rowKey;
    }

    public String getLogName() {
        return logName;
    }

    public void setLogName(String logName) {
        this.logName = logName;
    }

    public File getRetryFile() {
        return retryFile;
    }

    public void setRetryFile(File retryFile) {
        this.retryFile = retryFile;
    }

    @Override
    public String toString() {
        return "MessageBrief{" +
                "rowKey='" + rowKey + '\'' +
                ", logName='" + logName + '\'' +
                ", retryFile=" + retryFile +
                '}';
    }
}

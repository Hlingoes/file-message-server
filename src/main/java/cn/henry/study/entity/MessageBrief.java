package cn.henry.study.entity;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.boot.system.ApplicationHome;

import java.io.File;

/**
 * description: 文件或消息简介
 *
 * @author Hlingoes
 * @date 2020/3/27 22:20
 */
public class MessageBrief {
    private static Logger logger = LoggerFactory.getLogger(MessageBrief.class);

    private String retryDir = "fail_upload_files";

    private String rowKey;

    private String logName;

    private String retryPath;

    public MessageBrief(String rowKey, String logName) {
        this.rowKey = rowKey;
        this.logName = logName;
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
        logger.error("{}", this.toString());
        // remember remove MDC
        MDC.remove(this.logName);
    }

    /**
     * description: 获取临时写入文件
     *
     * @param
     * @return void
     * @author Hlingoes 2020/3/26
     */
    public File getTempFile() {
        String fileName = StringUtils.substringAfterLast(this.getRowKey(), "/");
        return FileUtils.getFile(this.getBaseJarDir(), this.retryDir, fileName);
    }

    /**
     * 获取当前jar包所在系统中的目录
     */
    private File getBaseJarDir() {
        return new ApplicationHome(getClass()).getDir();
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

    public String getRetryPath() {
        return retryPath;
    }

    public void setRetryPath(String retryPath) {
        this.retryPath = retryPath;
    }

    @Override
    public String toString() {
        return "MessageBrief{" +
                "rowKey='" + rowKey + '\'' +
                ", retryPath='" + retryPath + '\'' +
                '}';
    }
}

package cn.henry.study.entity;

import cn.henry.study.base.DefaultFileService;
import cn.henry.study.constants.HeaderConstants;
import cn.henry.study.utils.JacksonUtils;
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

    public MessageBrief(String logName) {
        this.logName = logName;
    }

    public MessageBrief(String rowKey, String logName) {
        this.rowKey = rowKey;
        this.logName = logName;
    }

    public MessageBrief(String rowKey, DefaultFileService fileService) {
        this.rowKey = rowKey;
        this.retryPath = this.tempFile().getAbsolutePath();
        this.logName = fileService.getEntityClazz().getSimpleName() + HeaderConstants.SIFT_LOG_PREFIX;
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
        String brief = this.briefMark() + JacksonUtils.object2Str(this);
        logger.error("{}", brief);
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
    public File tempFile() {
        String fileName = StringUtils.substringAfterLast(this.getRowKey(), "/");
        return FileUtils.getFile(jarHome, retryDir, fileName);
    }

    /**
     * description: 日志标记
     *
     * @param
     * @return java.lang.String
     * @author Hlingoes 2020/3/28
     */
    public String briefMark() {
        return this.getClass().getSimpleName() + HeaderConstants.SIFT_LOG_PREFIX;
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

}

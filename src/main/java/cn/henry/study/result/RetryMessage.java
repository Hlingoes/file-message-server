package cn.henry.study.result;

import cn.henry.study.base.DefaultFileService;
import cn.henry.study.constants.HeaderConstants;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.boot.system.ApplicationHome;

import java.io.File;
import java.io.IOException;

/**
 * description: 上传失败的文件参数
 *
 * @author Hlingoes
 * @date 2020/3/26 23:36
 */
public class RetryMessage {
    private static Logger logger = LoggerFactory.getLogger(RetryMessage.class);

    private DefaultFileService fileService;
    private String rowKey;
    private byte[] content;
    private String retryPath;

    public RetryMessage(DefaultFileService fileService, String rowKey, byte[] content) {
        this.fileService = fileService;
        this.rowKey = rowKey;
        this.content = content;
    }

    public String getLogName() {
        return this.fileService.getEntityClazz() + HeaderConstants.DATA_RETRY_SUFFIX;
    }

    /**
     * description: 将文件写入临时文件夹
     *
     * @param
     * @return void
     * @author Hlingoes 2020/3/26
     */
    public void writeTempFile() {
        String fileName = StringUtils.substringAfterLast(this.getRowKey(), "/");
        File tempFile = FileUtils.getFile(getBaseJarDir(), fileName);
        this.setRetryPath(tempFile.getAbsolutePath());
        try {
            FileUtils.writeByteArrayToFile(tempFile, this.getContent());
        } catch (IOException e) {
            logger.info("写临时文件失败: {}", this, e);
        }
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
        String logName = getLogName();
        MDC.put("siftLogName", logName);
        logger.error("{}", rowKey);
        // remember remove MDC
        MDC.remove(logName);
    }

    /**
     * 获取当前jar包所在系统中的目录
     */
    public File getBaseJarDir() {
        return new ApplicationHome(getClass()).getDir();
    }

    public String getRowKey() {
        return rowKey;
    }

    public void setRowKey(String rowKey) {
        this.rowKey = rowKey;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getRetryPath() {
        return retryPath;
    }

    public void setRetryPath(String retryPath) {
        this.retryPath = retryPath;
    }

}

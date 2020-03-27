package cn.henry.study.base;

import cn.henry.study.constants.HeaderConstants;
import cn.henry.study.entity.MessageBrief;
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

    private byte[] content;

    private MessageBrief messageBrief;

    public RetryMessage(DefaultFileService fileService, String rowKey, byte[] content) {
        this.fileService = fileService;
        this.messageBrief = new MessageBrief(rowKey, fileService.getEntityClazz() + HeaderConstants.DATA_RETRY_SUFFIX);
        this.content = content;
    }

    /**
     * 将文件简述写入日志
     */
    public void writeRetryLog() {
        this.messageBrief.writeRetryLog();
    }

    /**
     * description: 将文件写入临时文件夹
     *
     * @param
     * @return void
     * @author Hlingoes 2020/3/26
     */
    public void writeTempFile() {
        try {
            FileUtils.writeByteArrayToFile(this.messageBrief.getTempFile(), this.content);
        } catch (IOException e) {
            logger.info("写临时文件失败: {}", this, e);
        }
    }

    /**
     * 获取当前jar包所在系统中的目录
     */
    private File getBaseJarDir() {
        return new ApplicationHome(getClass()).getDir();
    }

    public MessageBrief getMessageBrief() {
        return messageBrief;
    }
}

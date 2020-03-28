package cn.henry.study.base;

import cn.henry.study.entity.MessageBrief;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.system.ApplicationHome;

import java.io.File;
import java.io.IOException;

/**
 * description: 上传失败的文件参数
 *
 * @author Hlingoes
 * @date 2020/3/26 23:36
 */
public class RetryService {
    private static Logger logger = LoggerFactory.getLogger(RetryService.class);

    private DefaultFileService fileService;

    private byte[] content;

    private MessageBrief messageBrief;

    public RetryService(DefaultFileService fileService, String rowKey, byte[] content) {
        this.fileService = fileService;
        this.messageBrief = new MessageBrief(rowKey, fileService);
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
            File file = this.messageBrief.tempFile();
            if (!file.exists()) {
                // 同一文件，多次失败，不需要重复写入
                FileUtils.writeByteArrayToFile(file, this.content);
            }
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

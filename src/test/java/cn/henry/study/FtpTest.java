package cn.henry.study;

import cn.henry.study.appication.FtpService;
import cn.henry.study.entity.Book;
import cn.henry.study.utils.JacksonUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

/**
 * description: FTP测试用例
 *
 * @author Hlingoes
 * @date 2019/12/22 23:52
 */
@SpringBootTest(classes = FileMessageServer.class)
@RunWith(SpringRunner.class)
public class FtpTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(FtpTest.class);

    @Autowired
    private FtpService ftpService;

    @Test
    public void testDownload() {
        ftpService.downloadFile("/html", "cat_meme.html", "G:\\迅雷下载\\test.html");
    }
}

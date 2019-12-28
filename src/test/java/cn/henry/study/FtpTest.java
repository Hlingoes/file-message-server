package cn.henry.study;

import cn.henry.study.appication.FtpService;
import cn.henry.study.pool.CustomThreadFactoryBuilder;
import cn.henry.study.pool.HttpClientDownloadPool;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.util.concurrent.*;

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
        ftpService.download("/html", "cat_meme.html", "G:\\迅雷下载");
    }

    public void testUpload() {
        File dir = new File("G:\\下载");
        File[] files = dir.listFiles();
        String path = "/资料/bak/";
        for (File file : files) {
            ftpService.upload(path, file.getName(), file);
        }
    }

    @Test
    public void  testDelete(){
        String path = "/资料/bak/黄山山名由来及其文化背景研究.pdf";
        ftpService.deleteFile(path);
    }
}

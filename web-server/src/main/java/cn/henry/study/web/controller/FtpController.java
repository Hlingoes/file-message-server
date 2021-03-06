package cn.henry.study.web.controller;

import cn.henry.study.web.service.files.FtpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

/**
 * description: 访问接口，通过service返回对应的结果集
 *
 * @author Hlingoes
 * @date 2019/12/21 18:19
 */
@RestController
public class FtpController {
    private static final Logger logger = LoggerFactory.getLogger(FtpController.class);

    @Autowired
    private FtpService ftpService;

    @GetMapping(value = "testDownload")
    public boolean testDownload() {
        return ftpService.download("/资料/bak/[muchong.com]数学物理方法.pdf",
                "G:\\迅雷下载\\[muchong.com]数学物理方法.pdf");
    }

    @GetMapping(value = "testUpload")
    public String testUpload() {
        File file = new File("G:\\下载\\[emuch.net]普林斯顿数学指南.pdf");
        String path = "/资料/bak/";
        for (int i = 0; i < 10; i++) {
            ftpService.testUploadFail(path, file.getName(), file);
        }
        return "glad to see you";
    }

}

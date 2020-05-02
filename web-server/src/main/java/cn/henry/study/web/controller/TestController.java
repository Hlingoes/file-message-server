package cn.henry.study.web.controller;

import cn.henry.study.common.result.CommonResult;
import cn.henry.study.common.result.Result;
import cn.henry.study.common.utils.FileHelpUtils;
import cn.henry.study.web.entity.CommandParams;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * description: 测试类
 *
 * @author Hlingoes
 * @date 2020/4/8 17:30
 */
@RestController
@RequestMapping("test")
public class TestController {

    @GetMapping(value = "hello1")
    public String hello1() {
        return "hello world";
    }

    @GetMapping(value = "hello2")
    public String hello2() {
        return "hello world";
    }

    @PostMapping(value = "submit")
    public Result submit(@RequestBody CommandParams commandParams) {
        return CommonResult.success(commandParams);
    }

    /**
     * description: 测试文件上传
     *
     * @param commandParams
     * @param file
     * @return cn.henry.study.common.result.Result
     * @author Hlingoes 2020/5/2
     */
    @PostMapping(value = "uploadFile")
    public Result uploadFile(CommandParams commandParams, @RequestParam("file") MultipartFile file) throws IOException {
        File tempFile = FileHelpUtils.findTempleFile(commandParams.getFileName());
        FileHelpUtils.writeTempFile(tempFile, file.getBytes());
        commandParams.setFilePath(tempFile.getAbsolutePath());
        return CommonResult.success(commandParams);
    }

}

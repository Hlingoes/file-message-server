package cn.henry.study.web.controller;

import cn.henry.study.common.result.CommonResult;
import cn.henry.study.common.result.Result;
import cn.henry.study.common.utils.FileHelpUtils;
import cn.henry.study.common.utils.JacksonUtils;
import cn.henry.study.web.entity.CommandParams;
import cn.henry.study.web.entity.QuartzJob;
import cn.henry.study.web.listener.QuartzJobExcelListener;
import cn.henry.study.web.service.quartz.QuartzJobService;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * description: 测试类
 *
 * @author Hlingoes
 * @date 2020/4/8 17:30
 */
@RestController
@RequestMapping("test")
public class TestController {

    @Autowired
    private QuartzJobService jobService;

    @GetMapping(value = "hello")
    public Result hello() {
        return CommonResult.success("hello world");
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

    /**
     * description: 文件下载（失败了会返回一个有部分数据的Excel）
     * 1. 创建excel对应的实体对象
     * 2. 设置返回的 参数
     * 3. 直接写，这里注意，finish的时候会自动关闭OutputStream,当然你外面再关闭流问题不大
     *
     * @param fileName
     * @return java.lang.String
     * @author Hlingoes 2020/5/2
     */
    @GetMapping(value = "downloadExcel")
    public void downloadExcel(@RequestParam("fileName") String fileName, HttpServletResponse response) throws IOException {
        HorizontalCellStyleStrategy horizontalCellStyleStrategy = getHorizontalCellStyleStrategy();
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        fileName = URLEncoder.encode(fileName, "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
        // 这里 需要指定写用哪个class去写，然后写到第一个sheet，名字为模板 然后文件流会自动关闭
        List<QuartzJob> list = this.jobService.findAll();
        EasyExcel.write(response.getOutputStream(), QuartzJob.class)
                .registerWriteHandler(horizontalCellStyleStrategy)
                .sheet("模板")
                .doWrite(list);
    }

    private HorizontalCellStyleStrategy getHorizontalCellStyleStrategy() {
        // 头的策略
        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        // 背景设置为红色
        headWriteCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        WriteFont headWriteFont = new WriteFont();
        headWriteFont.setFontHeightInPoints((short) 14);
        headWriteCellStyle.setWriteFont(headWriteFont);
        // 内容的策略
        WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
        // 这里需要指定 FillPatternType 为FillPatternType.SOLID_FOREGROUND 不然无法显示背景颜色.头默认了 FillPatternType所以可以不指定
        contentWriteCellStyle.setFillPatternType(FillPatternType.SOLID_FOREGROUND);
        contentWriteCellStyle.setBorderBottom(BorderStyle.THIN);
        contentWriteCellStyle.setBorderTop(BorderStyle.THIN);
        contentWriteCellStyle.setBorderLeft(BorderStyle.THIN);
        contentWriteCellStyle.setBorderRight(BorderStyle.THIN);
        contentWriteCellStyle.setBottomBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        contentWriteCellStyle.setTopBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        contentWriteCellStyle.setLeftBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        contentWriteCellStyle.setRightBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        // 背景色
        contentWriteCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        WriteFont contentWriteFont = new WriteFont();
        // 字体大小
        contentWriteFont.setFontHeightInPoints((short) 12);
        contentWriteCellStyle.setWriteFont(contentWriteFont);
        // 这个策略是 头是头的样式 内容是内容的样式 其他的策略可以自己实现
        return new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);
    }

    /**
     * 文件下载并且失败的时候返回json（默认失败了会返回一个有部分数据的Excel）
     *
     * @since 2.1.1
     */
    @GetMapping("downloadFailedUsingJson")
    public void downloadFailedUsingJson(@RequestParam("fileName") String fileName, HttpServletResponse response) throws IOException {
        try {
            downloadExcel(fileName, response);
        } catch (Exception e) {
            // 重置response
            response.reset();
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            Map<String, String> map = new HashMap<String, String>(16);
            map.put("status", "failure");
            map.put("message", "下载文件失败" + e.getMessage());
            response.getWriter().println(JacksonUtils.object2Str(map));
        }
    }

    /**
     * description: Excel 文件上传
     * 1. 创建excel对应的实体对象
     * 2. 由于默认一行行的读取excel，所以需要创建excel一行一行的回调监听器
     * 3. 直接读即可
     *
     * @param file
     * @return java.lang.String
     * @author Hlingoes 2020/5/2
     */
    @PostMapping("uploadExcel")
    public Result uploadExcel(CommandParams commandParams, @RequestParam("file") MultipartFile file) throws IOException {
        QuartzJobExcelListener quartzJobExcelListener = new QuartzJobExcelListener(this.jobService);
        EasyExcel.read(file.getInputStream(), QuartzJob.class, quartzJobExcelListener).sheet().doRead();
        return CommonResult.success(quartzJobExcelListener.getDescription().toString());
    }
}

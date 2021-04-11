package cn.henry.study.common;

import cn.henry.study.common.freemaker2excel.ExportCommentExcel;
import cn.henry.study.common.freemaker2excel.ExportExcel;
import cn.henry.study.common.freemaker2excel.ExportImageExcel;
import org.junit.Test;

/**
 * description:
 *
 * @author Hlingoes
 * @date 2021/4/11 22:44
 */
public class FreemarkerUtilsTest {

    @Test
    public void test() {

        // 1.Freemarker导出xml格式复杂的Excel示例
        ExportExcel excel = new ExportExcel();
        excel.export();

        // 2.Freemarker导出带有图片的Excel示例
        ExportImageExcel imageExcel = new ExportImageExcel();
        //Excel 2003+ 版本（有弹框提示数据损坏，兼容性不好）
        imageExcel.export2003();
        //Excel 2007+ 版本(推荐使用，兼容性好，性能佳)
        imageExcel.export2007();

        // 3.导出带有注释的Excel
        ExportCommentExcel exportCommentExcel = new ExportCommentExcel();
        // Excel 2007+ 版本(推荐使用)
        exportCommentExcel.export();

    }
}

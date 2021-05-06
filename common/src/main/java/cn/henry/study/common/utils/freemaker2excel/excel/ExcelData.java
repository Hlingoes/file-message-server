package cn.henry.study.common.utils.freemaker2excel.excel;

/**
 * @project freemarker-excel
 * @description: 自定义解析excel的Data类
 * @author 大脑补丁
 * @create 2020-04-14 16:54
 */
public class ExcelData {

    private String type;

    private String xmlns;

    private ExcelFont excelFont;

    private String text;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getXmlns() {
        return xmlns;
    }

    public void setXmlns(String xmlns) {
        this.xmlns = xmlns;
    }

    public ExcelFont getExcelFont() {
        return excelFont;
    }

    public void setExcelFont(ExcelFont excelFont) {
        this.excelFont = excelFont;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

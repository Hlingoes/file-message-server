package cn.henry.study.common.utils.freemaker2excel.excel;

/**
 * @project freemarker-excel
 * @description: 自定义解析excel的Data类
 * @author 大脑补丁
 * @create 2020-04-14 16:54
 */
public class Data {

    private String type;

    private String xmlns;

    private Font font;

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

    public Font getFont() {
        return font;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

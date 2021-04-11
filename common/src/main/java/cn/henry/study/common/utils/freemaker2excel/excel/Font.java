package cn.henry.study.common.utils.freemaker2excel.excel;

/**
 * @project freemarker-excel
 * @description: 自定义解析excel的Font类
 * @author 大脑补丁
 * @create 2020-04-14 16:54
 */
public class Font {

    private String face;

    private String charSet;

    private String color;

    private String text;

    private int bold;

    public String getFace() {
        return face;
    }

    public void setFace(String face) {
        this.face = face;
    }

    public String getCharSet() {
        return charSet;
    }

    public void setCharSet(String charSet) {
        this.charSet = charSet;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getBold() {
        return bold;
    }

    public void setBold(int bold) {
        this.bold = bold;
    }
}

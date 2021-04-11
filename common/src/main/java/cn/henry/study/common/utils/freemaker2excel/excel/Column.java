package cn.henry.study.common.utils.freemaker2excel.excel;

/**
 * @project freemarker-excel
 * @description: 自定义解析excel的Column类
 * @author 大脑补丁
 * @create 2020-04-14 16:54
 */
public class Column {

    private Integer index;
    private double width;
    private int autofitwidth;

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public int getAutofitwidth() {
        return autofitwidth;
    }

    public void setAutofitwidth(int autofitwidth) {
        this.autofitwidth = autofitwidth;
    }
}

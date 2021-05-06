package cn.henry.study.common.utils.freemaker2excel.excel;

/**
 * @project freemarker-excel
 * @description: 自定义解析excel的Column类
 * @author 大脑补丁
 * @create 2020-04-14 16:54
 */
public class ExcelColumn {

    private Integer index;
    private double width;
    private int autoFitWidth;

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

    public int getAutoFitWidth() {
        return autoFitWidth;
    }

    public void setAutoFitWidth(int autoFitWidth) {
        this.autoFitWidth = autoFitWidth;
    }
}

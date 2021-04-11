package cn.henry.study.common.utils.freemaker2excel.excel;

import java.util.List;

/**
 * @project freemarker-excel
 * @description: 自定义解析excel的Row类
 * @author 大脑补丁
 * @create 2020-04-14 16:54
 */
public class Row {

    private Integer height;

    private List<Cell> cells;

    private Integer index;

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public List<Cell> getCells() {
        return cells;
    }

    public void setCells(List<Cell> cells) {
        this.cells = cells;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }
}

package cn.henry.study.common.utils.freemaker2excel.excel;

import java.util.List;

/**
 * @project freemarker-excel
 * @description: 自定义解析excel的Table类
 * @author 大脑补丁
 * @create 2020-04-14 16:54
 */
public class Table {
    private Integer expandedColumnCount;

    private Integer expandedRowCount;

    private Integer fullColumns;

    private Integer fullRows;

    private Integer defaultColumnWidth;

    private Integer defaultRowHeight;

    private List<Column> columns;

    private List<Row> rows;

    public Integer getExpandedColumnCount() {
        return expandedColumnCount;
    }

    public void setExpandedColumnCount(Integer expandedColumnCount) {
        this.expandedColumnCount = expandedColumnCount;
    }

    public Integer getExpandedRowCount() {
        return expandedRowCount;
    }

    public void setExpandedRowCount(Integer expandedRowCount) {
        this.expandedRowCount = expandedRowCount;
    }

    public Integer getFullColumns() {
        return fullColumns;
    }

    public void setFullColumns(Integer fullColumns) {
        this.fullColumns = fullColumns;
    }

    public Integer getFullRows() {
        return fullRows;
    }

    public void setFullRows(Integer fullRows) {
        this.fullRows = fullRows;
    }

    public Integer getDefaultColumnWidth() {
        return defaultColumnWidth;
    }

    public void setDefaultColumnWidth(Integer defaultColumnWidth) {
        this.defaultColumnWidth = defaultColumnWidth;
    }

    public Integer getDefaultRowHeight() {
        return defaultRowHeight;
    }

    public void setDefaultRowHeight(Integer defaultRowHeight) {
        this.defaultRowHeight = defaultRowHeight;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }

    public List<Row> getRows() {
        return rows;
    }

    public void setRows(List<Row> rows) {
        this.rows = rows;
    }
}

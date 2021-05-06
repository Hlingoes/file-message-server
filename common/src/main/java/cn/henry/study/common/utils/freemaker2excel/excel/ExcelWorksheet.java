package cn.henry.study.common.utils.freemaker2excel.excel;

/**
 * @project freemarker-excel
 * @description: 自定义解析excel的Worksheet类
 * @author 大脑补丁
 * @create 2020-04-14 16:54
 */
public class ExcelWorksheet {

    private String Name;

    private ExcelTable excelTable;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public ExcelTable getExcelTable() {
        return excelTable;
    }

    public void setExcelTable(ExcelTable excelTable) {
        this.excelTable = excelTable;
    }
}

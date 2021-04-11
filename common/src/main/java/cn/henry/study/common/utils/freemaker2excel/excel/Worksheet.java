package cn.henry.study.common.utils.freemaker2excel.excel;

/**
 * @project freemarker-excel
 * @description: 自定义解析excel的Worksheet类
 * @author 大脑补丁
 * @create 2020-04-14 16:54
 */
public class Worksheet {

    private String Name;

    private Table table;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }
}

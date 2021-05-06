package cn.henry.study.common.utils.freemaker2excel.excel;

import org.apache.poi.ss.util.CellRangeAddress;

import java.util.List;

/**
 * @project freemarker-excel
 * @description: 合并单元格信息
 * @author 大脑补丁
 * @create 2020-04-14 16:54
 */
public class ExcelCellRangeAddressEntity {

    private CellRangeAddress cellRangeAddress;

    private List<ExcelStyle.Border> borders;

    public CellRangeAddress getCellRangeAddress() {
        return cellRangeAddress;
    }

    public void setCellRangeAddress(CellRangeAddress cellRangeAddress) {
        this.cellRangeAddress = cellRangeAddress;
    }

    public List<ExcelStyle.Border> getBorders() {
        return borders;
    }

    public void setBorders(List<ExcelStyle.Border> borders) {
        this.borders = borders;
    }
}

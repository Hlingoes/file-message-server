package cn.henry.study.common.utils.freemaker2excel.excel;

import org.apache.poi.ss.util.CellRangeAddress;

import java.util.List;

/**
 * @project freemarker-excel
 * @description: 合并单元格信息
 * @author 大脑补丁
 * @create 2020-04-14 16:54
 */
public class CellRangeAddressEntity {

    private CellRangeAddress cellRangeAddress;

    private List<Style.Border> borders;

    public CellRangeAddress getCellRangeAddress() {
        return cellRangeAddress;
    }

    public void setCellRangeAddress(CellRangeAddress cellRangeAddress) {
        this.cellRangeAddress = cellRangeAddress;
    }

    public List<Style.Border> getBorders() {
        return borders;
    }

    public void setBorders(List<Style.Border> borders) {
        this.borders = borders;
    }
}

package cn.henry.study.common.utils.freemaker2excel.excel;

/**
 * @project freemarker-excel
 * @description: 自定义解析excel的cell类
 * @author 大脑补丁
 * @create 2020-04-14 16:54
 */
public class ExcelCell {

    private String styleID;

    private Integer mergeAcross;

    private Integer mergeDown;

    private ExcelData excelData;

    private Integer index;

    private ExcelComment excelComment;

    public String getStyleID() {
        return styleID;
    }

    public void setStyleID(String styleID) {
        this.styleID = styleID;
    }

    public Integer getMergeAcross() {
        return mergeAcross;
    }

    public void setMergeAcross(Integer mergeAcross) {
        this.mergeAcross = mergeAcross;
    }

    public Integer getMergeDown() {
        return mergeDown;
    }

    public void setMergeDown(Integer mergeDown) {
        this.mergeDown = mergeDown;
    }

    public ExcelData getExcelData() {
        return excelData;
    }

    public void setExcelData(ExcelData excelData) {
        this.excelData = excelData;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public ExcelComment getExcelComment() {
        return excelComment;
    }

    public void setExcelComment(ExcelComment excelComment) {
        this.excelComment = excelComment;
    }
}

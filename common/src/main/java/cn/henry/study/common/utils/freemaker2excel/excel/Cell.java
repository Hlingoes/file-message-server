package cn.henry.study.common.utils.freemaker2excel.excel;

/**
 * @project freemarker-excel
 * @description: 自定义解析excel的cell类
 * @author 大脑补丁
 * @create 2020-04-14 16:54
 */
public class Cell {

    private String styleID;

    private Integer mergeAcross;

    private Integer MergeDown;

    private Data data;

    private Integer index;

    private Comment comment;

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
        return MergeDown;
    }

    public void setMergeDown(Integer mergeDown) {
        MergeDown = mergeDown;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }
}

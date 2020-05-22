package cn.henry.study.common.bo;

import java.util.List;

/**
 * description: 分段查询参数
 *
 * @author Hlingoes
 * @date 2020/5/22 23:50
 */
public class PartitionElements {
    private int page;
    private int rows;
    private Object[] args;
    private List<Object> datas;

    public PartitionElements() {

    }

    public PartitionElements(int page, int rows, Object[] args) {
        this.page = page;
        this.rows = rows;
        this.args = args;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public List<Object> getDatas() {
        return datas;
    }

    public void setDatas(List<Object> datas) {
        this.datas = datas;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    @Override
    public String toString() {
        return "PartitionElements{" +
                "page=" + page +
                ", rows=" + rows +
                ", datas=" + datas +
                '}';
    }
}

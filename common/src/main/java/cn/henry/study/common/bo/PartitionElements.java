package cn.henry.study.common.bo;

import java.util.List;

/**
 * description: 分段查询参数
 *
 * @author Hlingoes
 * @date 2020/5/22 23:50
 */
public class PartitionElements {
    private long index;
    private long rows;
    private long total;
    private Object[] args;
    private List<Object> datas;

    public PartitionElements() {

    }

    public PartitionElements(long index, long rows, long total, Object[] args) {
        this.index = index;
        this.rows = rows;
        this.total = total;
        this.args = args;
    }

    public long getIndex() {
        return index;
    }

    public void setIndex(long index) {
        this.index = index;
    }

    public long getRows() {
        return rows;
    }

    public void setRows(long rows) {
        this.rows = rows;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
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
                "index=" + index +
                ", rows=" + rows +
                ", datas=" + datas +
                '}';
    }
}

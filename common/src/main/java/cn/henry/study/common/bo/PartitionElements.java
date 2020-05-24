package cn.henry.study.common.bo;

import java.util.List;

/**
 * description: 分段参数
 *
 * @author Hlingoes
 * @date 2020/5/22 23:50
 */
public class PartitionElements {
    /**
     * 当前页数
     */
    private long currentPage;
    /**
     * 每页显示条目个数
     */
    private long pageSize;
    /**
     * 总页数
     */
    private long pageCount;
    /**
     * 总条目数
     */
    private long total;
    private Object[] args;
    private List<Object> datas;

    public PartitionElements() {

    }

    public PartitionElements(long currentPage, long pageSize, long pageCount, long total, Object[] args) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.pageCount = pageCount;
        this.total = total;
        this.args = args;
    }

    /**
     * description: 根据任务总量和单次任务处理量，计算任务个数
     *
     * @param total
     * @param pageSize
     * @return long
     * @author Hlingoes 2020/5/23
     */
    public static long calculateTaskCount(long total, long pageSize) {
        long pageCount = total / pageSize;
        if (total % pageSize != 0) {
            pageCount = pageCount + 1;
        }
        return pageCount;
    }

    public long getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(long currentPage) {
        this.currentPage = currentPage;
    }

    public long getPageSize() {
        return pageSize;
    }

    public void setPageSize(long pageSize) {
        this.pageSize = pageSize;
    }

    public long getPageCount() {
        return pageCount;
    }

    public void setPageCount(long pageCount) {
        this.pageCount = pageCount;
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
                "currentPage=" + currentPage +
                ", pageSize=" + pageSize +
                ", datas=" + datas +
                '}';
    }
}

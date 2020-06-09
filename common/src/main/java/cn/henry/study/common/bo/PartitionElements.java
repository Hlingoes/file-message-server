package cn.henry.study.common.bo;

import java.util.Arrays;

/**
 * description: 分段参数
 *
 * @author Hlingoes
 * @date 2020/5/22 23:50
 */
public class PartitionElements {
    /**
     * 当前的分段任务索引
     */
    private long index;
    /**
     * 批量处理的任务个数
     */
    private long batchCounts;
    /**
     * 任务的分段个数
     */
    private long partitions;
    /**
     * 任务总数
     */
    private long totalCounts;
    private Object[] args;
    private Object data;

    public PartitionElements() {

    }

    public PartitionElements(long batchCounts, long totalCounts, Object[] args) {
        this.batchCounts = batchCounts;
        this.totalCounts = totalCounts;
        this.partitions = aquirePartitions(totalCounts, batchCounts);
        this.args = args;
    }

    public PartitionElements(long index, PartitionElements elements) {
        this.index = index;
        this.batchCounts = elements.getBatchCounts();
        this.partitions = elements.getPartitions();
        this.totalCounts = elements.getTotalCounts();
        this.args = elements.getArgs();
    }

    /**
     * description: 根据任务总量和单次任务处理量，计算任务个数
     *
     * @param totalCounts
     * @param batchCounts
     * @return long partitions
     * @author Hlingoes 2020/5/23
     */
    public long aquirePartitions(long totalCounts, long batchCounts) {
        long partitions = totalCounts / batchCounts;
        if (totalCounts % batchCounts != 0) {
            partitions = partitions + 1;
        }
        //  兼容任务总数total = 1 的情况
        if (partitions == 0) {
            partitions = 1;
        }
        return partitions;
    }

    public long getIndex() {
        return index;
    }

    public void setIndex(long index) {
        this.index = index;
    }

    public long getBatchCounts() {
        return batchCounts;
    }

    public void setBatchCounts(long batchCounts) {
        this.batchCounts = batchCounts;
    }

    public long getPartitions() {
        return partitions;
    }

    public void setPartitions(long partitions) {
        this.partitions = partitions;
    }

    public long getTotalCounts() {
        return totalCounts;
    }

    public void setTotalCounts(long totalCounts) {
        this.totalCounts = totalCounts;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "PartitionElements{" +
                "index=" + index +
                ", batchCounts=" + batchCounts +
                ", partitions=" + partitions +
                ", totalCounts=" + totalCounts +
                ", args=" + Arrays.toString(args) +
                '}';
    }
}

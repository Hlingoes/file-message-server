package cn.henry.study.common.service;

import cn.henry.study.common.bo.PartitionElements;

import java.util.List;

/**
 * description: 业务分治接口
 *
 * @author Hlingoes 2020/5/22
 */
public interface OperationThreadService {

    /**
     * description: 任务总量
     *
     * @param args
     * @return long
     * @author Hlingoes 2020/5/22
     */
    long count(Object[] args);

    /**
     * description: 单次分段查询
     *
     * @param elements
     * @return java.util.List<java.lang.Object>
     * @author Hlingoes 2020/5/22
     */
    List<Object> find(PartitionElements elements);

    /**
     * description: 更新
     *
     * @param elements
     * @return void
     * @author Hlingoes 2020/5/22
     */
    void update(PartitionElements elements);

    /**
     * description: 删除
     *
     * @param elements
     * @return void
     * @author Hlingoes 2020/5/22
     */
    void delete(PartitionElements elements);

    /**
     * description: 与处理方法
     *
     * @param elements
     * @return void
     * @author Hlingoes 2020/5/23
     */
    void prepare(PartitionElements elements);
}

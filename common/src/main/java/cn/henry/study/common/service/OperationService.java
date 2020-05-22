package cn.henry.study.common.service;

import cn.henry.study.common.bo.PartitionElements;

import java.util.List;

/**
 * description: 业务分治接口
 *
 * @author Hlingoes 2020/5/22
 */
public interface OperationService {

    /**
     * description: 任务总量
     *
     * @param args
     * @return int
     * @author Hlingoes 2020/5/22
     */
    int count(Object[] args);

    /**
     * description: 单次分段查询
     *
     * @param elements
     * @param args
     * @return java.util.List<java.lang.Object>
     * @author Hlingoes 2020/5/22
     */
    List<Object> find(PartitionElements elements, Object[] args);

    /**
     * description: 更新
     *
     * @param elements
     * @param args
     * @return void
     * @author Hlingoes 2020/5/22
     */
    void update(PartitionElements elements, Object[] args);

    /**
     * description: 删除
     *
     * @param elements
     * @param args
     * @return void
     * @author Hlingoes 2020/5/22
     */
    void delete(PartitionElements elements, Object[] args);

    /**
     * description: 与处理方法
     *
     * @param elements
     * @return void
     * @author Hlingoes 2020/5/23
     */
    void prepare(PartitionElements elements);
}

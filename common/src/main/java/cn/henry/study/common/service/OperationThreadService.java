package cn.henry.study.common.service;

import cn.henry.study.common.bo.PartitionElements;

/**
 * description: 业务分治归并处理接口
 *
 * @author Hlingoes 2020/5/22
 */
public interface OperationThreadService {

    /**
     * description: 任务总量
     *
     * @param args
     * @return long
     * @throws Exception
     * @author Hlingoes 2020/5/22
     */
    long count(Object[] args) throws Exception;

    /**
     * description: 在多线程分治任务之前的预处理方法，返回业务数据
     *
     * @param args
     * @return Object
     * @throws Exception
     * @author Hlingoes 2020/5/23
     */
    Object prepare(Object[] args) throws Exception;

    /**
     * description: 多线程的任务逻辑
     *
     * @param elements
     * @return java.lang.Object
     * @throws Exception
     * @author Hlingoes 2020/5/24
     */
    Object invoke(PartitionElements elements) throws Exception;

    /**
     * description: 多线程单个任务结束后的归并方法
     *
     * @param elements
     * @param object
     * @return void
     * @throws Exception
     * @author Hlingoes 2020/5/23
     */
    void post(PartitionElements elements, Object object) throws Exception;

    /**
     * description: 归并结果之后的尾处理
     *
     * @param object
     * @return java.lang.Object
     * @throws Exception
     * @author Hlingoes 2020/5/24
     */
    Object finished(Object object) throws Exception;

}

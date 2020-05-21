package cn.henry.study.common.thread;

import java.util.List;

/**
 * description: 批量操作的接口，包括find, update, delete
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
     * @param args
     * @return java.util.List<java.lang.Object>
     * @author Hlingoes 2020/5/22
     */
    List<Object> find(Object[] args);

    /**
     * description:
     *
     * @param args
     * @return void
     * @author Hlingoes 2020/5/22
     */
    void update(Object[] args);

    /**
     * description:
     *
     * @param args
     * @return void
     * @author Hlingoes 2020/5/22
     */
    void delete(Object[] args);

}

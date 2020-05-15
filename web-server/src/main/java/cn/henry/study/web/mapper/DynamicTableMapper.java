package cn.henry.study.web.mapper;

import org.apache.ibatis.annotations.Param;

/**
 * description: 分表的mapper
 *
 * @author Hlingoes
 * @date 2020/5/16 0:12
 */
public interface DynamicTableMapper {

    /**
     * description:  创建表
     *
     * @param tableName
     * @return void
     * @author Hlingoes 2020/5/16
     */
    void createDynamicTable(@Param(value = "tableName") String tableName);
}

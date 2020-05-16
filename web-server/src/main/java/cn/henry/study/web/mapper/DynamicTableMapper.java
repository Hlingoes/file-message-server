package cn.henry.study.web.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.dao.DataAccessException;

import java.util.Map;

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
     * @throws DataAccessException
     * @author Hlingoes 2020/5/16
     */
    void createDynamicTable(@Param(value = "tableName") String tableName) throws DataAccessException;

    /**
     * description: 使用show tables检查表是否存在
     *
     * @param tableName
     * @return java.util.Map<java.lang.String, java.lang.String>
     * @throws DataAccessException
     * @author Hlingoes 2020/5/16
     */
    Map<String, String> checkTableExistsWithShow(@Param(value = "tableName") String tableName) throws DataAccessException;
}

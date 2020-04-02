package cn.henry.study.database;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * description: 动态数据源获取的核心代码
 *
 * @author Hlingoes
 * @date 2020/4/2 22:57
 */
public class DynamicDataSource extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        return DatabaseContextHolder.getDatabaseType();
    }
}

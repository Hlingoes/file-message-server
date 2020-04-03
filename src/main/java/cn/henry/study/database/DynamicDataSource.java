package cn.henry.study.database;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * description: 获取与数据源相关的key，
 * 此key是Map<String,DataSource> resolvedDataSources 中与数据源绑定的key值
 * 在通过determineTargetDataSource获取目标数据源时使用
 *
 * @author Hlingoes
 * @date 2020/4/2 22:57
 */
public class DynamicDataSource extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        return DatabaseContextHolder.getRouteKey();
    }
}

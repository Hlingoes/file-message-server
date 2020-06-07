package com.xiaoju.uemc.tinyid.config;

import com.xiaoju.uemc.tinyid.entity.JdbcProps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.util.*;

/**
 * @author du_imba
 */
@Configuration
public class DataSourceConfig {
    private static final Logger logger = LoggerFactory.getLogger(DataSourceConfig.class);

    private static final String SEP = ",";
    private static final String PROPS_PREFIX = "datasource.tinyid.";
    private static final String DEFAULT_DATASOURCE_TYPE = "com.zaxxer.hikari.HikariDataSource";

    @Autowired
    private Environment environment;

    @Bean
    public DataSource getDynamicDataSource() {
        DynamicDataSource routingDataSource = new DynamicDataSource();
        List<String> dataSourceKeys = new ArrayList<>();
        Iterable<ConfigurationPropertySource> sources = ConfigurationPropertySources.get(environment);
        Binder binder = new Binder(sources);
        BindResult<Properties> bindResult = binder.bind(PROPS_PREFIX, Properties.class);
        Properties properties = bindResult.get();

        String names = properties.getProperty("names");
        String dataSourceType = properties.getProperty("type");

        Map<Object, Object> targetDataSources = new HashMap<>(4);
        routingDataSource.setTargetDataSources(targetDataSources);
        routingDataSource.setDataSourceKeys(dataSourceKeys);
        // 多个数据源
        for (String name : names.split(SEP)) {
            BindResult<JdbcProps> bindResultProps = binder.bind(PROPS_PREFIX + name + ".", JdbcProps.class);
            JdbcProps jdbcProps = bindResultProps.get();
            DataSource dataSource = buildDataSource(dataSourceType, jdbcProps);
            buildDataSourceProperties(dataSource, jdbcProps);
            targetDataSources.put(name, dataSource);
            dataSourceKeys.add(name);
        }
        return routingDataSource;
    }

    private void buildDataSourceProperties(DataSource dataSource, JdbcProps jdbcProps) {
        try {
            BeanUtils.copyProperties(dataSource, jdbcProps);
        } catch (Exception e) {
            logger.error("error copy properties", e);
        }
    }

    private DataSource buildDataSource(String dataSourceType, JdbcProps jdbcProps) {
        try {
            String className = DEFAULT_DATASOURCE_TYPE;
            if (dataSourceType != null && !"".equals(dataSourceType.trim())) {
                className = dataSourceType;
            }
            Class<? extends DataSource> type = (Class<? extends DataSource>) Class.forName(className);
            return DataSourceBuilder.create()
                    .driverClassName(jdbcProps.getDriverClassName())
                    .url(jdbcProps.getUrl())
                    .username(jdbcProps.getUsername())
                    .password(jdbcProps.getPassword())
                    .type(type)
                    .build();
        } catch (ClassNotFoundException e) {
            logger.error("buildDataSource error", e);
            throw new IllegalStateException(e);
        }
    }

}

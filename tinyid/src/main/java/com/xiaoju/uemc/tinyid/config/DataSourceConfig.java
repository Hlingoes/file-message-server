package com.xiaoju.uemc.tinyid.config;

import com.xiaoju.uemc.tinyid.entity.JdbcProps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.*;

/**
 * @author du_imba
 */
@Configuration
public class DataSourceConfig {
    private static final Logger logger = LoggerFactory.getLogger(DataSourceConfig.class);

    public static final String PROPS_PREFIX = "datasource.tinyid.";
    private static final String SEP = ",";
    private static final String DEFAULT_DATASOURCE_TYPE = "com.zaxxer.hikari.HikariDataSource";

    @Autowired
    private Environment environment;

    @Bean
    public DataSource getDynamicDataSource() {
        DynamicDataSource routingDataSource = new DynamicDataSource();
        List<String> dataSourceKeys = new ArrayList<>();

        String[] profiles = environment.getActiveProfiles();
        // 解决spring的配置读取问题，父层级为application-dev.properties则调用方应该为: application-dev.yml，
        // spring会逐级查询，找到该文件则停止
        for (String profile : profiles) {
            // 读取resource下的文件
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            // 获取单个文件
            Resource resource = resolver.getResource("application-" + profile + ".properties");
            String filePath = "";
            try {
                filePath = resource.getFile().getAbsolutePath();
                Properties props = PropertiesLoaderUtils.loadProperties(resource);
                String names = props.getProperty(PROPS_PREFIX + "names");
                String dataSourceType = props.getProperty(PROPS_PREFIX + "type");
                Map<Object, Object> targetDataSources = new HashMap<>(4);
                routingDataSource.setTargetDataSources(targetDataSources);
                routingDataSource.setDataSourceKeys(dataSourceKeys);
                // 多个数据源
                for (String name : names.split(SEP)) {
                    JdbcProps jdbcProps = new JdbcProps(props, name);
                    DataSource dataSource = buildDataSource(dataSourceType, jdbcProps);
                    buildDataSourceProperties(dataSource, jdbcProps);
                    targetDataSources.put(name, dataSource);
                    dataSourceKeys.add(name);
                }
            } catch (IOException e) {
                logger.info("read file source error: {}", filePath, e);
            }
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

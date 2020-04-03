package cn.henry.study.configuration;

import cn.henry.study.database.DynamicDataSource;
import cn.henry.study.entity.JdbcProperties;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import java.util.HashMap;
import java.util.Map;

/**
 * description: mybatis多数据源配置
 * 1. 根据spring对bean的初始化顺序，当前bean在初始的时候，先走afterPropertiesSet()，动态设置所有数据源；
 * 2. 初始化完成后，给定默认的数据源DynamicDataSource defaultDataSource，设置到SqlSessionFactory中；
 * 3. 用DatabaseContextHolder将缓存当前线程的DataSource；
 * 4. 本案例使用RestControllerAspect切面，通过请求中的routeKey来切换数据源；
 * 5. 实际调用的地方在DynamicDataSource类；
 * 可根据使用场景，修改数据库的配置注入，或者用其他方式切换数据源
 *
 * @author Hlingoes
 * @date 2020/4/2 22:44
 */
@Configuration
public class MybatisDynamicDataSourceConfig implements InitializingBean, ApplicationContextAware {
    private static Logger logger = LoggerFactory.getLogger(MybatisDynamicDataSourceConfig.class);

    @Autowired
    private Environment environment;

    @Autowired
    private DataSourceConfig dataSourceConfig;

    private ApplicationContext applicationContext;

    /**
     * 本bean初始化时，根据数据源个数，动态生成数据源bean
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        for (JdbcProperties props : dataSourceConfig.getMysqlClients()) {
            BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(DruidDataSource.class);
            beanDefinitionBuilder.addPropertyValue("driverClassName", props.getDriverClassName());
            beanDefinitionBuilder.addPropertyValue("username", props.getUsername());
            beanDefinitionBuilder.addPropertyValue("password", props.getPassword());
            beanDefinitionBuilder.addPropertyValue("url", props.getUrl());
            beanDefinitionBuilder.addPropertyValue("initialSize", dataSourceConfig.getInitialSize());
            beanDefinitionBuilder.addPropertyValue("minIdle", dataSourceConfig.getMinIdle());
            beanDefinitionBuilder.addPropertyValue("maxActive", dataSourceConfig.getMaxActive());
            beanDefinitionBuilder.addPropertyValue("maxWait", dataSourceConfig.getMaxWait());
            beanDefinitionBuilder.addPropertyValue("poolPreparedStatements", dataSourceConfig.getPoolPreparedStatements());
            beanDefinitionBuilder.addPropertyValue("maxPoolPreparedStatementPerConnectionSize", dataSourceConfig.getMaxPoolPreparedStatementPerConnectionSize());
            beanDefinitionBuilder.addPropertyValue("validationQuery", dataSourceConfig.getValidationQuery());
            beanDefinitionBuilder.addPropertyValue("validationQueryTimeout", dataSourceConfig.getValidationQueryTimeout());
            beanDefinitionBuilder.addPropertyValue("testOnBorrow", dataSourceConfig.getTestOnBorrow());
            beanDefinitionBuilder.addPropertyValue("testOnReturn", dataSourceConfig.getTestOnReturn());
            beanDefinitionBuilder.addPropertyValue("testWhileIdle", dataSourceConfig.getTestWhileIdle());
            beanDefinitionBuilder.addPropertyValue("filters", dataSourceConfig.getFilters());
            BeanDefinition dataBeanDefinition = beanDefinitionBuilder.getRawBeanDefinition();
            BeanDefinitionRegistry beanFactory = (BeanDefinitionRegistry) applicationContext.getAutowireCapableBeanFactory();
            beanFactory.registerBeanDefinition(props.getRouteKey(), dataBeanDefinition);
            logger.info("数据库连接源: {}", props);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * description: 动态注入数据源，默认最后一个url生效
     *
     * @param
     * @return cn.henry.study.database.DynamicDataSource
     * @Primary 该注解表示在同一个接口有多个实现类可以注入的时候，默认选择哪一个，而不是让@autowire注解报错
     * @author Hlingoes 2020/4/3
     */
    @Bean
    @Primary
    public DynamicDataSource dataSource() {
        Map<Object, Object> targetDataSources = new HashMap<>();
        String beanKey = null;
        Object defaultSource = null;
        for (JdbcProperties props : dataSourceConfig.getMysqlClients()) {
            beanKey = props.getRouteKey();
            targetDataSources.put(beanKey, applicationContext.getBean(beanKey));
            if (props.getMaster()) {
                defaultSource = applicationContext.getBean(beanKey);
            }
        }
        DynamicDataSource dataSource = new DynamicDataSource();
        // 该方法是AbstractRoutingDataSource的方法
        dataSource.setTargetDataSources(targetDataSources);
        // 没有设置master哭，则默认的datasource设置为最后一个数据源
        if (null == defaultSource) {
            defaultSource = applicationContext.getBean(beanKey);
        }
        dataSource.setDefaultTargetDataSource(defaultSource);

        return dataSource;
    }

    /**
     * 根据数据源创建SqlSessionFactory
     */
    @Bean
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean fb = new SqlSessionFactoryBean();
        // 指定数据源(这个必须有，否则报错)
        fb.setDataSource(this.dataSource());
        // 下边两句仅仅用于*.xml文件，如果整个持久层操作不需要使用到xml文件的话（只用注解就可以搞定），则不加
        fb.setTypeAliasesPackage(environment.getProperty("mybatis.type-aliases-package"));
        // 指定基包
        fb.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(environment.getProperty("mybatis.mapper-locations")));

        //把可能定义的拦截器扔进去
        try {
            String[] interceptors = applicationContext.getBeanNamesForType(Interceptor.class);
            if (interceptors != null) {
                Interceptor[] interceptorsArray = new Interceptor[interceptors.length];
                for (int i = 0; i < interceptors.length; i++) {
                    Interceptor interceptor = (Interceptor) applicationContext.getBean(interceptors[i]);
                    interceptorsArray[i] = interceptor;
                }
                fb.setPlugins(interceptorsArray);
            }
        } catch (Exception e) {
            logger.error("SqlSessionFactory设置失败", e);
        }
        return fb.getObject();
    }

    /**
     * 配置事务管理器
     */
    @Bean
    public DataSourceTransactionManager transactionManager(DynamicDataSource dataSource) throws Exception {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    public ServletRegistrationBean druidServlet() {
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean();
        servletRegistrationBean.setServlet(new StatViewServlet());
        servletRegistrationBean.addUrlMappings("/druid/*");
        Map<String, String> initParameters = new HashMap<String, String>();
        // 用户名
        initParameters.put("loginUsername", "admin");
        // 密码
        initParameters.put("loginPassword", "admin");
        // 禁用HTML页面上的“Reset All”功能
        initParameters.put("resetEnable", "false");
        // IP白名单 (没有配置或者为空，则允许所有访问)
        initParameters.put("allow", "");
        // IP黑名单 (存在共同时，deny优先于allow)
//        initParameters.put("deny", "192.168.20.38");
        servletRegistrationBean.setInitParameters(initParameters);
        return servletRegistrationBean;
    }

    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new WebStatFilter());
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
        return filterRegistrationBean;
    }

}

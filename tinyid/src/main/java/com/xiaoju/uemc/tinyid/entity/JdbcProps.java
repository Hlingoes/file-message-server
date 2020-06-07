package com.xiaoju.uemc.tinyid.entity;

import com.xiaoju.uemc.tinyid.config.DataSourceConfig;

import java.util.Properties;

/**
 * description: 数据库连接配置
 *
 * @author Hlingoes
 * @date 2020/6/7 11:23
 */
public class JdbcProps {
    private String driverClassName;
    private String url;
    private String username;
    private String password;
    private int maxActive;
    private boolean testOnBorrow;

    public JdbcProps() {
    }

    public JdbcProps(Properties props, String activeName) {
        this.driverClassName = props.get(DataSourceConfig.PROPS_PREFIX + activeName + ".driverClassName").toString();
        this.url = props.get(DataSourceConfig.PROPS_PREFIX + activeName + ".url").toString();
        this.username = props.get(DataSourceConfig.PROPS_PREFIX + activeName + ".username").toString();
        this.password = props.get(DataSourceConfig.PROPS_PREFIX + activeName + ".password").toString();
        this.maxActive = Integer.valueOf(props.get(DataSourceConfig.PROPS_PREFIX + activeName + ".maxActive").toString());
        this.testOnBorrow = Boolean.valueOf(props.get(DataSourceConfig.PROPS_PREFIX + activeName + ".testOnBorrow").toString());
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getMaxActive() {
        return maxActive;
    }

    public void setMaxActive(int maxActive) {
        this.maxActive = maxActive;
    }

    public boolean isTestOnBorrow() {
        return testOnBorrow;
    }

    public void setTestOnBorrow(boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
    }
}

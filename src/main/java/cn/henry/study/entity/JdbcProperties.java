package cn.henry.study.entity;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * description: 数据库连接的基本配置
 *
 * @author Hlingoes
 * @date 2020/4/2 23:17
 */
public class JdbcProperties {
    private String type;
    private String driverClassName;
    private String url;
    private String username;
    private String password;

    public JdbcProperties() {
    }

    /**
     * description: 截取出数据库名，用tableName_md5(url)作bean的key
     *
     * @param
     * @return java.lang.String
     * @author Hlingoes 2020/4/2
     */
    public String beanKey() {
        return StringUtils.substringBetween(url, "/", "?") + DigestUtils.md5Hex(url);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
}

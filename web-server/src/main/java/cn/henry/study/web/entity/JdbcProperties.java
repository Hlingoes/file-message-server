package cn.henry.study.web.entity;

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
    private boolean master;
    private String routeKey;

    public JdbcProperties() {
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

    public boolean getMaster() {
        return master;
    }

    public void setMaster(boolean master) {
        this.master = master;
    }

    public String getRouteKey() {
        return routeKey;
    }

    public void setRouteKey(String routeKey) {
        this.routeKey = routeKey;
    }

    @Override
    public String toString() {
        return "JdbcProperties{" +
                "type='" + type + '\'' +
                ", driverClassName='" + driverClassName + '\'' +
                ", url='" + url + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", master=" + master +
                ", routeKey='" + routeKey + '\'' +
                '}';
    }
}

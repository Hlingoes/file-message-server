package cn.henry.study.web.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * description: Http连接池配置，通过yml文件组装成POJO.
 * java配置的优先级低于yml配置；如果yml配置不存在，会采用java配置
 * 如果需要频繁对一个服务端进行调用可以使用连接池,避免多次建立连接的操作,节省资源开支
 * maxTotalConnect: 连接池的最大连接数，0代表不限；如果取0，需要考虑连接泄露导致系统崩溃的后果
 * maxConnectPerRoute: 每个路由的最大连接数,如果只调用同一个服务端,可以设置和最大连接数相同,也就是一个路由
 * connectTimeout: 客户端和服务端建立连接的超时时间,这里最大只能是21s,因为操作系统的tcp进行三次握手时,有它自己的超时时间,即便设置100s也是在21s后报错.
 * readTimeout: 也就是socketTime,指的是两个相邻的数据包的间隔超时时间,比如下载一个比较大的文件,就算耗时很长也不会中断,但是如果两次响应时间间隔超过这个值就会报错.
 * connectionRequestTimout: 从连接池获取连接的等待时间,不宜过长
 * retryTimes: 重试次数
 * keepAliveTime: 长连接保持时间, http1.1都是默认开启长连接的,如果不配置这个时间,连接池会默认永久保持连接,这显然是不合理的. 对于需要频繁调用的服务端,我们可以开启长连接,然后将这个保持时间设置小一些,能保证相邻两次请求都长连接都还在就可以了. 并不是越久越好,如果是不频繁访问的服务端,长期保持一个无用的连接也会大大占用资源.
 * keepAliveTargetHost: 针对不同的请求地址,可以单独设置不同的长连接存活时间
 * 原文链接：https://blog.csdn.net/zzzgd_666/article/details/88858181
 * @author Hlingoes
 * @date 2019/12/21 20:59
 */
@Component
@ConfigurationProperties(prefix = "spring.http-client.pool")
public class HttpClientPoolConfig {
    /**
     * 连接池的最大连接数
     */
    private int maxTotalConnect;
    /**
     * 同路由的并发数
     */
    private int maxConnectPerRoute;
    /**
     * 客户端和服务器建立连接超时，默认2s
     */
    private int connectTimeout = 2 * 1000;
    /**
     * 指客户端从服务器读取数据包的间隔超时时间,不是总读取时间，默认30s
     */
    private int readTimeout = 30 * 1000;

    private String charset = "UTF-8";
    /**
     * 重试次数,默认2次
     */
    private int retryTimes = 2;
    /**
     * 从连接池获取连接的超时时间,不宜过长,单位ms
     */
    private int connectionRequestTimout = 200;
    /**
     * 针对不同的地址,特别设置不同的长连接保持时间
     */
    private Map<String, Integer> keepAliveTargetHost;
    /**
     * 针对不同的地址,特别设置不同的长连接保持时间,单位 s
     */
    private int keepAliveTime = 60;

    public int getMaxTotalConnect() {
        return maxTotalConnect;
    }

    public void setMaxTotalConnect(int maxTotalConnect) {
        this.maxTotalConnect = maxTotalConnect;
    }

    public int getMaxConnectPerRoute() {
        return maxConnectPerRoute;
    }

    public void setMaxConnectPerRoute(int maxConnectPerRoute) {
        this.maxConnectPerRoute = maxConnectPerRoute;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public int getRetryTimes() {
        return retryTimes;
    }

    public void setRetryTimes(int retryTimes) {
        this.retryTimes = retryTimes;
    }

    public int getConnectionRequestTimout() {
        return connectionRequestTimout;
    }

    public void setConnectionRequestTimout(int connectionRequestTimout) {
        this.connectionRequestTimout = connectionRequestTimout;
    }

    public Map<String, Integer> getKeepAliveTargetHost() {
        return keepAliveTargetHost;
    }

    public void setKeepAliveTargetHost(Map<String, Integer> keepAliveTargetHost) {
        this.keepAliveTargetHost = keepAliveTargetHost;
    }

    public int getKeepAliveTime() {
        return keepAliveTime;
    }

    public void setKeepAliveTime(int keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
    }

    @Override
    public String toString() {
        return "HttpClientPoolConfig{" +
                "maxTotalConnect=" + maxTotalConnect +
                ", maxConnectPerRoute=" + maxConnectPerRoute +
                ", connectTimeout=" + connectTimeout +
                ", readTimeout=" + readTimeout +
                ", charset='" + charset + '\'' +
                ", retryTimes=" + retryTimes +
                ", connectionRequestTimout=" + connectionRequestTimout +
                ", keepAliveTargetHost=" + keepAliveTargetHost +
                ", keepAliveTime=" + keepAliveTime +
                '}';
    }
}

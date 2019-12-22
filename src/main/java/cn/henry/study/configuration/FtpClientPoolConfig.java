package cn.henry.study.configuration;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * description: ftp配置参数对象,继承自GenericObjectPoolConfig
 *
 * @author jelly
 * @citation Hlingoes
 * @date 2019/12/22 20:22
 */
@Component
@ConfigurationProperties(prefix = "spring.ftp.pool")
public class FtpClientPoolConfig extends GenericObjectPoolConfig {
    /**
     * 主机名
     */
    private String host;
    /**
     * 端口
     */
    private int port = 21;
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 连接超时时间 毫秒
     */
    private int connectTimeOut = 5000;
    /**
     * 控制连接的字符编码
     */
    private String controlEncoding = "utf-8";
    /**
     * 缓冲区大小
     */
    private int bufferSize = 1024;
    /**
     * 传输数据格式，2表binary二进制数据
     */
    private int fileType = 2;
    /**
     * 数据传输超时 毫秒
     */
    private int dataTimeout = 120000;
    private boolean useEPSVwithIPv4 = false;
    /**
     * 是否启用被动模式
     */
    private boolean passiveMode = true;
    /**
     * 连接耗尽时是否阻塞, false报异常,ture阻塞直到超时, 默认true
     */
    private boolean blockWhenExhausted = true;
    /**
     * 最大空闲等待时间(毫秒)，建议设置稍微长点，如90分钟
     */
    private long maxWaitMillis = 5400000;
    /**
     * 最大连接数
     */
    private int maxTotal = 50;
    /**
     * 最大空闲连接数
     */
    private int maxIdle = 50;
    /**
     * 最小空闲连接数
     */
    private int minIdle = 2;
    /**
     * 申请连接时 检测是否有效
     */
    private boolean testOnBorrow = true;
    /**
     * 返回连接时 检测是否有效
     */
    private boolean testOnReturn = true;
    /**
     * 创建连接时 检测是否有效
     */
    private boolean testOnCreate = true;
    /**
     * 空闲时检测连接是否有效
     */
    private boolean testWhileIdle = true;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
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

    public int getConnectTimeOut() {
        return connectTimeOut;
    }

    public void setConnectTimeOut(int connectTimeOut) {
        this.connectTimeOut = connectTimeOut;
    }

    public String getControlEncoding() {
        return controlEncoding;
    }

    public void setControlEncoding(String controlEncoding) {
        this.controlEncoding = controlEncoding;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public int getFileType() {
        return fileType;
    }

    public void setFileType(int fileType) {
        this.fileType = fileType;
    }

    public int getDataTimeout() {
        return dataTimeout;
    }

    public void setDataTimeout(int dataTimeout) {
        this.dataTimeout = dataTimeout;
    }

    public boolean isUseEPSVwithIPv4() {
        return useEPSVwithIPv4;
    }

    public void setUseEPSVwithIPv4(boolean useEPSVwithIPv4) {
        this.useEPSVwithIPv4 = useEPSVwithIPv4;
    }

    public boolean isPassiveMode() {
        return passiveMode;
    }

    public void setPassiveMode(boolean passiveMode) {
        this.passiveMode = passiveMode;
    }

    public boolean isBlockWhenExhausted() {
        return blockWhenExhausted;
    }

    @Override
    public void setBlockWhenExhausted(boolean blockWhenExhausted) {
        this.blockWhenExhausted = blockWhenExhausted;
    }

    @Override
    public long getMaxWaitMillis() {
        return maxWaitMillis;
    }

    @Override
    public void setMaxWaitMillis(long maxWaitMillis) {
        this.maxWaitMillis = maxWaitMillis;
    }

    @Override
    public int getMaxTotal() {
        return maxTotal;
    }

    @Override
    public void setMaxTotal(int maxTotal) {
        this.maxTotal = maxTotal;
    }

    @Override
    public int getMaxIdle() {
        return maxIdle;
    }

    @Override
    public void setMaxIdle(int maxIdle) {
        this.maxIdle = maxIdle;
    }

    @Override
    public int getMinIdle() {
        return minIdle;
    }

    @Override
    public void setMinIdle(int minIdle) {
        this.minIdle = minIdle;
    }

    public boolean isTestOnBorrow() {
        return testOnBorrow;
    }

    @Override
    public void setTestOnBorrow(boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
    }

    public boolean isTestOnReturn() {
        return testOnReturn;
    }

    @Override
    public void setTestOnReturn(boolean testOnReturn) {
        this.testOnReturn = testOnReturn;
    }

    public boolean isTestOnCreate() {
        return testOnCreate;
    }

    @Override
    public void setTestOnCreate(boolean testOnCreate) {
        this.testOnCreate = testOnCreate;
    }

    public boolean isTestWhileIdle() {
        return testWhileIdle;
    }

    @Override
    public void setTestWhileIdle(boolean testWhileIdle) {
        this.testWhileIdle = testWhileIdle;
    }
}

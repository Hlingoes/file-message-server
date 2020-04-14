package cn.henry.study.factory;

import cn.henry.study.configuration.FtpClientPoolConfig;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * description: ftpclient 工厂
 *
 * @author Hlingoes
 * @date 2019/12/22 20:46
 */
@Component
public class FtpClientFactory extends BasePooledObjectFactory<FTPClient> {
    private static final Logger LOGGER = LoggerFactory.getLogger(FtpClientFactory.class);

    private static String COMMAND = "OPTS UTF8";

    private static String ARGS = "ON";

    @Autowired
    private FtpClientPoolConfig ftpClientPoolConfig;

    /**
     * 新建对象
     */
    @Override
    public FTPClient create() throws Exception {
        FTPClient ftpClient = new FTPClient();
        ftpClient.setConnectTimeout(ftpClientPoolConfig.getConnectTimeOut());
        try {
            LOGGER.info("连接ftp服务器:" + ftpClientPoolConfig.getHost() + ":" + ftpClientPoolConfig.getPort());
            ftpClient.connect(ftpClientPoolConfig.getHost(), ftpClientPoolConfig.getPort());
            int reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
                LOGGER.error("FTPServer 拒绝连接");
                return null;
            }
            boolean result = ftpClient.login(ftpClientPoolConfig.getUsername(), ftpClientPoolConfig.getPassword());
            if (!result) {
                LOGGER.error("ftpClient登录失败!");
                throw new Exception("ftpClient登录失败! userName:" + ftpClientPoolConfig.getUsername() + ", password:"
                        + ftpClientPoolConfig.getPassword());
            }
            if (FTPReply.isPositiveCompletion(ftpClient.sendCommand(COMMAND, ARGS))) {
                // 开启服务器对UTF-8的支持，如果服务器支持就用UTF-8，否则使用本地编码(GBK)
                ftpClient.setControlEncoding("UTF-8");
                ftpClientPoolConfig.setControlEncoding("UTF-8");
            } else {
                ftpClient.setControlEncoding("GBK");
                ftpClientPoolConfig.setControlEncoding("GBK");
            }
            ftpClient.setControlEncoding(ftpClientPoolConfig.getControlEncoding());
            ftpClient.setBufferSize(ftpClientPoolConfig.getBufferSize());
            ftpClient.setFileType(ftpClientPoolConfig.getFileType());
            ftpClient.setDataTimeout(ftpClientPoolConfig.getDataTimeout());
            ftpClient.setUseEPSVwithIPv4(ftpClientPoolConfig.isUseEPSVwithIPv4());
            if (ftpClientPoolConfig.isPassiveMode()) {
                LOGGER.info("进入ftp被动模式");
                // 进入被动模式
                ftpClient.enterLocalPassiveMode();
            }
        } catch (IOException e) {
            LOGGER.error("FTP连接失败：", e);
        }
        return ftpClient;
    }

    @Override
    public PooledObject<FTPClient> wrap(FTPClient ftpClient) {
        return new DefaultPooledObject<FTPClient>(ftpClient);
    }

    /**
     * 销毁对象
     */
    @Override
    public void destroyObject(PooledObject<FTPClient> p) throws Exception {
        FTPClient ftpClient = p.getObject();
        ftpClient.logout();
        super.destroyObject(p);
    }

    /**
     * 验证对象
     */
    @Override
    public boolean validateObject(PooledObject<FTPClient> p) {
        FTPClient ftpClient = p.getObject();
        boolean connect = false;
        try {
            connect = ftpClient.sendNoOp();
        } catch (IOException e) {
            LOGGER.error("验证ftp连接对象,返回false");
        }
        return connect;
    }

    /**
     * description: 用于连接池中获取pool属性
     *
     * @param
     * @return cn.henry.study.configuration.FtpClientPoolConfig
     * @author Hlingoes 2019/12/22
     */
    public FtpClientPoolConfig getFtpClientPoolConfig() {
        return ftpClientPoolConfig;
    }
}

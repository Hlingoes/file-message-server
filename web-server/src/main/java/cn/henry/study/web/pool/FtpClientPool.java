package cn.henry.study.web.pool;

import cn.henry.study.web.factory.FtpClientFactory;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

/**
 * description: FTP 客户端连接池
 *
 * @author Hlingoes
 * @date 2019/12/22 20:56
 */
@Component
public class FtpClientPool {

    private static final Logger LOGGER = LoggerFactory.getLogger(FtpClientPool.class);

    /**
     * ftp客户端连接池
     */
    private GenericObjectPool<FTPClient> pool;

    @Autowired
    private FtpClientFactory ftpClientFactory;

    /**
     * description: 构造器注入，初始化连接池pool
     *
     * @param ftpClientFactory
     * @author Hlingoes 2019/12/23
     */
    public FtpClientPool(FtpClientFactory ftpClientFactory) {
        LOGGER.info("初始化ftpClientPool...");
        pool = new GenericObjectPool<>(ftpClientFactory, ftpClientFactory.getFtpClientPoolConfig());
    }

    @PreDestroy
    public void destroy() {
        if (pool != null) {
            pool.close();
            LOGGER.info("销毁ftpClientPool...");
        }
    }

    /**
     * description: 借  获取一个连接对象
     *
     * @return org.apache.commons.net.ftp.FTPClient
     * @author Hlingoes 2019/12/23
     */
    public FTPClient borrowObject() throws Exception {
        return pool.borrowObject();
    }

    /**
     * description: 还   归还一个连接对象
     *
     * @param ftpClient
     * @author Hlingoes 2019/12/23
     */
    public void returnObject(FTPClient ftpClient) {
        if (ftpClient != null) {
            pool.returnObject(ftpClient);
        }
    }
}

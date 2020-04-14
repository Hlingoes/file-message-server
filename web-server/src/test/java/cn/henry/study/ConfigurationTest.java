package cn.henry.study;

import cn.henry.study.configuration.DataSourceConfig;
import cn.henry.study.configuration.FtpClientPoolConfig;
import cn.henry.study.configuration.HttpClientPoolConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * description: 测试配置的读取
 *
 * @author Hlingoes
 * @date 2019/12/21 21:21
 */
@SpringBootTest(classes = WebMessageServer.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class ConfigurationTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationTest.class);

    @Autowired
    private HttpClientPoolConfig httpClientPoolConfig;

    @Autowired
    private FtpClientPoolConfig ftpClientPoolConfig;

    @Autowired
    private DataSourceConfig dataSourceConfig;

    /**
     * description: 测试将yml配置转为pojo对象，方便多个变量取值
     *
     * @return void
     * @author Hlingoes 2019/12/21
     */
    @Test
    public void testReadHttpClientPoolConfig() {
        LOGGER.info("httpClientPoolConfig: {}", httpClientPoolConfig);
    }

    @Test
    public void testReadFtpClientPoolConfig() {
        LOGGER.info("ftpClientPoolConfig: {}", ftpClientPoolConfig);
    }

    @Test
    public void testMultiDataSourceConfig() {
        LOGGER.info("multiDataSourceInitialConfig: {}", dataSourceConfig);
    }
}

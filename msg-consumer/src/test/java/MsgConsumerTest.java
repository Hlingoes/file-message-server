import cn.henry.study.common.utils.IpUtils;
import cn.henry.study.common.utils.JacksonUtils;
import cn.henry.study.consumer.MsgConsumerServer;
import com.xiaoju.uemc.tinyid.server.TinyIdGenerateServer;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

/**
 * description:
 *
 * @author Hlingoes
 * @date 2020/6/7 18:35
 */
@SpringBootTest(classes = MsgConsumerServer.class)
@RunWith(SpringRunner.class)
public class MsgConsumerTest {
    private static Logger logger = LoggerFactory.getLogger(MsgConsumerTest.class);

    @Autowired
    private TinyIdGenerateServer tinyIdGenerateServer;

    @Autowired
    private DiscoveryClient discoveryClient;

    @Value("${spring.application.name}")
    private String appName;

    @Test
    public void checkRunningService() {
        String localIpStr = IpUtils.getIpAddress();
        long localIpLong = IpUtils.ipToLong(localIpStr);
        List<ServiceInstance> allInstances = this.discoveryClient.getInstances(this.appName);
        if (CollectionUtils.isEmpty(allInstances)) {
            return;
        }
        long[] ipLongs = new long[allInstances.size()];
        for (int i = 0; i < allInstances.size(); i++) {
            logger.info("服务: {}", JacksonUtils.object2Str(allInstances.get(i)));
            String host = allInstances.get(i).getHost();
            ipLongs[i] = IpUtils.ipToLong(host);
        }
        Arrays.sort(ipLongs);
        if (localIpLong == ipLongs[0]) {
            logger.info("符合本机执行条件，服务appName={}，地址为：{}，正在执行任务...", this.appName, localIpStr);
        } else {
            logger.info("服务appName={}，本机不需要运行任务实例", this.appName);
        }
    }

    @Test
    public void nextIdTest() {
        for (int i = 0; i < 50; i++) {
            logger.info("aquire id at times {}, return: {}", i, this.tinyIdGenerateServer.nextId("test"));
        }
    }

}

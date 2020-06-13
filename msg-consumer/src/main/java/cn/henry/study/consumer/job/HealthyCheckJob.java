package cn.henry.study.consumer.job;

import cn.henry.study.common.utils.IpUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * description: 定时检查业务
 *
 * @author Hlingoes
 * @date 2020/6/12 23:58
 */
@Component
public class HealthyCheckJob {
    private static Logger logger = LoggerFactory.getLogger(HealthyCheckJob.class);

    @Autowired
    private DiscoveryClient discoveryClient;

    @Value("${spring.application.name}")
    private String appName;

    @Scheduled(cron = "0 0/1 * * * ?")
    public void checkRunningService() {
        String localIpStr = IpUtils.getIpAddress();
        long localIpLong = IpUtils.ipToLong(localIpStr);
        List<ServiceInstance> allInstances = this.discoveryClient.getInstances(this.appName);
        if (CollectionUtils.isEmpty(allInstances)) {
            return;
        }
        long[] ipLongs = new long[allInstances.size()];
        for (int i = 0; i < allInstances.size(); i++) {
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

}

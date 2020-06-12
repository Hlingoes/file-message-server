package cn.henry.study.consumer.job;

import cn.henry.study.common.utils.IpUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * description: 定时检查业务
 *
 * @author Hlingoes
 * @date 2020/6/12 23:58
 */
@Service
public class HealthyCheckJob {
    private static Logger logger = LoggerFactory.getLogger(HealthyCheckJob.class);

    @Autowired
    private DiscoveryClient discoveryClient;

    @Scheduled(cron = "* 0/30 * * * ?")
    public void checkRunningService(String serviceName) {
        if (!ipCompare(acquireClientUris(serviceName))) {
            return;
        }
        logger.info("{}服务，地址为：{}，正在执行任务...", serviceName, IpUtils.getIpAddress());
    }

    private List<URI> acquireClientUris(String serviceName) {
        List<ServiceInstance> serviceInstanceList = this.discoveryClient.getInstances(serviceName);
        List<URI> urlList = new ArrayList<URI>();
        if (!CollectionUtils.isEmpty(serviceInstanceList)) {
            serviceInstanceList.forEach(si -> {
                urlList.add(si.getUri());
            });
        }
        return urlList;
    }

    /**
     * 对比方法
     *
     * @param uris
     * @return
     */
    private boolean ipCompare(List<URI> uris) {
        try {
            String localIpStr = IpUtils.getIpAddress();
            long localIpLong = IpUtils.ipToLong(localIpStr);
            int size = uris.size();
            if (size == 0) {
                return false;
            }
            Long[] longHost = new Long[size];
            for (int i = 0; i < uris.size(); i++) {
                String host = uris.get(i).getHost();
                longHost[i] = IpUtils.ipToLong(host);
            }
            Arrays.sort(longHost);
            if (localIpLong == longHost[0]) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}

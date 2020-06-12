package cn.henry.study.eureka.listener;

import cn.henry.study.common.utils.JacksonUtils;
import com.netflix.appinfo.InstanceInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.eureka.server.event.*;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * description: 监听服务中心的事件
 *
 * @author Hlingoes
 * @date 2020/6/12 23:12
 */
@Component
public class EurekaStateListener {
    private static Logger logger = LoggerFactory.getLogger(EurekaStateListener.class);

    @EventListener
    public void listen(EurekaInstanceCanceledEvent event) {
        logger.info("服务:{}, 已下线", event);
    }

    @EventListener
    public void listen(EurekaInstanceRegisteredEvent event) {
        InstanceInfo instanceInfo = event.getInstanceInfo();
        logger.info("服务进行注册: {}", JacksonUtils.object2Str(instanceInfo));
    }

    @EventListener
    public void listen(EurekaInstanceRenewedEvent event) {
        InstanceInfo instanceInfo = event.getInstanceInfo();
        logger.info("服务进行续约: {}", JacksonUtils.object2Str(instanceInfo));
    }

    @EventListener
    public void listen(EurekaRegistryAvailableEvent event) {
        logger.info("注册中心启动,{}", System.currentTimeMillis());
    }

    @EventListener
    public void listen(EurekaServerStartedEvent event) {
        logger.info("注册中心服务端启动,{}", System.currentTimeMillis());
    }

}

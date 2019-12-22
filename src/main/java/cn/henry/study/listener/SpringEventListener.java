package cn.henry.study.listener;

import cn.henry.study.base.DefaultFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * description:
 *
 * @author Hlingoes
 * @date 2019/12/21 18:27
 */
@Component
public class SpringEventListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpringEventListener.class);
    private Map<Class, DefaultFileService> massFactory = new HashMap<>();

    @EventListener
    public void event(ApplicationReadyEvent event) {
        Map<String, DefaultFileService> map = event.getApplicationContext().getBeansOfType(DefaultFileService.class);
        map.forEach((key, value) -> {
            if (null != value.getEntityClazz()) {
                massFactory.put(value.getEntityClazz(), value);
            }
        });
        massFactory.forEach((key, valve) -> LOGGER.info("key: {}, valve: {}", key, valve.getClass()));
    }

}

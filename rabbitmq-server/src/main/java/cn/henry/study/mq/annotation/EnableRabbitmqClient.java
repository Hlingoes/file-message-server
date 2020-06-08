package cn.henry.study.mq.annotation;

import cn.henry.study.mq.config.RabbitmqClient;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * description:
 *
 * @author Hlingoes
 * @date 2020/6/9 0:00
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({RabbitmqClient.class})
public @interface EnableRabbitmqClient {
}

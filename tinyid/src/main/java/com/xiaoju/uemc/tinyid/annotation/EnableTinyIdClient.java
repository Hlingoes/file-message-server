package com.xiaoju.uemc.tinyid.annotation;

import com.xiaoju.uemc.tinyid.config.TinyIdClient;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * description:
 *
 * @author Hlingoes
 * @date 2020/6/7 19:18
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({TinyIdClient.class})
public @interface EnableTinyIdClient {
}

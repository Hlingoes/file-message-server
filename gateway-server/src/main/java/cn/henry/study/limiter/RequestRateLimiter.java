package cn.henry.study.limiter;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * description: 自定义限流标志的key，从exchange对象中获取服务ID、请求信息，用户信息等，
 * 可多维度设置限流规则
 *
 * @author Hlingoes
 * @date 2020/4/25 21:34
 */
@Component
public class RequestRateLimiter {
    /**
     * ip地址限流
     *
     * @return 限流key
     */
    @Bean
    public KeyResolver remoteAddressKeyResolver() {
        return exchange -> Mono.just(exchange.getRequest().getRemoteAddress().getHostName());
    }

    /**
     * 请求路径限流
     *
     * @return 限流key
     */
    @Bean
    public KeyResolver apiKeyResolver() {
        return exchange -> Mono.just(exchange.getRequest().getPath().value());
    }

    /**
     * username限流
     *
     * @return 限流key
     */
    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> Mono.just(exchange.getRequest().getQueryParams().getFirst("username"));
    }
}

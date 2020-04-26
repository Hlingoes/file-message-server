package cn.henry.study.gateway.strategy;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * description:
 *
 * @author Hlingoes
 * @date 2020/4/17 0:45
 */
public class UriKeyResolver implements KeyResolver {

    /**
     * description: 根据请求的 uri 限流
     *
     * @param exchange
     * @return reactor.core.publisher.Mono<java.lang.String>
     * @author Hlingoes 2020/4/17
     */
    @Override
    public Mono<String> resolve(ServerWebExchange exchange) {
        return Mono.just(exchange.getRequest().getURI().getPath());
    }
}

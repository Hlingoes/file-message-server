package cn.henry.study.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * description:
 *
 * @author Hlingoes
 * @date 2020/4/17 0:47
 */
public class TokenFilter implements GlobalFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 请求对象
        ServerHttpRequest request = exchange.getRequest();
        // 响应对象
        ServerHttpResponse response = exchange.getResponse();

        // 只有综合路由才添加这个全局过滤器（routesId：route_all）
        // 如果请求路径中不存在 routeAll 字符串
        if (request.getURI().toString().indexOf("routeAll") == -1) {
            System.out.println("filter -> return");
            // 直接跳出
            return chain.filter(exchange);
        }

        // 从请求中获取 token 参数
        String token = exchange.getRequest().getQueryParams().getFirst("token");
        // 如果为空，那么将返回 401
        if (token == null || token.isEmpty()) {
            byte[] bits = "test for trying".getBytes(StandardCharsets.UTF_8);
            DataBuffer buffer = response.bufferFactory().wrap(bits);
            // 设置响应对象状态码 401
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            // 设置响应对象内容并且指定编码，否则在浏览器中会中文乱码
            response.getHeaders().add("Content-Type", "text/plain;charset=UTF-8");
            // 返回响应对象
            return response.writeWith(Mono.just(buffer));
        }
        // 获取请求地址
        String beforePath = request.getPath().pathWithinApplication().value();
        // 获取响应状态码
        HttpStatus beforeStatusCode = response.getStatusCode();
        System.out.println("响应码：" + beforeStatusCode + "，请求路径：" + beforePath);
        // 请求前
        System.out.println("filter -> before");
        // 如果不为空，就通过
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            // 获取请求地址
            String afterPath = request.getPath().pathWithinApplication().value();
            // 获取响应状态码
            HttpStatus afterStatusCode = response.getStatusCode();
            System.out.println("响应码：" + afterStatusCode + "，请求路径：" + afterPath);
            // 响应后
            System.out.println("filter -> after");
        }));
    }
}

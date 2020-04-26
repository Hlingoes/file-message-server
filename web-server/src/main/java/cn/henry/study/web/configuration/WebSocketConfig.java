package cn.henry.study.web.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * description: 这个bean会自动注册使用了@ServerEndpoint注解声明的Websocket endpoint。
 * 如果使用独立的servlet容器，而不是直接使用springboot的内置容器，就不要注入ServerEndpointExporter
 * 使用外部tomcat容器启动websocket
 * 1.删除ServerEndpointExporter配置bean
 * 2.接收连接的类WebSocketService删除@Component
 *
 * @author Hlingoes
 * @date 2020/2/25 22:51
 */
@Configuration
public class WebSocketConfig {
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}

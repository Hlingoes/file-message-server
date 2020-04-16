package cn.henry.study;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * description:
 *
 * @author Hlingoes
 * @date 2020/4/17 0:21
 */
@SpringBootApplication
@EnableEurekaClient
public class GatewayServer {
    public static void main(String[] args) {
        SpringApplication.run(GatewayServer.class, args);
    }
}

package cn.henry.study.consumer;

import com.xiaoju.uemc.tinyid.annotation.EnableTinyIdClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * description:
 *
 * @author Hlingoes
 * @date 2020/4/16 0:32
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableTinyIdClient
public class MsgConsumerServer {
    public static void main(String[] args) {
        SpringApplication.run(MsgConsumerServer.class, args);
    }
}

package com.xiaoju.uemc.tinyid;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * description:
 *
 * @author Hlingoes
 * @date 2020/6/7 17:00
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class TinyIdApplication {
    public static void main(String[] args) {
        SpringApplication.run(TinyIdApplication.class);
    }
}

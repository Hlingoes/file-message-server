package cn.henry.study;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * description: 文件服务应用的启动类，包括http，ftp，rabbitmq系列服务
 *
 * @author Hlingoes
 * @date 2019/12/21 18:36
 */
@SpringBootApplication
public class FileMessageServer {
    public static void main(String[] args) {
        SpringApplication.run(FileMessageServer.class);
    }
}

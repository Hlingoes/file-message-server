package cn.henry.study.mq.sender;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * description:
 *
 * @author Hlingoes
 * @date 2020/6/8 22:07
 */
@Component
public class SimpleSender {

    @Resource(name = "firstRabbitTemplate")
    private RabbitTemplate firstRabbitTemplate;

    @Resource(name = "secondRabbitTemplate")
    private RabbitTemplate secondRabbitTemplate;

    public void firstSend(String queueName, String msg) {
        this.firstRabbitTemplate.convertAndSend(queueName, msg);
    }

    public void secondSend(String queueName, String msg) {
        this.secondRabbitTemplate.convertAndSend(queueName, msg);
    }
    
}

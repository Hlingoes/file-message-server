package cn.henry.study.mq.receiver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * description:
 *
 * @author Hlingoes
 * @date 2020/6/8 22:01
 */
@Component
public class SimpleReceiver {
    private static Logger logger = LoggerFactory.getLogger(SimpleReceiver.class);

    @RabbitListener(queues = {"queue_1"}, containerFactory = "firstListenerContainerFactory")
    public void firstConsumer(Message message, com.rabbitmq.client.Channel channel) {
        String msg = new String(message.getBody());
        try {
            logger.info("收到消息: {}", msg);
            // 手动ACK
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (IOException e) {
            logger.info("处理消息失败: {}", msg, e);
        }
    }

    @RabbitListener(queues = {"queue_2"}, containerFactory = "secondListenerContainerFactory")
    public void secondConsumer(Message message, com.rabbitmq.client.Channel channel) {
        String msg = new String(message.getBody());
        try {
            logger.info("收到消息: {}", msg);
            // 手动ACK
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (IOException e) {
            logger.info("处理消息失败: {}", msg, e);
        }
    }
}

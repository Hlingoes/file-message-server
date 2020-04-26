package cn.henry.study.web.service.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * 消息队列消费者
 *
 * @author zxf
 * @date 2019/8/15
 */
@Configuration
public class RabbitMqConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMqConsumer.class);

    @RabbitListener(queues = RabbitMqConstant.QUEUE_1)
    public void consumerOne(Message message, com.rabbitmq.client.Channel channel) {
        try {
            byte[] body = message.getBody();
            String json = new String(body);
            LOGGER.info("{}: 收到消息: {}", RabbitMqConstant.QUEUE_1, json);
            //手动ACK
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (IOException e) {
            LOGGER.info("{}: 处理消息失败", RabbitMqConstant.QUEUE_1, e);
        }
    }

    @RabbitListener(queues = RabbitMqConstant.QUEUE_2)
    public void consumerTwo(Message message, com.rabbitmq.client.Channel channel) {
        try {
            byte[] body = message.getBody();
            String json = new String(body);
            LOGGER.info("{}: 收到消息: {}", RabbitMqConstant.QUEUE_2, json);
            //手动ACK
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (IOException e) {
            LOGGER.info("{}: 处理消息失败", RabbitMqConstant.QUEUE_2, e);
        }
    }

    /**
     * 当死信队列的消息过期后,会通过死信交换机把过期消息发送到这里
     *
     * @param message
     * @param channel
     */
    @RabbitListener(queues = RabbitMqConstant.CONSUMER_BEAD_QUEUE)
    public void consumerThree(Message message, com.rabbitmq.client.Channel channel) {
        try {
            byte[] body = message.getBody();
            String json = new String(body);
            LOGGER.info("{}: 收到消息: {}", RabbitMqConstant.CONSUMER_BEAD_QUEUE, json);
            //手动ACK
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (IOException e) {
            LOGGER.info("{}: 处理消息失败", RabbitMqConstant.CONSUMER_BEAD_QUEUE, e);
        }
    }

}

package cn.henry.study.appication.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * 消息队列发送工具类
 *
 * @author zxf
 * @date 2019/8/15
 */
@Component
public class RabbitMqSend implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnCallback {

    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMqSend.class);

    private RabbitTemplate rabbitTemplate;

    private RabbitMqProperties config;


    @Autowired
    public RabbitMqSend(RabbitTemplate rabbitTemplate) {
        super();
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitTemplate.setMandatory(true);
        this.rabbitTemplate.setReturnCallback(this);
        this.rabbitTemplate.setConfirmCallback(this);
    }

    /**
     * description: 发布/订阅模式发送
     *
     * @param json
     * @return void
     * @author Hlingoes 2020/1/1
     */
    public void routeSend(String json) {
        Message message = this.setMessage(json);
        // 在fanoutExchange中在绑定Q到X上时，会自动把Q的名字当作bindingKey。
        this.rabbitTemplate.convertAndSend(RabbitMqConstant.FANOUT_EXCHANGE, "", message);
    }

    /**
     * description: 简单模式发送
     *
     * @param json
     * @return void
     * @author Hlingoes 2020/1/1
     */
    public void simpleSend(String json) {
        Message message = this.setMessage(json);
        this.rabbitTemplate.convertAndSend(RabbitMqConstant.QUEUE_1, message, new CorrelationData("123456"));
    }

    /**
     * description: 路由模式发送
     *
     * @param routingKey
     * @param json
     * @return void
     * @author Hlingoes 2020/1/1
     */
    public void routingSend(String routingKey, String json) {
        Message message = this.setMessage(json);
        this.rabbitTemplate.convertAndSend(RabbitMqConstant.DIRECT_EXCHANGE, routingKey, message);
    }

    /**
     * 主题模式发送
     *
     * @param routingKey
     * @param json
     */
    public void topicSend(String routingKey, String json) {
        Message message = this.setMessage(json);
        this.rabbitTemplate.convertAndSend(RabbitMqConstant.TOPIC_EXCHANGE, routingKey, message);
    }

    /**
     * 死信模式发送,用于定时任务处理
     *
     * @param routingKey
     * @param message
     */
    public void beadSend(String routingKey, Message message) {
        this.rabbitTemplate.convertAndSend(RabbitMqConstant.DEAD_EXCHANGE, routingKey, message);
    }

    /**
     * 设置消息参数
     *
     * @param json
     * @return
     */
    private Message setMessage(String json) {
        MessageProperties messageProperties = new MessageProperties();
        Message message = new Message(json.getBytes(), messageProperties);
        //消息持久化
        message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
        return message;
    }

    /**
     * 消息确认
     *
     * @param correlationData
     * @param ack
     * @param cause
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        if (ack) {
            LOGGER.info("发送成功");
        } else {
            LOGGER.info("发送失败");
        }
    }

    /**
     * description: 消息发送失败回传
     *
     * @param message
     * @param replyCode
     * @param replyText
     * @param exchange
     * @param routingKey
     * @return void
     * @author Hlingoes 2020/1/1
     */
    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        LOGGER.info("return message: {}, replyCode: {}, replyText: {}, exchange: {}, routingKey: {}",
                new String(message.getBody()), replyCode, replyText, exchange, routingKey);
    }

    /**
     * 扩展消息的CorrelationData，方便在回调中应用
     */
    public void setCorrelationData(String bizId, String coordinator, RabbitMqEventMessage msg, Integer retry) {
        rabbitTemplate.setCorrelationDataPostProcessor(((message, correlationData) ->
                new RabbitMqCorrelationDataExt(bizId, coordinator,
                        retry == null ? config.getDistributed().getCommitMaxRetries() : retry, msg)));
    }
}



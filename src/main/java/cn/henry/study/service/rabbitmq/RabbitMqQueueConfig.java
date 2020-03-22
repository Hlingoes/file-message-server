package cn.henry.study.service.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @消息队列配置
 * @author zxf
 * @date 2019/8/15
 */
@Configuration
public class RabbitMqQueueConfig {

    private  static final Logger logger = LoggerFactory.getLogger(RabbitMqQueueConfig.class);

    /**
     *  创建订阅模式交换机
     *  @return
     */
    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange(RabbitMqConstant.FANOUT_EXCHANGE,true, false);
    }

    /**
     * 创建路由模式交换机
     * @return
     */
    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(RabbitMqConstant.DIRECT_EXCHANGE,true, false);
    }

    /**
     * 创建主题模式交换机
     * @return
     */
    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(RabbitMqConstant.TOPIC_EXCHANGE,true, false);
    }

    /**
     * 创建死信交换机,跟普通交换机一样,只是死信交换机只用来接收过期的消息
     * @return
     */
    @Bean
    public DirectExchange deadExchange() {
        return new DirectExchange(RabbitMqConstant.DEAD_EXCHANGE, true, false);
    }

    /**
     * 创建死信队列,该队列没有消费者,消息会设置过期时间,消息过期后会发送到死信交换机,在由死信交换机转发至处理该消息的队列中
     * @return
     */
    @Bean
    public Queue BeadQueue() {
        Map<String, Object> arguments = new HashMap<>(2);
        // 死信路由到死信交换器DLX
        arguments.put("x-dead-letter-exchange", RabbitMqConstant.DEAD_EXCHANGE);
        arguments.put("x-dead-letter-routing-key", RabbitMqConstant.ROUTING_KEY2);
        return new Queue(RabbitMqConstant.DEAD_QUEUE, true, false, false, arguments);

    }

    /**
     * 处理死信队列的消费队列
     *
     */
    @Bean
    public Queue consumerBeadQueue() {
        return new Queue(RabbitMqConstant.CONSUMER_BEAD_QUEUE, true);

    }

    /**
     * 创建队列1
     * @return
     */
    @Bean
    public Queue Queue1() {
        //队列持久化
        return new Queue(RabbitMqConstant.QUEUE_1, true);
    }

    /**
     * 创建队列2
     * @return
     */
    @Bean
    public Queue Queue2() {
        return new Queue(RabbitMqConstant.QUEUE_2, true);
    }

    /**
     * 订阅模式队列1绑定交换机
     * @return
     */
    @Bean
    public Binding fanoutBinding1() {
        return BindingBuilder.bind(Queue1()).to(fanoutExchange());
    }

    /**
     * 订阅模式队列2绑定交换机
     * @return
     */
    @Bean
    public Binding fanoutBinding2() {
        return BindingBuilder.bind(Queue2()).to(fanoutExchange());
    }

    /**
     * 路由模式队列1绑定交换机,通过key1发送
     * @return
     */
    @Bean
    public Binding directBinding1() {
        return BindingBuilder.bind(Queue1()).to(directExchange()).with(RabbitMqConstant.ROUTING_KEY1);
    }

    /**
     * 路由模式队列2绑定交换机,通过key2发送
     * @return
     */
    @Bean
    public Binding directBinding2() {
        return BindingBuilder.bind(Queue2()).to(directExchange()).with(RabbitMqConstant.ROUTING_KEY2);
    }

    /**
     * 主题模式队列1绑定交换机
     * 符号“#”匹配一个或多个词，符号“*”匹配一个词。比如“hello.#”能够匹配到“hello.123.456”，但是“hello.*”只能匹配到“hello.123”
     * @return
     */
    @Bean
    public Binding topicBinding1() {
        return BindingBuilder.bind(Queue1()).to(topicExchange()).with(RabbitMqConstant.TOPIC_BINDING_KEY1);
    }

    /**
     * 主题模式队列1绑定交换机
     * 符号“#”匹配一个或多个词，符号“*”匹配一个词。比如“hello.#”能够匹配到“hello.123.456”，但是“hello.*”只能匹配到“hello.123”
     * @return
     */
    @Bean
    public Binding topicBinding2() {
        return BindingBuilder.bind(Queue2()).to(topicExchange()).with(RabbitMqConstant.TOPIC_BINDING_KEY2);
    }


    /**
     * 将死信队列与死信交换机绑定,key1
     *
     * @return
     */
    @Bean
    public Binding beadQueueBinding() {
        return BindingBuilder.bind(BeadQueue()).to(deadExchange()).with(RabbitMqConstant.ROUTING_KEY1);
    }

    /**
     * 将处理死信队列的消费队列与死信交换机绑定 key2
     *
     * @return
     */
    @Bean
    public Binding consumerBeadQueueBinding() {
        return BindingBuilder.bind(consumerBeadQueue()).to(deadExchange()).with(RabbitMqConstant.ROUTING_KEY2);
    }
}

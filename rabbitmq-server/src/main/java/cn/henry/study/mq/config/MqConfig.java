package cn.henry.study.mq.config;

import cn.henry.study.mq.entity.RabbitmqProps;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

/**
 * description:
 *
 * @author Hlingoes
 * @date 2020/6/8 21:36
 */
@Configuration
public class MqConfig {

    @Autowired
    private Environment environment;

    @Bean(name = "firstRabbitmqProps")
    public RabbitmqProps firstRabbitmqProps() {
        return Binder.get(environment).bind("spring.rabbitmq.first", Bindable.of(RabbitmqProps.class)).get();
    }

    @Bean(name = "firstConnectionFactory")
    @Primary
    public ConnectionFactory firstConnectionFactory(@Qualifier("firstRabbitmqProps") RabbitmqProps rabbitmqProps) {
        return connectionFactory(rabbitmqProps);
    }

    @Bean(name = "firstRabbitTemplate")
    public RabbitTemplate firstRabbitTemplate(@Qualifier("firstConnectionFactory") ConnectionFactory connectionFactory) {
        return new RabbitTemplate(connectionFactory);
    }

    @Bean(name = "firstListenerContainerFactory")
    public SimpleRabbitListenerContainerFactory firstListenerContainerFactory(
            SimpleRabbitListenerContainerFactoryConfigurer configurer,
            @Qualifier("firstConnectionFactory") ConnectionFactory connectionFactory,
            @Qualifier("firstRabbitmqProps") RabbitmqProps rabbitmqProps) {
        return simpleRabbitListenerContainerFactory(configurer, connectionFactory, rabbitmqProps);
    }

    @Bean(name = "secondRabbitmqProps")
    public RabbitmqProps secondRabbitmqProps() {
        return Binder.get(environment).bind("spring.rabbitmq.second", Bindable.of(RabbitmqProps.class)).get();
    }

    @Bean(name = "secondConnectionFactory")
    public ConnectionFactory secondConnectionFactory(@Qualifier("secondRabbitmqProps") RabbitmqProps rabbitmqProps) {
        return connectionFactory(rabbitmqProps);
    }

    @Bean(name = "secondRabbitTemplate")
    public RabbitTemplate secondRabbitTemplate(@Qualifier("secondConnectionFactory") ConnectionFactory connectionFactory) {
        return new RabbitTemplate(connectionFactory);
    }

    @Bean(name = "secondListenerContainerFactory")
    public SimpleRabbitListenerContainerFactory secondListenerContainerFactory(
            SimpleRabbitListenerContainerFactoryConfigurer configurer,
            @Qualifier("secondConnectionFactory") ConnectionFactory connectionFactory,
            @Qualifier("secondRabbitmqProps") RabbitmqProps rabbitmqProps) {
        return simpleRabbitListenerContainerFactory(configurer, connectionFactory, rabbitmqProps);
    }

    private ConnectionFactory connectionFactory(RabbitmqProps rabbitmqProps) {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(rabbitmqProps.getHost());
        connectionFactory.setPort(rabbitmqProps.getPort());
        connectionFactory.setUsername(rabbitmqProps.getUsername());
        connectionFactory.setPassword(rabbitmqProps.getPassword());
        return connectionFactory;
    }

    private SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory(
            SimpleRabbitListenerContainerFactoryConfigurer configurer,
            @Qualifier("secondConnectionFactory") ConnectionFactory connectionFactory,
            @Qualifier("secondRabbitmqProps") RabbitmqProps rabbitmqProps) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConcurrentConsumers(rabbitmqProps.getConcurrency());
        factory.setPrefetchCount(rabbitmqProps.getPrefetch());
        configurer.configure(factory, connectionFactory);
        return factory;
    }

}

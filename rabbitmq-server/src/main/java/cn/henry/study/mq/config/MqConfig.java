package cn.henry.study.mq.config;

import cn.henry.study.mq.entity.RabbitmqProps;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * description:
 *
 * @author Hlingoes
 * @date 2020/6/8 21:36
 */
@Configuration
public class MqConfig {

    @Bean(name = "firstRabbitmqProps")
    @Primary
    public RabbitmqProps firstRabbitmqProps(@Value("${spring.rabbitmq.first.host}") String host,
                                            @Value("${spring.rabbitmq.first.port}") int port,
                                            @Value("${spring.rabbitmq.first.username}") String username,
                                            @Value("${spring.rabbitmq.first.password}") String password,
                                            @Value("${spring.rabbitmq.first.concurrency}") int concurrency,
                                            @Value("${spring.rabbitmq.first.prefetch}") int prefetch
    ) {
        return new RabbitmqProps(host, port, username, password, concurrency, prefetch);
    }

    @Bean(name = "firstConnectionFactory")
    @Primary
    public ConnectionFactory firstConnectionFactory(@Qualifier("firstRabbitmqProps") RabbitmqProps rabbitmqProps
    ) {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(rabbitmqProps.getHost());
        connectionFactory.setPort(rabbitmqProps.getPort());
        connectionFactory.setUsername(rabbitmqProps.getUsername());
        connectionFactory.setPassword(rabbitmqProps.getPassword());
        return connectionFactory;
    }

    @Bean(name = "firstRabbitTemplate")
    public RabbitTemplate firstRabbitTemplate(@Qualifier("firstConnectionFactory") ConnectionFactory connectionFactory) {
        return new RabbitTemplate(connectionFactory);
    }

    @Bean(name = "firstListenerContainerFactory")
    public SimpleRabbitListenerContainerFactory firstListenerContainerFactory(
            SimpleRabbitListenerContainerFactoryConfigurer configurer,
            @Qualifier("firstConnectionFactory") ConnectionFactory connectionFactory,
            @Qualifier("firstRabbitmqProps") RabbitmqProps rabbitmqProps
    ) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConcurrentConsumers(rabbitmqProps.getConcurrency());
        factory.setPrefetchCount(rabbitmqProps.getPrefetch());
        configurer.configure(factory, connectionFactory);
        return factory;
    }

    @Bean(name = "secondRabbitmqProps")
    public RabbitmqProps secondRabbitmqProps(@Value("${spring.rabbitmq.second.host}") String host,
                                             @Value("${spring.rabbitmq.second.port}") int port,
                                             @Value("${spring.rabbitmq.second.username}") String username,
                                             @Value("${spring.rabbitmq.second.password}") String password,
                                             @Value("${spring.rabbitmq.second.concurrency}") int concurrency,
                                             @Value("${spring.rabbitmq.second.prefetch}") int prefetch
    ) {
        return new RabbitmqProps(host, port, username, password, concurrency, prefetch);
    }

    @Bean(name = "secondConnectionFactory")
    public ConnectionFactory secondConnectionFactory(@Qualifier("secondRabbitmqProps") RabbitmqProps rabbitmqProps) {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(rabbitmqProps.getHost());
        connectionFactory.setPort(rabbitmqProps.getPort());
        connectionFactory.setUsername(rabbitmqProps.getUsername());
        connectionFactory.setPassword(rabbitmqProps.getPassword());
        return connectionFactory;
    }

    @Bean(name = "secondRabbitTemplate")
    public RabbitTemplate secondRabbitTemplate(@Qualifier("secondConnectionFactory") ConnectionFactory connectionFactory) {
        return new RabbitTemplate(connectionFactory);
    }

    @Bean(name = "secondListenerContainerFactory")
    public SimpleRabbitListenerContainerFactory secondListenerContainerFactory(
            SimpleRabbitListenerContainerFactoryConfigurer configurer,
            @Qualifier("secondConnectionFactory") ConnectionFactory connectionFactory,
            @Qualifier("secondRabbitmqProps") RabbitmqProps rabbitmqProps
    ) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConcurrentConsumers(rabbitmqProps.getConcurrency());
        factory.setPrefetchCount(rabbitmqProps.getPrefetch());
        configurer.configure(factory, connectionFactory);
        return factory;
    }
}

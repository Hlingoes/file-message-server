package cn.henry.study.mq.utils;

import cn.henry.study.common.utils.JacksonUtils;
import cn.henry.study.mq.entity.RabbitmqProps;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * description:
 *
 * @author Hlingoes
 * @citation https://blog.csdn.net/qq_33399709/article/details/91511044
 * @date 2020/6/8 22:51
 */
public class RabbitMqUtils {
    private static Logger logger = LoggerFactory.getLogger(RabbitMqUtils.class);

    private RabbitAdmin rabbitAdmin;

    private RabbitTemplate rabbitTemplate;

    public RabbitMqUtils(RabbitTemplate rabbitTemplate) {
        this.rabbitAdmin = new RabbitAdmin(rabbitTemplate);
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * 转换Message对象
     *
     * @param messageType 返回消息类型 MessageProperties类中常量
     * @param msg
     * @return
     */
    public Message getMessage(String messageType, Object msg) {
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType(messageType);
        Message message = new Message(msg.toString().getBytes(), messageProperties);
        return message;
    }

    /**
     * 有绑定Key的Exchange发送
     *
     * @param routingKey
     * @param msg
     */
    public void sendMessageToExchange(TopicExchange topicExchange, String routingKey, Object msg) {
        Message message = getMessage(MessageProperties.CONTENT_TYPE_JSON, msg);
        rabbitTemplate.send(topicExchange.getName(), routingKey, message);
    }

    /**
     * 没有绑定KEY的Exchange发送
     *
     * @param exchange
     * @param msg
     */
    public void sendMessageToExchange(TopicExchange topicExchange, AbstractExchange exchange, String msg) {
        addExchange(exchange);
        logger.info("RabbitMQ send exchange: {} -> {}", exchange.getName(), msg);
        rabbitTemplate.convertAndSend(topicExchange.getName(), msg);
    }

    /**
     * 给queue发送消息
     *
     * @param queueName
     * @param msg
     */
    public void sendToQueue(String queueName, String msg) {
        sendToQueue(DirectExchange.DEFAULT, queueName, msg);
    }

    /**
     * 给direct交换机指定queue发送消息
     *
     * @param directExchange
     * @param queueName
     * @param msg
     */
    public void sendToQueue(DirectExchange directExchange, String queueName, String msg) {
        Queue queue = new Queue(queueName);
        addQueue(queue);
        Binding binding = BindingBuilder.bind(queue).to(directExchange).withQueueName();
        rabbitAdmin.declareBinding(binding);
        // 设置消息内容的类型，默认是 application/octet-stream 会是 ASCII 码值
        rabbitTemplate.convertAndSend(directExchange.getName(), queueName, msg);
    }

    /**
     * 给queue发送消息
     *
     * @param queueName
     */
    public String receiveFromQueue(String queueName) {
        return receiveFromQueue(DirectExchange.DEFAULT, queueName);
    }

    /**
     * 给direct交换机指定queue发送消息
     *
     * @param directExchange
     * @param queueName
     */
    public String receiveFromQueue(DirectExchange directExchange, String queueName) {
        Queue queue = new Queue(queueName);
        addQueue(queue);
        Binding binding = BindingBuilder.bind(queue).to(directExchange).withQueueName();
        rabbitAdmin.declareBinding(binding);
        String messages = (String) rabbitTemplate.receiveAndConvert(queueName);
        System.out.println("Receive:" + messages);
        return messages;
    }

    /**
     * 创建Exchange
     *
     * @param exchange
     */
    public void addExchange(AbstractExchange exchange) {
        rabbitAdmin.declareExchange(exchange);
    }

    /**
     * 删除一个Exchange
     *
     * @param exchangeName
     */
    public boolean deleteExchange(String exchangeName) {
        return rabbitAdmin.deleteExchange(exchangeName);
    }


    /**
     * Declare a queue whose name is automatically named. It is created with exclusive = true, autoDelete=true, and
     * durable = false.
     *
     * @return Queue
     */
    public Queue addQueue() {
        return rabbitAdmin.declareQueue();
    }

    /**
     * 创建一个指定的Queue
     *
     * @param queue
     * @return queueName
     */
    public String addQueue(Queue queue) {
        return rabbitAdmin.declareQueue(queue);
    }

    /**
     * Delete a queue.
     *
     * @param queueName the name of the queue.
     * @param unused    true if the queue should be deleted only if not in use.
     * @param empty     true if the queue should be deleted only if empty.
     */
    public void deleteQueue(String queueName, boolean unused, boolean empty) {
        rabbitAdmin.deleteQueue(queueName, unused, empty);
    }

    /**
     * 删除一个queue
     *
     * @param queueName
     * @return true if the queue existed and was deleted.
     */
    public boolean deleteQueue(String queueName) {
        return rabbitAdmin.deleteQueue(queueName);
    }

    /**
     * 绑定一个队列到一个匹配型交换器使用一个routingKey
     *
     * @param queue
     * @param exchange
     * @param routingKey
     */
    public void addBinding(Queue queue, TopicExchange exchange, String routingKey) {
        Binding binding = BindingBuilder.bind(queue).to(exchange).with(routingKey);
        rabbitAdmin.declareBinding(binding);
    }

    /**
     * 绑定一个Exchange到一个匹配型Exchange 使用一个routingKey
     *
     * @param exchange
     * @param topicExchange
     * @param routingKey
     */
    public void addBinding(Exchange exchange, TopicExchange topicExchange, String routingKey) {
        Binding binding = BindingBuilder.bind(exchange).to(topicExchange).with(routingKey);
        rabbitAdmin.declareBinding(binding);
    }

    /**
     * 去掉一个binding
     *
     * @param binding
     */
    public void removeBinding(Binding binding) {
        rabbitAdmin.removeBinding(binding);
    }

    /**
     * 获得CloseableHttpClient对象，通过basic认证。
     *
     * @param username
     * @param password
     * @return
     * @citation https://blog.csdn.net/ultingCSDN/article/details/84862630
     */
    private CloseableHttpClient getBasicHttpClient(String username, String password) {
        // 创建HttpClientBuilder
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        // 设置BasicAuth
        CredentialsProvider provider = new BasicCredentialsProvider();
        AuthScope scope = new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT, AuthScope.ANY_REALM);
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);
        provider.setCredentials(scope, credentials);
        httpClientBuilder.setDefaultCredentialsProvider(provider);
        return httpClientBuilder.build();
    }

    /**
     * 根据API获得相关的MQ信息
     *
     * @param url
     * @param username
     * @param password
     * @return
     * @citation https://blog.csdn.net/ultingCSDN/article/details/84862630
     */
    private String getMqMsg(String url, String username, String password) {
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpClient httpClient = getBasicHttpClient(username, password);
        // 设置超时时间
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(3000).setConnectionRequestTimeout(1000)
                .setSocketTimeout(3000).build();
        httpGet.setConfig(requestConfig);

        CloseableHttpResponse response = null;
        String msg = null;
        try {
            response = httpClient.execute(httpGet);
            StatusLine status = response.getStatusLine();
            int state = status.getStatusCode();
            if (state == HttpStatus.SC_OK) {
                msg = EntityUtils.toString(response.getEntity());
            } else {
                logger.info("请求url: {}, status: {}", url, state);
            }
        } catch (Exception e) {
            logger.error("请求异常, url: {}", url, e);
        } finally {
            closeAll(response, httpClient);
        }
        return msg;
    }

    /**
     * description: 关闭通信
     *
     * @param response
     * @param httpClient
     * @return void
     * @author Hlingoes 2020/6/8
     * @citation https://blog.csdn.net/ultingCSDN/article/details/84862630
     */
    private static void closeAll(CloseableHttpResponse response, CloseableHttpClient httpClient) {
        if (response != null) {
            try {
                response.close();
            } catch (IOException e) {
                logger.error("response关闭异常", e);
            }
        }
        try {
            httpClient.close();
        } catch (IOException e) {
            logger.error("http请求关闭异常", e);
        }
    }

    /**
     * description: 获取rabbitmq中的所有队列
     *
     * @param rabbitmqProps
     * @return java.util.List<java.lang.String>
     * @author Hlingoes 2020/6/8
     */
    public List<String> getAllQueuesFromHttp(RabbitmqProps rabbitmqProps) {
        String url = String.format("http://%s:%d/api/queues", rabbitmqProps.getHost(), rabbitmqProps.getHttpPort());
        String msg = getMqMsg(url, rabbitmqProps.getUsername(), rabbitmqProps.getPassword());
        List<String> queues = new ArrayList<>();
        JsonNode arrNode = null;
        try {
            arrNode = JacksonUtils.MAPPER.readTree(msg);
            for (JsonNode objNode : arrNode) {
                if (objNode.has("name")) {
                    queues.add(objNode.get("name").toString());
                }
            }
        } catch (IOException e) {
            logger.error("Jackson转换异常", e);
        }
        return queues;
    }
}

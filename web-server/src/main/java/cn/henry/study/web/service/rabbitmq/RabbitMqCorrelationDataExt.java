package cn.henry.study.web.service.rabbitmq;

import org.springframework.amqp.rabbit.support.CorrelationData;

/**
 * 扩展 CorrelationData
 *
 * @author zxf
 */
public class RabbitMqCorrelationDataExt extends CorrelationData {

    private String coordinator;

    private Integer maxRetries;

    private RabbitMqEventMessage message;

    public RabbitMqCorrelationDataExt(String id, String coordinator, Integer maxRetries, RabbitMqEventMessage message) {
        super(id);
        this.coordinator = coordinator;
        this.maxRetries = maxRetries;
        this.message = message;
    }

    public String getCoordinator() {
        return coordinator;
    }

    public void setCoordinator(String coordinator) {
        this.coordinator = coordinator;
    }

    public Integer getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(Integer maxRetries) {
        this.maxRetries = maxRetries;
    }

    public RabbitMqEventMessage getMessage() {
        return message;
    }

    public void setMessage(RabbitMqEventMessage message) {
        this.message = message;
    }
}

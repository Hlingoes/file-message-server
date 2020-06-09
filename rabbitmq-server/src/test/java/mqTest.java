import cn.henry.study.common.utils.JacksonUtils;
import cn.henry.study.mq.RabbitMqApplication;
import cn.henry.study.mq.entity.RabbitmqProps;
import cn.henry.study.mq.sender.SimpleSender;
import cn.henry.study.mq.utils.RabbitMqUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

/**
 * description:
 *
 * @author Hlingoes
 * @date 2020/6/8 22:11
 */
@SpringBootTest(classes = RabbitMqApplication.class)
@RunWith(SpringRunner.class)
public class mqTest {
    private static Logger logger = LoggerFactory.getLogger(mqTest.class);

    @Autowired
    private SimpleSender simpleSender;

    @Resource(name = "firstRabbitmqProps")
    private RabbitmqProps rabbitmqProps;

    @Resource(name = "firstRabbitTemplate")
    private RabbitTemplate firstRabbitTemplate;

    @Test
    public void sendTest() {
        this.simpleSender.firstSend("queue_1", "queue_1 begin to miss you");
        this.simpleSender.secondSend("queue_2", "queue_2 begin to miss you");
    }

    @Test
    public void mqHttpApiTest() {
        RabbitMqUtils rabbitMqUtils = new RabbitMqUtils(firstRabbitTemplate);
        List<String> queues = rabbitMqUtils.getAllQueuesFromHttp(rabbitmqProps);
        logger.info("queues: {}", queues);
    }
}
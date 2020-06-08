import cn.henry.study.consumer.MsgConsumerServer;
import cn.henry.study.mq.entity.RabbitmqProps;
import cn.henry.study.mq.utils.RabbitMqUtils;
import com.xiaoju.uemc.tinyid.server.TinyIdGenerateServer;
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
 * @date 2020/6/7 18:35
 */
@SpringBootTest(classes = MsgConsumerServer.class)
@RunWith(SpringRunner.class)
public class MsgConsumerTest {
    private static Logger logger = LoggerFactory.getLogger(MsgConsumerTest.class);

    @Autowired
    private TinyIdGenerateServer tinyIdGenerateServer;

    @Resource(name = "firstRabbitmqProps")
    private RabbitmqProps rabbitmqProps;

    @Resource(name = "firstRabbitTemplate")
    private RabbitTemplate firstRabbitTemplate;

    @Test
    public void nextIdTest() {
        for (int i = 0; i < 50; i++) {
            logger.info("aquire id at times {}, return: {}", i, this.tinyIdGenerateServer.nextId("test"));
        }
    }

    @Test
    public void mqHttpApiTest() {
        RabbitMqUtils rabbitMqUtils = new RabbitMqUtils(firstRabbitTemplate);
        List<String> queues = rabbitMqUtils.getAllQueuesFromHttp(rabbitmqProps);
        logger.info("queues: {}", queues);
    }

}

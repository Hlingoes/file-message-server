package cn.henry.study.mq;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.OptionHelper;
import cn.henry.study.common.enums.LogNameEnum;
import cn.henry.study.common.utils.LoggerUtils;
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

    @Test
    public void loggerUtilsTest() {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        String oph = OptionHelper.substVars("${LOG_HOME}/test-log.log", context);
        logger.info("这个就没问题");
        LoggerUtils.getLogger(LogNameEnum.TEST, mqTest.class).info("1#####{}####info{}", oph, mqTest.class);
        LoggerUtils.getLogger(LogNameEnum.TEST, LoggerUtils.class).info("2#####{}####info{}", oph, LoggerUtils.class);
        LoggerUtils.getLogger(LogNameEnum.TEST, mqTest.class).error("1#####{}####error{}", oph, mqTest.class);
        LoggerUtils.getLogger(LogNameEnum.TEST, LoggerUtils.class).error("2#####{}####error{}", oph, LoggerUtils.class);
    }
}

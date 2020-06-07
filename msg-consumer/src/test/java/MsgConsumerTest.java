import cn.henry.study.consumer.MsgConsumerServer;
import com.xiaoju.uemc.tinyid.server.TinyIdGenerateServer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

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

    @Test
    public void nextIdTest() {
        for (int i = 0; i < 50; i++) {
            logger.info("aquire id at times {}, return: {}", i, this.tinyIdGenerateServer.nextId("test"));
        }
    }

}

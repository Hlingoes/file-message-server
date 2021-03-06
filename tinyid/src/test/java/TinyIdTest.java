import com.xiaoju.uemc.tinyid.TinyIdApplication;
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
 * @date 2020/6/7 12:22
 */
@SpringBootTest(classes = TinyIdApplication.class)
@RunWith(SpringRunner.class)
public class TinyIdTest {
    private static Logger logger = LoggerFactory.getLogger(TinyIdTest.class);

    @Autowired
    private TinyIdGenerateServer tinyIdGenerateServer;

    @Test
    public void nextIdTest() {
        for (int i = 0; i < 50; i++) {
            logger.info("aquire id at times {}, return: {}", i, this.tinyIdGenerateServer.nextId("test"));
        }
    }

    @Test
    public void nextIdBatchTest() {
        logger.info("aquire ids: {}", this.tinyIdGenerateServer.nextId("test", 10));
    }
}

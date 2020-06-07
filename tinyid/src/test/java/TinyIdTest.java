import com.xiaoju.uemc.tinyid.TinyIdServer;
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
@RunWith(SpringRunner.class)
public class TinyIdTest {
    private static Logger logger = LoggerFactory.getLogger(TinyIdTest.class);

    @Autowired
    private TinyIdServer tinyIdServer;

    @Test
    public void nextIdTest() {
        logger.info("aquire id: {}", this.tinyIdServer.nextId("test"));
    }
}

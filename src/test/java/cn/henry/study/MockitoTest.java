package cn.henry.study;

import cn.henry.study.application.FtpService;
import cn.henry.study.entity.Book;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

/**
 * description:
 *
 * @author Hlingoes
 * @date 2020/3/3 1:25
 */
@RunWith(value = SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = FileMessageServer.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MockitoTest {
    /**
     * description: 对被测类中@Autowired的对象，用@Mocks标注；对被测类自己，用@InjectMocks标注
     */
    @Mock
    private Book book;

    /**
     * description:  此处必须给具体的实现，不能是接口
     */
    @InjectMocks
    FtpService ftpService;

    @Test
    public void testFtpService() {
        // 将ftpService部分mock化
        ftpService = spy(ftpService);
        String path = "/test";
        // 这里必须用doReturn()而不能是when().thenReturn()
        doReturn(true).when(ftpService).delete(path);
        boolean result = ftpService.delete(path);
        Assert.assertEquals(true, result);
    }
}

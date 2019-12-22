package cn.henry.study;

import cn.henry.study.appication.HttpClientTemplateService;
import cn.henry.study.entity.Book;
import cn.henry.study.pool.HttpClientDownloadPool;
import cn.henry.study.utils.JacksonUtils;
import org.apache.catalina.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * description: http服务的测试用例
 *
 * @author Hlingoes 2019/12/21
 */
@SpringBootTest(classes = FileMessageServer.class)
@RunWith(SpringRunner.class)
public class HttpTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpTest.class);

    @Resource(name = "restTemplate")
    private RestTemplate restTemplate;

    @Autowired
    private HttpClientTemplateService httpClientTemplate;

    @Autowired
    private HttpClientDownloadPool httpDownloadPool;

    public void testLogin() {
        String url = "http://localhost:8181/UserApplication/FormUserManager/login";
        MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
        formData.add("name", "suns1");
        formData.add("password", "123456");
        User loginUser = restTemplate.postForObject(url, formData, User.class);
        System.out.println(loginUser);
    }

    public void testDeleteUserById() {
        String url = "http://localhost:8181/UserApplication/FormUserManager/deleteUserById?ids={ids}";
        restTemplate.delete(url, new int[]{13, 14});
    }

    public void testQueryById() {
        String url = "http://localhost:8181/UserApplication/FormUserManager/queryUserById?id={id}";
        User user = restTemplate.getForObject(url, User.class, 1);
        System.out.println(user);
    }

    public void testQueryByPage() {
        String url = "http://localhost:8181/UserApplication/FormUserManager/queryByPage?pageNow={pageNow}&pageSize={pageSize}&column={column}&value={value}";
        Map<String, Object> params = new HashMap<>();
        params.put("pageNow", "1");
        params.put("pageSize", "5");
        params.put("column", "name");
        params.put("value", "suns1");
        List<User> users = restTemplate.getForObject(url, List.class, params);
        for (int i = 0; i < users.size(); i++) {
            System.out.println(users.get(i));
        }
    }

    public void testUpload() {
        String url = "http://localhost:9111/uploadFile";
        MultiValueMap<String, Object> multiValueMap = new LinkedMultiValueMap<>();
        FileSystemResource fileUpload = new FileSystemResource(new File("G:\\下载\\1B22.pdf"));
        multiValueMap.add("file", fileUpload);
        multiValueMap.add("fileName", "hulin-1B22.pdf");
        restTemplate.postForObject(url, multiValueMap, String.class);
    }

    public void testDownload(){
        String path = "G:\\迅雷下载";
        String url = "http://d2.11684.com/jc-srRabbitMQpdf_20190-11684.com.rar";
        httpDownloadPool.downloadByMultithread(url, path, 10);
    }

    @Test
    public void testReflection(){
        LOGGER.info(JacksonUtils.object2Str(httpClientTemplate.getFormFields(Book.class)));
    }
}

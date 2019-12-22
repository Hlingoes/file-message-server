package cn.henry.study.appication;

import cn.henry.study.base.DefaultFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * description: 使用RestTemplate实现的文件操作
 *
 * @author Hlingoes
 * @date 2019/12/21 18:06
 */
@Service
public class RestTemplateService extends DefaultFileService {

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public Class<?> getEntityClazz() {
        return restTemplate.getClass();
    }
}

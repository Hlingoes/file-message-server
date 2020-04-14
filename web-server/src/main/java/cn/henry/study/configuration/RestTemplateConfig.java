package cn.henry.study.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * description: RestTemplate默认是使用JDK原生的URLConnection,默认超时为-1, 表示没有超时时间
 *
 * @author Hlingoes
 * @date 2019/12/21 20:55
 */
@Configuration
public class RestTemplateConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate(simpleClientHttpRequestFactory());
    }

    @Bean
    public ClientHttpRequestFactory simpleClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        //单位为ms
        factory.setReadTimeout(30 * 1000);
        //单位为ms
        factory.setConnectTimeout(20 * 1000);
        factory.setBufferRequestBody(false);
        return factory;
    }

}

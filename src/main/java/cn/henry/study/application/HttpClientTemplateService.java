package cn.henry.study.application;

import cn.henry.study.base.DefaultFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

/**
 * description: 使用带连接池的自定义HttpTemplate服务
 *
 * @author Hlingoes
 * @date 2019/12/21 22:26
 */
@Service
public class HttpClientTemplateService extends DefaultFileService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientTemplateService.class);

    /**
     * 使用自定义的httpclient的restTemplate
     */
    @Resource(name = "httpClientTemplate")
    private RestTemplate httpClientTemplate;

    /**
     * description: 下载小文件,采用字节数组的方式,直接将所有返回都放入内存中,容易引发内存溢出
     *
     * @param url
     * @param targetDir
     * @return void
     * @author Hlingoes 2019/12/21
     */
    public void downloadLittleFile(String url, String targetDir) {
        downloadLittleFile(url, targetDir, null);
    }

    /**
     * 下载小文件,直接将所有返回都放入内存中,容易引发内存溢出
     *
     * @param url
     * @param targetDir
     */
    public void downloadLittleFile(String url, String targetDir, Map<String, String> params) {
        Instant now = Instant.now();
        String completeUrl = addGetQueryParam(url, params);
        ResponseEntity<byte[]> rsp = httpClientTemplate.getForEntity(completeUrl, byte[].class);
        LOGGER.info("[下载文件] [状态码] code:{}", rsp.getStatusCode());
        try {
            String path = getAndCreateDownloadDir(url, targetDir);
            Files.write(Paths.get(path), Objects.requireNonNull(rsp.getBody(), "未获取到下载文件"));
        } catch (IOException e) {
            LOGGER.error("[下载文件] 写入失败:", e);
        }
        LOGGER.info("[下载文件] 完成,耗时:{}", ChronoUnit.MILLIS.between(now, Instant.now()));
    }

    /**
     * 下载大文件,使用流接收
     *
     * @param url
     * @param targetDir
     */
    public void downloadBigFile(String url, String targetDir){
        downloadBigFile(url,targetDir,null);
    }
    /**
     * 下载大文件,使用流接收
     *
     * @param url
     * @param targetDir
     */
    public void downloadBigFile(String url, String targetDir, Map<String, String> params) {
        Instant now = Instant.now();
        String completeUrl = addGetQueryParam(url, params);
        try {
            String path = getAndCreateDownloadDir(url, targetDir);
            //定义请求头的接收类型
            RequestCallback requestCallback = request -> request.getHeaders()
                    .setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM, MediaType.ALL));
            // getForObject会将所有返回直接放到内存中,使用流来替代这个操作
            ResponseExtractor<Void> responseExtractor = response -> {
                // Here I write the response to a file but do what you like
                Files.copy(response.getBody(), Paths.get(path));
                return null;
            };
            httpClientTemplate.execute(completeUrl, HttpMethod.GET, requestCallback, responseExtractor);
        } catch (IOException e) {
            LOGGER.error("[下载文件] 写入失败:", e);
        }
        LOGGER.info("[下载文件] 完成,耗时:{}", ChronoUnit.MILLIS.between(now, Instant.now()));
    }

    /**
     * 拼接get请求参数
     *
     * @param url
     * @param params
     * @return
     */
    private String addGetQueryParam(String url, Map<String, String> params) {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(url);
        if (!CollectionUtils.isEmpty(params)) {
            params.forEach((k, v) ->{
                uriComponentsBuilder.queryParam(k, v);
            });
        }
        return uriComponentsBuilder.build().encode().toString();
    }

    /**
     * 创建或获取下载文件夹的路径
     *
     * @param url
     * @param targetDir
     * @return
     */
    public String getAndCreateDownloadDir(String url, String targetDir) throws IOException {
        String filename = url.substring(url.lastIndexOf("/") + 1);
        int index = url.indexOf("?");
        if (index != -1) {
            filename = filename.substring(0, index);
        }
        if (!Files.exists(Paths.get(targetDir))) {
            Files.createDirectories(Paths.get(targetDir));
        }
        return targetDir.endsWith("/") ? targetDir + filename : targetDir + "/" + filename;
    }

    @Override
    public Class<?> getEntityClazz() {
        return httpClientTemplate.getClass();
    }
}

package cn.henry.study.web.service.files;

import cn.henry.study.common.bo.PartitionElements;
import cn.henry.study.common.service.OperationThreadService;
import cn.henry.study.common.utils.MultiOperationThreadUtils;
import cn.henry.study.common.utils.ThreadPoolExecutorUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ExecutorService;

/**
 * description: 使用带连接池的自定义HttpTemplate服务
 * 调用HEAD方法去获取到文件大小,
 * 在请求头中设置Range属性,每个线程分别去下载属于它那一部分的数据,
 * 最后合并成一个文件
 *
 * @author Hlingoes
 * @date 2019/12/21 22:26
 */
@Service
public class HttpClientTemplateService extends DefaultFileService implements OperationThreadService {
    private static Logger logger = LoggerFactory.getLogger(HttpClientTemplateService.class);

    private static final int ONE_KB_SIZE = 1024;
    /**
     * 大于20M的文件视为大文件,采用流下载
     */
    private static final int BIG_FILE_SIZE = 20 * 1024 * 1024;
    /**
     * 使用自定义的httpclient的restTemplate
     */
    @Resource(name = "httpClientTemplate")
    private RestTemplate httpClientTemplate;

    /**
     * 线程池
     */
    private static ExecutorService executor = ThreadPoolExecutorUtils.getExecutorPool();
    private static String prefix = String.valueOf(System.currentTimeMillis());

    /**
     * description: 多线程下载文件
     *
     * @param url
     * @param targetPath
     * @return void
     * @author Hlingoes 2020/5/23
     */
    public void downloadByMultiThread(String url, String targetPath) throws IOException {
        long startTimestamp = System.currentTimeMillis();
        String fileFullPath = obtainFilePath(url, targetPath);
        Object[] args = new Object[]{url, fileFullPath};
        logger.info("Download started, url:{}, fileFullPath:{}", url, fileFullPath);
        MultiOperationThreadUtils.batchExecute(this, args);
        long completedTimestamp = System.currentTimeMillis();
        logger.info("Download finished, url:{}, fileFullPath:{}, const: {}ms", url, fileFullPath, (completedTimestamp - startTimestamp));
    }

    /**
     * description: 判断文件大小
     *
     * @param contentLength
     * @return boolean
     * @author Hlingoes 2020/5/23
     */
    private boolean isBigFile(long contentLength) {
        if (contentLength > ONE_KB_SIZE * ONE_KB_SIZE) {
            logger.info("Content-Length: {}MB", (contentLength / ONE_KB_SIZE / ONE_KB_SIZE));
        } else if (contentLength > ONE_KB_SIZE) {
            logger.info("Content-Length: {}KB", (contentLength / ONE_KB_SIZE));
        } else {
            logger.info("Content-Length: {}B", (contentLength));
        }
        return contentLength >= BIG_FILE_SIZE;
    }

    /**
     * description: 直接将所有数据读取到内存中，然后写入到文件，速度更快，适合小文件的下载，大文件容易引发内存溢出
     *
     * @param url
     * @param targetDir
     * @param params
     * @return void
     * @author Hlingoes 2020/5/23
     */
    public void downloadFileByMemoryMode(String url, String targetDir, Map<String, String> params) {
        String completeUrl = obtainGetUri(url, params);
        String filePath = obtainFilePath(completeUrl, targetDir);
        downloadFileByMemoryMode(completeUrl, filePath);
    }

    /**
     * description: 直接将所有数据读取到内存中，然后写入到文件，速度更快，适合小文件的下载，大文件容易引发内存溢出
     *
     * @param url
     * @param filePath
     * @return void
     * @author Hlingoes 2020/5/23
     */
    public void downloadFileByMemoryMode(String url, String filePath) {
        Instant now = Instant.now();
        // getForObject会将所有返回直接放到内存中
        ResponseEntity<byte[]> res = httpClientTemplate.getForEntity(url, byte[].class);
        try {
            Files.write(Paths.get(filePath), Objects.requireNonNull(res.getBody(), "未获取到下载文件"));
            logger.info("下载成功, url: {}, filePath: {}, 耗时:{}", url, filePath, ChronoUnit.MILLIS.between(now, Instant.now()));
        } catch (IOException e) {
            logger.error("[下载文件] 写入失败:", e);
        }
    }

    /**
     * description: 直接将所有数据读取到内存中，然后写入到文件，速度更快，适合小文件的下载，大文件容易引发内存溢出
     * 设置请求头header，分段下载文件
     *
     * @param start
     * @param end
     * @param url
     * @param filePath
     * @return void
     * @author Hlingoes 2020/5/23
     */
    private void downloadFilePartitionByMemoryMode(long start, long end, String url, String filePath) {
        Instant now = Instant.now();
        // 定义请求头的接收类型
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.RANGE, "bytes=" + start + "-" + end);
        headers.setAccept(Collections.singletonList(MediaType.ALL));
        ResponseEntity<byte[]> res = httpClientTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), byte[].class);
        try {
            Files.write(Paths.get(filePath), Objects.requireNonNull(res.getBody(), "未获取到下载文件"));
            logger.info("下载成功, url: {}, filePath: {}, 耗时:{}", url, filePath, ChronoUnit.MILLIS.between(now, Instant.now()));
        } catch (IOException e) {
            logger.error("下载失败, url: {}, filePath: {}", url, filePath, e);
        }
    }

    /**
     * description: 使用流接收数据，可以边读边写，适合下载大文件
     *
     * @param url
     * @param targetDir
     * @param params
     * @return void
     * @author Hlingoes 2020/5/23
     */
    public void downloadFileByStreamMode(String url, String targetDir, Map<String, String> params) {
        String completeUrl = obtainGetUri(url, params);
        String filePath = obtainFilePath(completeUrl, targetDir);
        downloadFileByStreamMode(completeUrl, filePath);
    }

    /**
     * description: 使用流接收数据，可以边读边写，适合下载大文件
     *
     * @param url
     * @param filePath
     * @return void
     * @author Hlingoes 2020/5/23
     */
    public void downloadFileByStreamMode(String url, String filePath) {
        // 定义请求头的接收类型
        RequestCallback requestCallback = request -> request.getHeaders()
                .setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM, MediaType.ALL));
        downloadFileByStreamMode(url, filePath, requestCallback);
    }

    /**
     * description: 使用流接收数据，可以边读边写，适合下载大文件
     * 设置请求头header，分段下载文件
     *
     * @param start
     * @param end
     * @param url
     * @param filePath
     * @return void
     * @author Hlingoes 2020/5/23
     */
    private void downloadFilePartitionByStreamMode(long start, long end, String url, String filePath) {
        RequestCallback requestCallback = request -> {
            HttpHeaders headers = request.getHeaders();
            headers.set(HttpHeaders.RANGE, "bytes=" + start + "-" + end);
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM, MediaType.ALL));
        };
        downloadFileByStreamMode(url, filePath, requestCallback);
    }

    /**
     * description: 使用流接收数据，可以边读边写，适合下载大文件
     *
     * @param url
     * @param filePath
     * @param requestCallback
     * @return void
     * @author Hlingoes 2020/5/23
     */
    private void downloadFileByStreamMode(String url, String filePath, RequestCallback requestCallback) {
        Instant now = Instant.now();
        ResponseExtractor<Void> responseExtractor = response -> {
            Files.copy(response.getBody(), Paths.get(filePath));
            return null;
        };
        httpClientTemplate.execute(url, HttpMethod.GET, requestCallback, responseExtractor);
        logger.info("下载成功, url: {}, filePath: {}, 耗时:{}", url, filePath, ChronoUnit.MILLIS.between(now, Instant.now()));
    }

    /**
     * 拼接get请求参数
     *
     * @param url
     * @param params
     * @return
     */
    private String obtainGetUri(String url, Map<String, String> params) {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(url);
        if (!CollectionUtils.isEmpty(params)) {
            params.forEach((k, v) -> {
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
    public String obtainFilePath(String url, String targetDir) {
        String filename = url.substring(url.lastIndexOf("/") + 1);
        int index = url.indexOf("?");
        if (index != -1) {
            filename = filename.substring(0, index);
        }
        File dir = new File(targetDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return targetDir.endsWith("/") ? targetDir + filename : targetDir + "/" + filename;
    }

    @Override
    public long count(Object[] args) {
        // 调用head方法,只获取头信息,拿到文件大小
        return httpClientTemplate.headForHeaders(args[0].toString()).getContentLength();
    }

    @Override
    public List<Object> find(PartitionElements elements) {
        // 将需要下载的文件分段
        long start = (elements.getIndex() - 1L) * elements.getRows();
        long end = elements.getIndex() * elements.getRows() - 1L;
        // 如果end > total, 表示最后一个分段，直接到total
        if (end > elements.getTotal()) {
            end = elements.getTotal();
        }
        Object[] args = elements.getArgs();
        String url = args[1].toString();
        String filePath = String.format("%s-%s-%d", args[2], prefix, elements.getIndex());
        if (isBigFile(end - start)) {
            downloadFilePartitionByStreamMode(start, end, url, filePath);
        } else {
            downloadFilePartitionByMemoryMode(start, end, url, filePath);
        }
        return null;
    }

    @Override
    public void update(PartitionElements elements) {

    }

    @Override
    public void delete(PartitionElements elements) {

    }

    @Override
    public void prepare(PartitionElements elements) {
        // 按分段合并文件
        Object[] args = elements.getArgs();
        String fileFullPath = args[2].toString();
        String filePath = String.format("%s-%s-%d", fileFullPath, prefix, elements.getIndex());
        try {
            logger.info("开始合并, 文件:{}", filePath);
            RandomAccessFile resultFile = new RandomAccessFile(fileFullPath, "rw");
            RandomAccessFile tempFile = new RandomAccessFile(filePath, "rw");
            tempFile.getChannel().transferTo(0, tempFile.length(), resultFile.getChannel());
            tempFile.close();
            FileUtils.deleteQuietly(new File(filePath));
            logger.info("删除临时文件: {}", filePath);
        } catch (IOException e) {
            logger.error("[多线程下载] {} 合并出错", filePath, e);
        }
    }

    @Override
    public Class<?> getEntityClazz() {
        return httpClientTemplate.getClass();
    }

}

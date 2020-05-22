package cn.henry.study.web.service.files;

import cn.henry.study.common.utils.ThreadPoolExecutorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
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
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/**
 * description: 使用带连接池的自定义HttpTemplate服务
 * 调用一次HEAD方法去获取到文件大小, 默认开启了10个线程,
 * 每个线程分配好下载的数据量,在请求头中设置Range属性,分别去下载属于它那一部分的数据,
 * 最后合并成一个文件
 * 原文链接：https://blog.csdn.net/zzzgd_666/article/details/88915818
 *
 * @author Hlingoes
 * @date 2019/12/21 22:26
 */
@Service
public class HttpClientTemplateService extends DefaultFileService {
    private static Logger logger = LoggerFactory.getLogger(HttpClientTemplateService.class);

    /**
     * 使用自定义的httpclient的restTemplate
     */
    @Resource(name = "httpClientTemplate")
    private RestTemplate httpClientTemplate;

    /**
     * 线程池
     */
    private static ExecutorService executor = ThreadPoolExecutorUtils.getExecutorPool();

    private static final int ONE_KB_SIZE = 1024;
    /**
     * 大于20M的文件视为大文件,采用流下载
     */
    private static final int BIG_FILE_SIZE = 20 * 1024 * 1024;
    private static String prefix = String.valueOf(System.currentTimeMillis());

    public void downloadByMultiThread(String url, String targetPath) {
        long startTimestamp = System.currentTimeMillis();
        boolean isBigFile;

        //调用head方法,只获取头信息,拿到文件大小
        long contentLength = httpClientTemplate.headForHeaders(url).getContentLength();
        isBigFile = contentLength >= BIG_FILE_SIZE;

        if (contentLength > 1024 * ONE_KB_SIZE) {
            logger.info("[多线程下载] Content-Length\t" + (contentLength / 1024 / 1024) + "MB");
        } else if (contentLength > ONE_KB_SIZE) {
            logger.info("[多线程下载] Content-Length\t" + (contentLength / 1024) + "KB");
        } else {
            logger.info("[多线程下载] Content-Length\t" + (contentLength) + "B");
        }
        long tempLength = contentLength / ThreadPoolExecutorUtils.DEFAULT_CORE_SIZE;
        long start, end = -1;

        ArrayList<CompletableFuture<HttpClientTemplateService.DownloadTemp>> futures = new ArrayList<>(ThreadPoolExecutorUtils.DEFAULT_CORE_SIZE);
        String fileFullPath;
        RandomAccessFile resultFile;
        try {
            fileFullPath = getAndCreateDownloadDir(url, targetPath);
            //创建目标文件
            resultFile = new RandomAccessFile(fileFullPath, "rw");

            logger.info("[多线程下载] Download started, url:{}\tfileFullPath:{}", url, fileFullPath);
            for (int i = 0; i < ThreadPoolExecutorUtils.DEFAULT_CORE_SIZE; ++i) {
                start = end + 1;
                end = end + tempLength;
                if (i == ThreadPoolExecutorUtils.DEFAULT_CORE_SIZE - 1) {
                    end = contentLength;
                }
                logger.info("[多线程下载] start:{}\tend:{}", start, end);

                HttpClientTemplateService.DownloadThread thread = new HttpClientTemplateService.DownloadThread(httpClientTemplate, i, start, end, url, fileFullPath, isBigFile);
                CompletableFuture<HttpClientTemplateService.DownloadTemp> future = CompletableFuture.supplyAsync(thread::call, executor);
                futures.add(future);
            }
        } catch (Exception e) {
            logger.error("[多线程下载] 下载出错", e);
            return;
        } finally {
            executor.shutdown();
        }

        //合并文件
        futures.forEach(f -> {
            try {
                f.thenAccept(o -> {
                    try {
                        logger.info("[多线程下载] {} 开始合并,文件:{}", o.threadName, o.filename);
                        RandomAccessFile tempFile = new RandomAccessFile(o.filename, "rw");
                        tempFile.getChannel().transferTo(0, tempFile.length(), resultFile.getChannel());
                        tempFile.close();
                        File file = new File(o.filename);
                        boolean b = file.delete();
                        logger.info("[多线程下载] {} 删除临时文件:{}\t结果:{}", o.threadName, o.filename, b);
                    } catch (IOException e) {
                        logger.error("[多线程下载] {} 合并出错", o.threadName, e);
                    }
                }).get();
            } catch (Exception e) {
                logger.error("[多线程下载] 合并出错", e);
            } finally {
                executor.shutdown();
            }
        });
        long completedTimestamp = System.currentTimeMillis();
        logger.info("=======下载完成======,耗时{}",
                isBigFile ? (completedTimestamp - startTimestamp) / 1000 + "s" : (completedTimestamp - startTimestamp) + "ms");
    }

    public class DownloadThread implements Callable<HttpClientTemplateService.DownloadTemp> {
        private int index;
        private String filePath;
        private long start, end;
        private String urlString;
        private RestTemplate httpClientTemplate;
        private boolean isBigFile;

        DownloadThread(RestTemplate restTemplate,
                       int index,
                       long start,
                       long end,
                       String url,
                       String fileFullPath,
                       boolean isBigFile) {
            this.httpClientTemplate = restTemplate;
            this.urlString = url;
            this.index = index;
            this.start = start;
            this.end = end;
            this.isBigFile = isBigFile;
            Assert.hasLength(fileFullPath, "文件下载路径不能为空");
            this.filePath = String.format("%s-%s-%d", fileFullPath, prefix, index);
        }

        @Override
        public HttpClientTemplateService.DownloadTemp call() {
            try {
                if (isBigFile) {
                    downloadBigFile();
                } else {
                    downloadLittleFile();
                }
            } catch (Exception e) {
                logger.error("[线程下载] 下载失败:", e);
            }
            HttpClientTemplateService.DownloadTemp downloadTemp = new HttpClientTemplateService.DownloadTemp();
            downloadTemp.index = index;
            downloadTemp.filename = filePath;
            downloadTemp.threadName = Thread.currentThread().getName();
            logger.info("[线程下载] \tcompleted.");
            return downloadTemp;
        }

        /**
         * 下载小文件
         *
         * @throws IOException
         */
        private void downloadLittleFile() throws IOException {
            // 定义请求头的接收类型
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.RANGE, "bytes=" + start + "-" + end);
            headers.setAccept(Collections.singletonList(MediaType.ALL));
            ResponseEntity<byte[]> rsp = httpClientTemplate.exchange(urlString, HttpMethod.GET, new HttpEntity<>(headers), byte[].class);
            logger.info("[线程下载] 返回状态码:{}", rsp.getStatusCode());
            Files.write(Paths.get(filePath), Objects.requireNonNull(rsp.getBody(), "未获取到下载文件"));
        }

        /**
         * 下载大文件
         */
        private void downloadBigFile() {
            RequestCallback requestCallback = request -> {
                HttpHeaders headers = request.getHeaders();
                headers.set(HttpHeaders.RANGE, "bytes=" + start + "-" + end);
                headers.setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM, MediaType.ALL));
            };
            // getForObject会将所有返回直接放到内存中,使用流来替代这个操作
            ResponseExtractor<Void> responseExtractor = response -> {
                // Here I write the response to a file but do what you like
                Files.copy(response.getBody(), Paths.get(filePath));
                logger.info("[线程下载] 返回状态码:{}", response.getStatusCode());
                return null;
            };
            httpClientTemplate.execute(urlString, HttpMethod.GET, requestCallback, responseExtractor);
        }
    }

    private static class DownloadTemp {
        private int index;
        private String filename;
        private String threadName;
    }

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
        logger.info("[下载文件] [状态码] code:{}", rsp.getStatusCode());
        try {
            String path = getAndCreateDownloadDir(url, targetDir);
            Files.write(Paths.get(path), Objects.requireNonNull(rsp.getBody(), "未获取到下载文件"));
        } catch (IOException e) {
            logger.error("[下载文件] 写入失败:", e);
        }
        logger.info("[下载文件] 完成,耗时:{}", ChronoUnit.MILLIS.between(now, Instant.now()));
    }

    /**
     * 下载大文件,使用流接收
     *
     * @param url
     * @param targetDir
     */
    public void downloadBigFile(String url, String targetDir) {
        downloadBigFile(url, targetDir, null);
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
            logger.error("[下载文件] 写入失败:", e);
        }
        logger.info("[下载文件] 完成,耗时:{}", ChronoUnit.MILLIS.between(now, Instant.now()));
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

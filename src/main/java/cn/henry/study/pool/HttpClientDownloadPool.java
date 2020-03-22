package cn.henry.study.pool;

import cn.henry.study.service.HttpClientTemplateService;
import cn.henry.study.utils.CustomThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.*;

/**
 * description: 调用一次HEAD方法去获取到文件大小, 默认开启了10个线程,
 * 每个线程分配好下载的数据量,在请求头中设置Range属性,分别去下载属于它那一部分的数据,
 * 最后合并成一个文件
 * 原文链接：https://blog.csdn.net/zzzgd_666/article/details/88915818
 * @author Hlingoes
 * @date 2019/12/22 0:28
 */
@Component
public class HttpClientDownloadPool {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientDownloadPool.class);

    /**
     * 使用自定义的httpclient的restTemplate
     */
    @Resource(name = "httpClientTemplate")
    private RestTemplate httpClientTemplate;

    @Autowired
    private HttpClientTemplateService httpClientTemplateService;

    /**
     * 线程最小值
     */
    private static final int MIN_POOL_SIZE = 10;
    /**
     * 线程最大值
     */
    private static final int MAX_POOL_SIZE = 100;
    /**
     * 等待队列大小
     */
    private static final int WAIT_QUEUE_SIZE = 1000;
    /**
     * 线程池
     */
    private static ExecutorService threadPool;

    private static final int ONE_KB_SIZE = 1024;
    /**
     * 大于20M的文件视为大文件,采用流下载
     */
    private static final int BIG_FILE_SIZE = 20 * 1024 * 1024;
    private static String prefix = String.valueOf(System.currentTimeMillis());

    public void downloadByMultithread(String url, String targetPath, Integer threadNum) {
        long startTimestamp = System.currentTimeMillis();
        //开启线程
        threadNum = threadNum == null ? MIN_POOL_SIZE : threadNum;
        Assert.isTrue(threadNum > 0, "线程数不能为负数");
        ThreadFactory factory = new CustomThreadFactoryBuilder().setNameFormat("http-demo-%d").build();
        threadPool = new ThreadPoolExecutor(
                threadNum, MAX_POOL_SIZE, 0, TimeUnit.MINUTES,
                new LinkedBlockingDeque<>(WAIT_QUEUE_SIZE), factory);
        boolean isBigFile;

        //调用head方法,只获取头信息,拿到文件大小
        long contentLength = httpClientTemplate.headForHeaders(url).getContentLength();
        isBigFile = contentLength >= BIG_FILE_SIZE;

        if (contentLength > 1024 * ONE_KB_SIZE) {
            LOGGER.info("[多线程下载] Content-Length\t" + (contentLength / 1024 / 1024) + "MB");
        } else if (contentLength > ONE_KB_SIZE) {
            LOGGER.info("[多线程下载] Content-Length\t" + (contentLength / 1024) + "KB");
        } else {
            LOGGER.info("[多线程下载] Content-Length\t" + (contentLength) + "B");
        }
        long tempLength = contentLength / threadNum;
        long start, end = -1;

        ArrayList<CompletableFuture<DownloadTemp>> futures = new ArrayList<>(threadNum);
        String fileFullPath;
        RandomAccessFile resultFile;
        try {
            fileFullPath = httpClientTemplateService.getAndCreateDownloadDir(url, targetPath);
            //创建目标文件
            resultFile = new RandomAccessFile(fileFullPath, "rw");

            LOGGER.info("[多线程下载] Download started, url:{}\tfileFullPath:{}", url, fileFullPath);
            for (int i = 0; i < threadNum; ++i) {
                start = end + 1;
                end = end + tempLength;
                if (i == threadNum - 1) {
                    end = contentLength;
                }
                LOGGER.info("[多线程下载] start:{}\tend:{}", start, end);

                DownloadThread thread = new DownloadThread(httpClientTemplate, i, start, end, url, fileFullPath, isBigFile);
                CompletableFuture<DownloadTemp> future = CompletableFuture.supplyAsync(thread::call, threadPool);
                futures.add(future);
            }
        } catch (Exception e) {
            LOGGER.error("[多线程下载] 下载出错", e);
            return;
        } finally {
            threadPool.shutdown();
        }

        //合并文件
        futures.forEach(f -> {
            try {
                f.thenAccept(o -> {
                    try {
                        LOGGER.info("[多线程下载] {} 开始合并,文件:{}", o.threadName, o.filename);
                        RandomAccessFile tempFile = new RandomAccessFile(o.filename, "rw");
                        tempFile.getChannel().transferTo(0, tempFile.length(), resultFile.getChannel());
                        tempFile.close();
                        File file = new File(o.filename);
                        boolean b = file.delete();
                        LOGGER.info("[多线程下载] {} 删除临时文件:{}\t结果:{}", o.threadName, o.filename, b);
                    } catch (IOException e) {
                        LOGGER.error("[多线程下载] {} 合并出错", o.threadName, e);
                    }
                }).get();
            } catch (Exception e) {
                LOGGER.error("[多线程下载] 合并出错", e);
            } finally {
                threadPool.shutdown();
            }
        });
        long completedTimestamp = System.currentTimeMillis();
        LOGGER.info("=======下载完成======,耗时{}",
                isBigFile ? (completedTimestamp - startTimestamp) / 1000 + "s" : (completedTimestamp - startTimestamp) + "ms");
    }

    public class DownloadThread implements Callable<DownloadTemp> {
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
        public DownloadTemp call() {
            try {
                if (isBigFile) {
                    downloadBigFile();
                } else {
                    downloadLittleFile();
                }
            } catch (Exception e) {
                LOGGER.error("[线程下载] 下载失败:", e);
            }
            DownloadTemp downloadTemp = new DownloadTemp();
            downloadTemp.index = index;
            downloadTemp.filename = filePath;
            downloadTemp.threadName = Thread.currentThread().getName();
            LOGGER.info("[线程下载] \tcompleted.");
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
            LOGGER.info("[线程下载] 返回状态码:{}", rsp.getStatusCode());
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
                LOGGER.info("[线程下载] 返回状态码:{}", response.getStatusCode());
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

}

package cn.henry.study.common.utils;

import cn.henry.study.common.bo.PartitionElements;
import cn.henry.study.common.service.OperationThreadService;
import cn.henry.study.common.thread.OperationThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * description: 多线程业务分治归并处理
 *
 * @author Hlingoes
 * @date 2020/5/22 0:42
 */
public class MultiThreadOperationUtils {
    private static Logger logger = LoggerFactory.getLogger(MultiThreadOperationUtils.class);

    /**
     * description: 开启多线程执行任务，按顺序归并处理任务结果
     * 默认的总页数为系统的cpu核数
     *
     * @param service
     * @return void
     * @author Hlingoes 2020/5/23
     */
    public static void batchExecute(OperationThreadService service, Object[] args) throws Exception {
        long total = service.count(args);
        long pageSize = total / ThreadPoolExecutorUtils.DEFAULT_CORE_SIZE;
        batchExecute(service, pageSize, ThreadPoolExecutorUtils.DEFAULT_CORE_SIZE, total, args);
    }

    /**
     * description: 开启多线程执行任务，按顺序归并处理任务结果
     * 给定每页显示条目个数
     *
     * @param service
     * @param pageSize
     * @return void
     * @author Hlingoes 2020/5/23
     */
    public static void batchExecute(OperationThreadService service, long pageSize, Object[] args) throws Exception {
        long total = service.count(args);
        long pageCount = PartitionElements.calculateTaskCount(total, pageSize);
        batchExecute(service, pageSize, pageCount, total, args);
    }

    /**
     * description: 开启多线程执行分治任务，按顺序归并处理任务结果
     *
     * @param service
     * @param pageSize
     * @param total
     * @param args
     * @return void
     * @author Hlingoes 2020/5/23
     */
    private static void batchExecute(OperationThreadService service, long pageSize, long pageCount, long total, Object[] args) throws Exception {
        ThreadPoolExecutor executor = ThreadPoolExecutorUtils.getExecutorPool();
        // 在多线程分治任务之前的预处理方法，返回业务数据
        final Object obj = service.prepare(args);
        // 预防list和map的resize，初始化给定容量，可提高性能
        ArrayList<CompletableFuture<PartitionElements>> futures = new ArrayList<>((int) pageCount);
        OperationThread opThread = null;
        CompletableFuture<PartitionElements> future = null;
        // 添加线程任务
        for (int i = 0; i < pageCount; i++) {
            // 划定任务分布
            opThread = new OperationThread(new PartitionElements(i + 1, pageSize, pageCount, total, args), service);
            future = CompletableFuture.supplyAsync(opThread::call, executor);
            futures.add(future);
        }
        // 关闭线程池
        executor.shutdown();
        // 阻塞线程，同步处理数据
        futures.forEach(f -> {
            try {
                f.thenAccept(element -> {
                    // 线程单个任务结束后的归并方法
                    try {
                        service.post(element, obj);
                    } catch (Exception e) {
                        logger.error("post routine fail", e);
                    }
                }).get();
            } catch (InterruptedException | ExecutionException e) {
                logger.error("future call fail", e);
            }
        });
        service.finished(obj);
    }

}

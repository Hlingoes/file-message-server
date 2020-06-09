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
     * 按照默认线程数，计算批量任务数
     *
     * @param service
     * @param args
     * @return void
     * @author Hlingoes 2020/5/23
     */
    public static Object batchExecute(OperationThreadService service, Object[] args) throws Exception {
        long totalCounts = service.count(args);
        long batchCounts = totalCounts / ThreadPoolExecutorUtils.defaultCoreSize;
        // 兼容任务少于核心线程数的情况
        if (batchCounts == 0) {
            batchCounts = 1L;
        }
        PartitionElements elements = new PartitionElements(batchCounts, totalCounts, args);
        return batchExecute(service, elements);
    }

    /**
     * description: 开启多线程执行任务，按顺序归并处理任务结果
     * 给定每页显示条目个数
     *
     * @param service
     * @param batchCounts
     * @param args
     * @return void
     * @author Hlingoes 2020/5/23
     */
    public static Object batchExecute(OperationThreadService service, long batchCounts, Object[] args) throws Exception {
        long totalCounts = service.count(args);
        PartitionElements elements = new PartitionElements(batchCounts, totalCounts, args);
        return batchExecute(service, elements);
    }

    /**
     * description: 开启多线程执行分治任务，按顺序归并处理任务结果
     *
     * @param service
     * @param elements
     * @return void
     * @author Hlingoes 2020/5/23
     */
    private static Object batchExecute(OperationThreadService service, PartitionElements elements) throws Exception {
        ThreadPoolExecutor executor = ThreadPoolExecutorUtils.getExecutorPool();
        // 在多线程分治任务之前的预处理方法，返回业务数据
        final Object obj = service.prepare(elements.getArgs());
        // 预防list和map的resize，初始化给定容量，可提高性能
        ArrayList<CompletableFuture<PartitionElements>> futures = new ArrayList<>((int) elements.getPartitions());
        OperationThread opThread = null;
        CompletableFuture<PartitionElements> future = null;
        // 添加线程任务
        for (int i = 0; i < elements.getPartitions(); i++) {
            // 划定任务分布
            opThread = new OperationThread(new PartitionElements(i + 1, elements), service);
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
        return service.finished(obj);
    }

}

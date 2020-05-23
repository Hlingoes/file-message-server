package cn.henry.study.common.utils;

import cn.henry.study.common.bo.PartitionElements;
import cn.henry.study.common.service.OperationThreadService;
import cn.henry.study.common.thread.OperationThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * description: 多线程业务分治归并处理
 *
 * @author Hlingoes
 * @date 2020/5/22 0:42
 */
public class MultiOperationThreadUtils {
    private static Logger logger = LoggerFactory.getLogger(MultiOperationThreadUtils.class);

    private static ThreadPoolExecutor executor = ThreadPoolExecutorUtils.getExecutorPool();

    /**
     * description: 开启多线程执行任务
     *
     * @param service
     * @return void
     * @author Hlingoes 2020/5/23
     */
    public static void batchExecute(OperationThreadService service, Object[] args) {
        long total = service.count(args);
        long rows = total / ThreadPoolExecutorUtils.DEFAULT_CORE_SIZE;
        batchExecute(service, args, rows, total);
    }

    /**
     * description: 开启多线程执行任务
     *
     * @param service
     * @param rows
     * @return void
     * @author Hlingoes 2020/5/23
     */
    public static void batchExecute(OperationThreadService service, long rows, Object[] args) {
        long total = service.count(args);
        batchExecute(service, args, rows, total);
    }

    /**
     * description: 开启多线程执行任务
     *
     * @param service
     * @param rows    单次任务处理量
     * @param args    任务总量
     * @return void
     * @author Hlingoes 2020/5/23
     */
    private static void batchExecute(OperationThreadService service, Object[] args, long rows, long total) {
        long taskCount = calculateTaskCount(total, rows);
        // 预防list和map的resize，初始化给定容量，可提高性能
        ArrayList<Future<PartitionElements>> futures = new ArrayList<>((int) taskCount);
        OperationThread opThread = null;
        // 添加线程任务
        for (int i = 0; i <= taskCount; i++) {
            opThread = new OperationThread(new PartitionElements(i + 1, rows, total, args), service);
            Future<PartitionElements> future = executor.submit(opThread);
            futures.add(future);
        }
        // 关闭线程池
        executor.shutdown();
        // 阻塞线程，并行处理数据
        for (Future<PartitionElements> future : futures) {
            try {
                service.prepare(future.get());
            } catch (InterruptedException | ExecutionException e) {
                logger.error("future recall fail", e);
            }
        }
    }

    /**
     * description: 开启多线程，最后同步执行任务
     *
     * @param service
     * @return void
     * @author Hlingoes 2020/5/23
     */
    public static void batchAsyncExecute(OperationThreadService service, Object[] args) {
        long total = service.count(args);
        long rows = total / ThreadPoolExecutorUtils.DEFAULT_CORE_SIZE;
        batchAsyncExecute(service, args, rows, total);
    }

    /**
     * description: 开启多线程，最后同步执行任务
     *
     * @param service
     * @param rows
     * @return void
     * @author Hlingoes 2020/5/23
     */
    public static void batchAsyncExecute(OperationThreadService service, long rows, Object[] args) {
        long total = service.count(args);
        batchAsyncExecute(service, args, rows, total);
    }

    /**
     * description: 开启多线程，最后同步执行任务
     *
     * @param service
     * @param rows    单次任务处理量
     * @param args    任务总量
     * @return void
     * @author Hlingoes 2020/5/23
     */
    private static void batchAsyncExecute(OperationThreadService service, Object[] args, long rows, long total) {
        long taskCount = calculateTaskCount(total, rows);
        // 预防list和map的resize，初始化给定容量，可提高性能
        ArrayList<CompletableFuture<PartitionElements>> futures = new ArrayList<>((int) taskCount);
        OperationThread opThread = null;
        CompletableFuture<PartitionElements> future = null;
        // 添加线程任务
        for (int i = 0; i <= taskCount; i++) {
            opThread = new OperationThread(new PartitionElements(i + 1, rows, total, args), service);
            future = CompletableFuture.supplyAsync(opThread::call, executor);
            futures.add(future);
        }
        // 关闭线程池
        executor.shutdown();
        // 阻塞线程，同步处理数据
        futures.forEach(f -> {
            try {
                f.thenAccept(elems -> {
                    service.prepare(elems);
                }).get();
            } catch (InterruptedException | ExecutionException e) {
                logger.error("future recall fail", e);
            }
        });
    }

    /**
     * description: 根据任务总量和单次任务处理量，计算任务个数
     *
     * @param total
     * @param rows
     * @return long
     * @author Hlingoes 2020/5/23
     */
    private static long calculateTaskCount(long total, long rows) {
        long taskCount = total / rows;
        if (total % rows != 0) {
            taskCount = taskCount + 1;
        }
        return taskCount;
    }

}

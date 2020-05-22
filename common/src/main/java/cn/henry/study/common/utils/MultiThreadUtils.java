package cn.henry.study.common.utils;

import cn.henry.study.common.bo.PartitionElements;
import cn.henry.study.common.service.OperationService;
import cn.henry.study.common.thread.OperationThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * description:
 *
 * @author Hlingoes
 * @date 2020/5/22 0:42
 */
public class MultiThreadUtils {
    private static Logger logger = LoggerFactory.getLogger(MultiThreadUtils.class);

    private static ThreadPoolExecutor executor = ThreadPoolUtils.getExecutorPool();
    private static int defautCount = 1000;

    /**
     * description: 开启多线程执行任务
     *
     * @param service
     * @return void
     * @author Hlingoes 2020/5/23
     */
    public static void batchExecute(OperationService service, Object[] args) {
        batchExecute(service, defautCount, args);
    }

    /**
     * description: 开启多线程执行任务
     *
     * @param service
     * @param count
     * @return void
     * @author Hlingoes 2020/5/23
     */
    public static void batchExecute(OperationService service, int count, Object[] args) {
        // 查询数据库总数量
        int total = service.count(args);
        // 需要查询的次数
        int times = total / count;
        if (total % count != 0) {
            times = times + 1;
        }
        ArrayList<Future<PartitionElements>> futures = new ArrayList<>(times);
        OperationThread dbThread = null;
        for (int i = 0; i <= times; i++) {
            dbThread = new OperationThread(new PartitionElements(i + 1, count, args), service);
            Future<PartitionElements> future = executor.submit(dbThread);
            futures.add(future);
        }
        // 关闭线程池
        executor.shutdown();
        // 处理线程返回结果
        if (!futures.isEmpty()) {
            for (Future<PartitionElements> future : futures) {
                try {
                    service.prepare(future.get());
                } catch (InterruptedException | ExecutionException e) {
                    logger.error("处理future回调失败", e);
                }
            }
        }
    }

}

package cn.henry.study.common.thread;

import cn.henry.study.common.utils.ThreadPoolUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * description:
 *
 * @author Hlingoes
 * @date 2020/5/22 0:42
 */
public class MultiThreadService {
    private static Logger logger = LoggerFactory.getLogger(MultiThreadService.class);

    private static ThreadPoolExecutor executor = ThreadPoolUtils.getExecutorPool();
    private static int count = 1000;

    private OperationService service;

    public MultiThreadService(OperationService service) {
        this.service = service;
    }

    public void batchExcute() {
        // 开始时间
        long startTime = System.currentTimeMillis();
        // 查询数据库总数量
        int total = service.count(new Object[]{"test"});
        // 需要查询的次数
        int times = total / count;
        if (total % count != 0) {
            times = times + 1;
        }
        ArrayList<Future<List<Object>>> futures = new ArrayList<>(times);
        for (int i = 1; i <= times; i++) {
            DbOperationThread thread = new DbOperationThread(i, count, service);
            Future<List<Object>> future = executor.submit(thread);
            futures.add(future);
        }
        // 关闭线程池
        executor.shutdown();
        // 处理线程返回结果
        if (!futures.isEmpty()) {
            for (Future<List<Object>> future : futures) {

            }
        }
        long endTime = System.currentTimeMillis();
        logger.info("线程查询数据用时: {}ms", (endTime - startTime));
    }

}

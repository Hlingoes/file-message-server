package cn.henry.study.pool;

import com.google.common.util.concurrent.Uninterruptibles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * description: 创建通用的线程池
 *
 * @author Hlingoes
 * @date 2020/2/26 0:46
 */
public class CustomThreadPool {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomThreadFactoryBuilder.class);

    private static int DEFAULT_QUEUE_SIZE = 1000;
    private static int DEFAULT_CORE_POOL_SIZE = 5;
    private static int DEFAULT_MAX_POOL_SIZE = 20;

    public static ExecutorService getExecutorPool() {
        ThreadFactory customFactory = new CustomThreadFactoryBuilder()
                .setNameFormat("custom-pool-%d")
                .build();
        BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(DEFAULT_QUEUE_SIZE);
        ExecutorService customThreadPool = new ThreadPoolExecutor(DEFAULT_CORE_POOL_SIZE,
                DEFAULT_MAX_POOL_SIZE, 60, TimeUnit.SECONDS, queue, customFactory,
                (r, executor) -> {
                    if (!executor.isShutdown()) {
                        LOGGER.warn("ThreadPool is too busy! waiting to insert task to queue! ");
                        Uninterruptibles.putUninterruptibly(executor.getQueue(), r);
                    }
                }) {
            @Override
            protected void afterExecute(Runnable r, Throwable t) {
                super.afterExecute(r, t);
                if (t == null && r instanceof Future<?>) {
                    try {
                        Future<?> future = (Future<?>) r;
                        future.get();
                    } catch (CancellationException ce) {
                        t = ce;
                    } catch (ExecutionException ee) {
                        t = ee.getCause();
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
                if (t != null) {
                    LOGGER.error("statisticsThreadPool error msg: {}", t.getMessage(), t);
                }
            }
        };
        return customThreadPool;
    }
}

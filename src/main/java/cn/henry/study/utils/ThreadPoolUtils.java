package cn.henry.study.utils;

import com.google.common.util.concurrent.Uninterruptibles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * description: 创建通用的线程池
 *
 * @author Hlingoes
 * @citation https://blog.csdn.net/wanghao112956/article/details/99292107
 * @date 2020/2/26 0:46
 */
public class ThreadPoolUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomThreadFactoryBuilder.class);

    private static int POLL_WAITING_TIME = 3 * 60;
    private static int DEFAULT_QUEUE_SIZE = 1000;
    private static int DEFAULT_CORE_POOL_SIZE = Runtime.getRuntime().availableProcessors();
    private static int DEFAULT_MAX_POOL_SIZE = 4 * DEFAULT_CORE_POOL_SIZE;

    /**
     * description: 创建线程池
     *
     * @param waitingTime
     * @param queueSize
     * @param maxPoolSize
     * @return java.util.concurrent.ThreadPoolExecutor
     * @author Hlingoes 2020/3/20
     */
    public static ThreadPoolExecutor getExecutorPool(int waitingTime, int queueSize, int maxPoolSize) {
        POLL_WAITING_TIME = waitingTime;
        DEFAULT_QUEUE_SIZE = queueSize;
        DEFAULT_MAX_POOL_SIZE = maxPoolSize;
        return getExecutorPool();
    }

    /**
     * description: 创建线程池
     *
     * @param waitingTime
     * @param queueSize
     * @return java.util.concurrent.ThreadPoolExecutor
     * @author Hlingoes 2020/3/20
     */
    public static ThreadPoolExecutor getExecutorPool(int waitingTime, int queueSize) {
        POLL_WAITING_TIME = waitingTime;
        DEFAULT_QUEUE_SIZE = queueSize;
        return getExecutorPool();
    }

    /**
     * description: 创建线程池
     *
     * @param waitingTime
     * @return java.util.concurrent.ThreadPoolExecutor
     * @author Hlingoes 2020/3/20
     */
    public static ThreadPoolExecutor getExecutorPool(int waitingTime) {
        POLL_WAITING_TIME = waitingTime;
        return getExecutorPool();
    }

    /**
     * description: 创建线程池
     *
     * @param
     * @return java.util.concurrent.ThreadPoolExecutor
     * @author Hlingoes 2020/3/20
     */
    public static ThreadPoolExecutor getExecutorPool() {
        ThreadFactory customFactory = new CustomThreadFactoryBuilder()
                .setNameFormat("custom-pool-%d")
                .build();
        BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(DEFAULT_QUEUE_SIZE);
        ThreadPoolExecutor customThreadPool = new ThreadPoolExecutor(DEFAULT_CORE_POOL_SIZE,
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
                    LOGGER.error("customThreadPool error msg: {}", t.getMessage(), t);
                }
            }
        };
        return customThreadPool;
    }

    /**
     * description: 所有任务执行完之后，释放线程池资源
     *
     * @param pool
     * @return void
     * @author Hlingoes 2020/3/20
     */
    public static void closeAfterComplete(ThreadPoolExecutor pool) {
        /**
         * 当线程池调用该方法时,线程池的状态则立刻变成SHUTDOWN状态。
         * 此时，则不能再往线程池中添加任何任务，否则将会抛出RejectedExecutionException异常。
         * 但是，此时线程池不会立刻退出，直到添加到线程池中的任务都已经处理完成，才会退出。
         * 唯一的影响就是不能再提交任务了，正则执行的任务即使在阻塞着也不会结束，在排队的任务也不会取消。
         */
        pool.shutdown();
        try {
            /**
             * awaitTermination方法可以设定线程池在关闭之前的最大超时时间，
             * 如果在超时时间结束之前线程池能够正常关闭，这个方法会返回true，否则，一旦超时，就会返回false。
             * 通常来说不可能无限制地等待下去，因此需要预估一个合理的超时时间，然后使用这个方法
             */
            if (!pool.awaitTermination(POLL_WAITING_TIME, TimeUnit.SECONDS)) {
                /**
                 * 如果awaitTermination方法返回false，又希望尽可能在线程池关闭之后再做其他资源回收工作，
                 * 可以考虑再调用一下shutdownNow方法，
                 * 此时队列中所有尚未被处理的任务都会被丢弃，同时会设置线程池中每个线程的中断标志位。
                 * shutdownNow并不保证一定可以让正在运行的线程停止工作，除非提交给线程的任务能够正确响应中断。
                 * 到了这一步，可以考虑继续调用awaitTermination方法，或者直接放弃，去做接下来要做的事情。
                 */
                pool.shutdownNow();
            }
        } catch (InterruptedException e) {
            LOGGER.error("ThreadPool overtime: {}", e.getMessage());
            //（重新）取消当前线程是否中断
            pool.shutdownNow();
            // 保持中断状态
            Thread.currentThread().interrupt();
        }
    }

}

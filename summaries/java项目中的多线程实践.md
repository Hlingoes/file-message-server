项目开发中对于一些数据的处理需要用到多线程，比如文件的批量上传，数据库的分批写入，大文件的分段下载等。
通常会使用spring自带的线程池处理，做到对线程的定制化处理和更好的可控，建议使用自定义的线程池。
主要涉及到的几个点：
> 1. 自定义线程工厂(ThreadFactoryBuilder)，主要用于线程的命名，方便追踪
> 2. 自定义的线程池(ThreadPoolExecutorUtils)，可以按功能优化配置参数
> 3. 一个抽象的多线程任务处理接口(OperationThreadService)和通用实现(OperationThread)
> 4. 统一的调度实现(MultiThreadOperationUtils)
##### 核心思想，分治归并，每个线程计算出自己的结果，最后统一汇总
~~~~
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

/**
 * description: 自定义实现的线程池，遵循alibaba编程规范，使用ThreadPoolExecutor创建线程池使用
 * 设置更有描述意义的线程名称，默认的ThreadFactory，它给线程起名字大概规律就是pool-m-thread-n，如pool-1-thread-1。
 * 当分析一个thread dump时，很难知道线程的目的，需要有描述意义的线程名称来分析追踪问题
 * 设置线程是否是守护线程，默认的ThreadFactory总是提交非守护线程
 * 设置线程优先级，默认ThreadFactory总是提交的一般优先级线程
 * <p>
 * CustomThreadFactoryBuilder类实现了一种优雅的Builder Mechanism方式去得到一个自定义ThreadFactory实例。
 * ThreadFactory接口中有一个接受Runnable类型参数的方法newThread(Runnable r)，
 * 业务的factory逻辑就应该写在这个方法中，去配置线程名称、优先级、守护线程状态等属性。
 * 原文链接：https://blog.csdn.net/zombres/article/details/80497515
 *
 * @author Hlingoes
 * @date 2019/12/22 0:45
 */
public class ThreadFactoryBuilder {
    private static Logger logger = LoggerFactory.getLogger(ThreadFactoryBuilder.class);

    private String nameFormat = null;
    private boolean daemon = false;
    private int priority = Thread.NORM_PRIORITY;

    public ThreadFactoryBuilder setNameFormat(String nameFormat) {
        if (nameFormat == null) {
            throw new NullPointerException();
        }
        this.nameFormat = nameFormat;
        return this;
    }

    public ThreadFactoryBuilder setDaemon(boolean daemon) {
        this.daemon = daemon;
        return this;
    }

    public ThreadFactoryBuilder setPriority(int priority) {
        if (priority < Thread.MIN_PRIORITY) {
            throw new IllegalArgumentException(String.format(
                    "Thread priority (%s) must be >= %s", priority, Thread.MIN_PRIORITY));
        }

        if (priority > Thread.MAX_PRIORITY) {
            throw new IllegalArgumentException(String.format(
                    "Thread priority (%s) must be <= %s", priority, Thread.MAX_PRIORITY));
        }

        this.priority = priority;
        return this;
    }

    public ThreadFactory build() {
        return build(this);
    }

    private static ThreadFactory build(ThreadFactoryBuilder builder) {
        final String nameFormat = builder.nameFormat;
        final Boolean daemon = builder.daemon;
        final Integer priority = builder.priority;
        final AtomicLong count = new AtomicLong(0);

        return (Runnable runnable) -> {
            Thread thread = new Thread(runnable);
            if (nameFormat != null) {
                thread.setName(String.format(nameFormat, count.getAndIncrement()));
            }
            if (daemon != null) {
                thread.setDaemon(daemon);
            }
            thread.setPriority(priority);
            thread.setUncaughtExceptionHandler((t, e) -> {
                String threadName = t.getName();
                logger.error("error occurred! threadName: {}, error msg: {}", threadName, e.getMessage(), e);
            });
            return thread;
        };
    }
}
~~~~
~~~~
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * description: 创建通用的线程池
 * <p>
 * corePoolSize：线程池中核心线程数量
 * maximumPoolSize：线程池同时允许存在的最大线程数量
 * 内部处理逻辑如下：
 * 当线程池中工作线程数小于corePoolSize，创建新的工作线程来执行该任务，不管线程池中是否存在空闲线程。
 * 如果线程池中工作线程数达到corePoolSize，新任务尝试放入队列，入队成功的任务将等待工作线程空闲时调度。
 * 1. 如果队列满并且线程数小于maximumPoolSize，创建新的线程执行该任务(注意：队列中的任务继续排序)。
 * 2. 如果队列满且线程数超过maximumPoolSize，拒绝该任务
 * <p>
 * keepAliveTime
 * 当线程池中工作线程数大于corePoolSize，并且线程空闲时间超过keepAliveTime，则这些线程将被终止。
 * 同样，可以将这种策略应用到核心线程，通过调用allowCoreThreadTimeout来实现。
 * <p>
 * BlockingQueue
 * 任务等待队列，用于缓存暂时无法执行的任务。分为如下三种堵塞队列：
 * 1. 直接递交，如SynchronousQueue，该策略直接将任务直接交给工作线程。如果当前没有空闲工作线程，创建新线程。
 * 这种策略最好是配合unbounded线程数来使用，从而避免任务被拒绝。但当任务生产速度大于消费速度，将导致线程数不断的增加。
 * 2. 无界队列，如LinkedBlockingQueue，当工作的线程数达到核心线程数时，新的任务被放在队列上。
 * 因此，永远不会有大于corePoolSize的线程被创建，maximumPoolSize参数失效。
 * 这种策略比较适合所有的任务都不相互依赖，独立执行。
 * 但是当任务处理速度小于任务进入速度的时候会引起队列的无限膨胀。
 * 3. 有界队列，如ArrayBlockingQueue，按前面描述的corePoolSize、maximumPoolSize、BlockingQueue处理逻辑处理。
 * 队列长度和maximumPoolSize两个值会相互影响：
 * 长队列 + 小maximumPoolSize。会减少CPU的使用、操作系统资源、上下文切换的消耗，但是会降低吞吐量，
 * 如果任务被频繁的阻塞如IO线程，系统其实可以调度更多的线程。
 * 短队列 + 大maximumPoolSize。CPU更忙，但会增加线程调度的消耗.
 * 总结一下，IO密集型可以考虑多些线程来平衡CPU的使用，CPU密集型可以考虑少些线程减少线程调度的消耗
 *
 * @author Hlingoes
 * @citation https://blog.csdn.net/wanghao112956/article/details/99292107
 * @citation https://www.jianshu.com/p/896b8e18501b
 * @date 2020/2/26 0:46
 */
public class ThreadPoolExecutorUtils {
    private static Logger logger = LoggerFactory.getLogger(ThreadFactoryBuilder.class);

    public static int defaultCoreSize = Runtime.getRuntime().availableProcessors();
    private static int pollWaitingTime = 60;
    private static int defaultQueueSize = 10 * 1000;
    private static int defaultMaxSize = 4 * defaultCoreSize;
    private static String threadName = "custom-pool";

    /**
     * description: 创建线程池
     *
     * @param waitingTime
     * @param coreSize
     * @param maxPoolSize
     * @param queueSize
     * @return java.util.concurrent.ThreadPoolExecutor
     * @author Hlingoes 2020/4/12
     */
    public static ThreadPoolExecutor getExecutorPool(int waitingTime, int coreSize, int maxPoolSize, int queueSize) {
        pollWaitingTime = waitingTime;
        defaultCoreSize = coreSize;
        defaultMaxSize = maxPoolSize;
        defaultQueueSize = queueSize;
        return getExecutorPool();
    }

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
        pollWaitingTime = waitingTime;
        defaultQueueSize = queueSize;
        defaultMaxSize = maxPoolSize;
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
        pollWaitingTime = waitingTime;
        defaultQueueSize = queueSize;
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
        pollWaitingTime = waitingTime;
        return getExecutorPool();
    }

    /**
     * description: 创建线程池
     *
     * @param
     * @return java.util.concurrent.ThreadPoolExecutor
     * @author Hlingoes 2020/6/6
     */
    public static ThreadPoolExecutor getExecutorPool() {
        return getExecutorPool(threadName);
    }

    /**
     * description: 创建线程池
     *
     * @param
     * @return java.util.concurrent.ThreadPoolExecutor
     * @author Hlingoes 2020/3/20
     */
    public static ThreadPoolExecutor getExecutorPool(String threadName) {
        ThreadFactory factory = new ThreadFactoryBuilder()
                .setNameFormat(threadName + "-%d")
                .build();
        BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(defaultQueueSize);
        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(defaultCoreSize,
                defaultMaxSize, 60, TimeUnit.SECONDS, queue, factory,
                (r, executor) -> {
                    /**
                     * 自定义的拒绝策略
                     * 当提交给线程池的某一个新任务无法直接被线程池中“核心线程”直接处理，
                     * 又无法加入等待队列，也无法创建新的线程执行；
                     * 又或者线程池已经调用shutdown()方法停止了工作；
                     * 又或者线程池不是处于正常的工作状态；
                     * 这时候ThreadPoolExecutor线程池会拒绝处理这个任务
                     */
                    if (!executor.isShutdown()) {
                        logger.warn("ThreadPoolExecutor is over working, please check the thread tasks! ");
                    }
                }) {

            /**
             * description: 针对提交给线程池的任务可能会抛出异常这一问题，
             * 可自行实现线程池的afterExecute方法，或者实现Thread的UncaughtExceptionHandler接口
             * ThreadFactoryBuilder中已经实现了UncaughtExceptionHandler接口，这里是为了进一步兼容
             *
             * @param r
             * @param t
             * @return void
             * @author Hlingoes 2020/5/27
             */
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
                    logger.error("customThreadPool error msg: {}", t.getMessage(), t);
                }
            }
        };
        /**
         * 备选方法，事先知道会有很多任务会提交给这个线程池，可以在初始化的时候完成核心线程的创建，提高系统性能
         * 一个线程池创建出来之后，在没有给它提交任何任务之前，这个线程池中的线程数为0
         * 一个个去创建新线程开销太大，影响系统性能
         * 可以在创建线程池的时候就将所有的核心线程全部一次性创建完毕，系统起来之后就可以直接使用
         */
        poolExecutor.prestartAllCoreThreads();
        return poolExecutor;
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
            if (!pool.awaitTermination(pollWaitingTime, TimeUnit.SECONDS)) {
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
            logger.error("ThreadPool overtime: {}", e.getMessage());
            //（重新）丢弃所有尚未被处理的任务，同时会设置线程池中每个线程的中断标志位
            pool.shutdownNow();
            // 保持中断状态
            Thread.currentThread().interrupt();
        }
    }
}
~~~~
~~~~
import java.util.Arrays;

/**
 * description: 分段参数
 *
 * @author Hlingoes
 * @date 2020/5/22 23:50
 */
public class PartitionElements {
    /**
     * 当前的分段任务索引
     */
    private long index;
    /**
     * 批量处理的任务个数
     */
    private long batchCounts;
    /**
     * 任务的分段个数
     */
    private long partitions;
    /**
     * 任务总数
     */
    private long totalCounts;
    private Object[] args;
    private Object data;

    public PartitionElements() {

    }

    public PartitionElements(long batchCounts, long totalCounts, Object[] args) {
        this.batchCounts = batchCounts;
        this.totalCounts = totalCounts;
        this.partitions = aquirePartitions(totalCounts, batchCounts);
        this.args = args;
    }

    public PartitionElements(long index, PartitionElements elements) {
        this.index = index;
        this.batchCounts = elements.getBatchCounts();
        this.partitions = elements.getPartitions();
        this.totalCounts = elements.getTotalCounts();
        this.args = elements.getArgs();
    }

    /**
     * description: 根据任务总量和单次任务处理量，计算任务个数
     *
     * @param totalCounts
     * @param batchCounts
     * @return long partitions
     * @author Hlingoes 2020/5/23
     */
    public long aquirePartitions(long totalCounts, long batchCounts) {
        long partitions = totalCounts / batchCounts;
        if (totalCounts % batchCounts != 0) {
            partitions = partitions + 1;
        }
        //  兼容任务总数total = 1 的情况
        if (partitions == 0) {
            partitions = 1;
        }
        return partitions;
    }

    public long getIndex() {
        return index;
    }

    public void setIndex(long index) {
        this.index = index;
    }

    public long getBatchCounts() {
        return batchCounts;
    }

    public void setBatchCounts(long batchCounts) {
        this.batchCounts = batchCounts;
    }

    public long getPartitions() {
        return partitions;
    }

    public void setPartitions(long partitions) {
        this.partitions = partitions;
    }

    public long getTotalCounts() {
        return totalCounts;
    }

    public void setTotalCounts(long totalCounts) {
        this.totalCounts = totalCounts;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "PartitionElements{" +
                "index=" + index +
                ", batchCounts=" + batchCounts +
                ", partitions=" + partitions +
                ", totalCounts=" + totalCounts +
                ", args=" + Arrays.toString(args) +
                '}';
    }
}
~~~~
~~~~
import cn.henry.study.common.bo.PartitionElements;

/**
 * description: 业务分治归并处理接口
 *
 * @author Hlingoes 2020/5/22
 */
public interface OperationThreadService {

    /**
     * description: 任务总量
     *
     * @param args
     * @return long
     * @throws Exception
     * @author Hlingoes 2020/5/22
     */
    long count(Object[] args) throws Exception;

    /**
     * description: 在多线程分治任务之前的预处理方法，返回业务数据
     *
     * @param args
     * @return Object
     * @throws Exception
     * @author Hlingoes 2020/5/23
     */
    Object prepare(Object[] args) throws Exception;

    /**
     * description: 多线程的任务逻辑
     *
     * @param elements
     * @return java.lang.Object
     * @throws Exception
     * @author Hlingoes 2020/5/24
     */
    Object invoke(PartitionElements elements) throws Exception;

    /**
     * description: 多线程单个任务结束后的归并方法
     *
     * @param elements
     * @param object
     * @return void
     * @throws Exception
     * @author Hlingoes 2020/5/23
     */
    void post(PartitionElements elements, Object object) throws Exception;

    /**
     * description: 归并结果之后的尾处理
     *
     * @param object
     * @return java.lang.Object
     * @throws Exception
     * @author Hlingoes 2020/5/24
     */
    Object finished(Object object) throws Exception;

}
~~~~
~~~~
import cn.henry.study.common.bo.PartitionElements;
import cn.henry.study.common.service.OperationThreadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

/**
 * description: 分段操作
 *
 * @author Hlingoes
 * @date 2020/5/22 0:13
 */
public class OperationThread implements Callable<PartitionElements> {
    private static Logger logger = LoggerFactory.getLogger(OperationThread.class);

    private PartitionElements elements;
    private OperationThreadService service;

    public OperationThread(PartitionElements elements, OperationThreadService service) {
        this.elements = elements;
        this.service = service;
    }

    @Override
    public PartitionElements call() {
        long startTime = System.currentTimeMillis();
        try {
            this.elements.setData(this.service.invoke(this.elements));
            long endTime = System.currentTimeMillis();
            logger.info("partition operation finished: {}, cost: {}ms", this.elements, (endTime - startTime));
        } catch (Exception e) {
            logger.error("task fail: {}", this.elements, e);
        }
        return this.elements;
    }

    public PartitionElements getElements() {
        return elements;
    }

    public void setElements(PartitionElements elements) {
        this.elements = elements;
    }

    public OperationThreadService getService() {
        return service;
    }

    public void setService(OperationThreadService service) {
        this.service = service;
    }

}
~~~~
~~~~
import cn.henry.study.common.bo.PartitionElements;
import cn.henry.study.common.service.OperationThreadService;
import cn.henry.study.common.thread.OperationThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.concurrent.Future;
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
        ArrayList<Future<PartitionElements>> futures = new ArrayList<>((int) elements.getPartitions());
        OperationThread opThread = null;
        Future<PartitionElements> future = null;
        // 添加线程任务
        for (int i = 0; i < elements.getPartitions(); i++) {
            // 划定任务分布
            opThread = new OperationThread(new PartitionElements(i + 1, elements), service);
            future = executor.submit(opThread);
            futures.add(future);
        }
        // 关闭线程池
        executor.shutdown();
        // 阻塞线程，同步处理数据
        futures.forEach(f -> {
            try {
                // 线程单个任务结束后的归并方法
                service.post(f.get(), obj);
            } catch (Exception e) {
                logger.error("post routine fail", e);
            }
        });
        return service.finished(obj);
    }

}
~~~~
~~~~
import cn.henry.study.common.bo.PartitionElements;
import cn.henry.study.common.service.OperationThreadService;
import cn.henry.study.common.utils.MultiThreadOperationUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * description:
 *
 * @author Hlingoes
 * @date 2020/6/12 20:52
 */
public class MultiThreadServiceTest implements OperationThreadService {
    private static Logger logger = LoggerFactory.getLogger(MultiThreadServiceTest.class);

    @Override
    public long count(Object[] args) throws Exception {
        return 100L;
    }

    @Override
    public Object prepare(Object[] args) throws Exception {
        return "success";
    }

    @Override
    public Object invoke(PartitionElements elements) throws Exception {
        List<Object> list = new ArrayList<>((int) elements.getBatchCounts());
        for (int i = 0; i < elements.getIndex(); i++) {
            list.add("test_" + i);
        }
        return list;
    }

    @Override
    public void post(PartitionElements elements, Object object) throws Exception {
        String insertSql = "insert into test (id) values ";
        StringBuilder sb = new StringBuilder();
        List<Object> datas = (List<Object>) elements.getData();
        for (int i = 0; i < datas.size(); i++) {
            if ((i + 1) % 5 == 0 || (i + 1) == datas.size()) {
                sb.append("('" + datas.get(i) + "')");
                logger.info("{}: 测试insert sql: {}", elements, insertSql + sb.toString());
                sb = new StringBuilder();
            } else {
                sb.append("('" + datas.get(i) + "'),");
            }
        }
    }

    @Override
    public Object finished(Object object) throws Exception {
        return object;
    }

    @Test
    public void testBatchExecute() {
        try {
            Object object = MultiThreadOperationUtils.batchExecute(new MultiThreadServiceTest(), 10, new Object[]{"test"});
            logger.info("测试完成: {}", object.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
~~~~
##### 总结：这是一个抽象之后的多线程业务流程处理方式，已在生产环境使用，多线程的重点在业务分割和思想上，有清晰的责任划分。
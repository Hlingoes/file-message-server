package cn.henry.study.pool;

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
public class CustomThreadFactoryBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomThreadFactoryBuilder.class);

    private String nameFormat = null;
    private boolean daemon = false;
    private int priority = Thread.NORM_PRIORITY;

    public CustomThreadFactoryBuilder setNameFormat(String nameFormat) {
        if (nameFormat == null) {
            throw new NullPointerException();
        }
        this.nameFormat = nameFormat;
        return this;
    }

    public CustomThreadFactoryBuilder setDaemon(boolean daemon) {
        this.daemon = daemon;
        return this;
    }

    public CustomThreadFactoryBuilder setPriority(int priority) {
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

    private static ThreadFactory build(CustomThreadFactoryBuilder builder) {
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
                LOGGER.error("error occurred! threadName: {}, error msg: {}", threadName, e.getMessage(), e);
            });
            return thread;
        };
    }
}

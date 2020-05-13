package cn.henry.study.common;

import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

/**
 * description: 测试java的退出钩子和文件锁，适用于只有一个java实例在运行
 * 文件锁分为2类，只能通过FileChannel对象来使用：
 * 1. 排它锁：又叫独占锁。对文件加排它锁后，该进程可以对此文件进行读写，该进程独占此文件，
 * 其他进程不能读写此文件，直到该进程释放文件锁。
 * 2. 共享锁：某个进程对文件加共享锁，其他进程也可以访问此文件，但这些进程都只能读此文件，不能写。线程是安全的。
 * 只要还有一个进程持有共享锁，此文件就只能读，不能写。
 * <p>
 * 有4种获取文件锁的方法：
 * 1. 对整个文件加锁，默认为排它锁，阻塞的方法，当文件锁不可用时，当前进程会被挂起
 * lock()
 * 2. 自定义加锁方式，前2个参数指定要加锁的部分（可以只对此文件的部分内容加锁），第三个参数值指定是否是共享锁
 * lock(long position, long size, boolean shared)
 * 3. 对整个文件加锁，默认为排它锁，非阻塞的方法，当文件锁不可用时，tryLock()会得到null值
 * tryLock()
 * 4. 自定义加锁方式，前2个参数指定要加锁的部分（可以只对此文件的部分内容加锁），第三个参数值指定是否是共享锁
 * tryLock(long position, long size, boolean shared)
 * <p>
 * 如果指定为共享锁，则其它进程可读此文件，所有进程均不能写此文件，如果某进程试图对此文件进行写操作，会抛出异常
 * 备注：在某些OS上，对某个文件加锁后，不能对此文件使用通道映射
 *
 * @author Hlingoes
 * @date 2020/5/13 21:00
 */
public class FileLockTest {
    /**
     * Reference to the JVM shutdown hook, if registered
     */
    private Thread shutdownHook;

    @Test
    public void testFileLocking() {
        String filePath = "lock_test.txt";
        FileChannel channel = null;
        try {
            File lockFile = new File(filePath);
            if (lockFile.exists()) {
                channel = new FileOutputStream(lockFile).getChannel();
            } else {
                channel = new FileOutputStream(classLoader().getResource(filePath).getPath()).getChannel();
            }
            final FileLock lock = channel.tryLock();
            if (lock != null) {
                this.shutdownHook = new Thread(() -> {
                    if (lock != null) {
                        try {
                            lock.release();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                // 注册jvm关闭事件
                Runtime.getRuntime().addShutdownHook(this.shutdownHook);
                // 一直运行下去的业务逻辑
                while (true) {
                    System.out.println("testFileLocking() waiting for you");
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                // 得不到锁,退出程序，这能保证该进程同时只能执行一个
                System.out.println("testFileLocking正在运行，防止启动多实例，当前启动程序退出");
                System.exit(-1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (channel != null) {
                try {
                    channel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Test
    public void testFileLocking2() {
        testFileLocking();
    }

    /**
     * description: 获取当前线程加载器
     *
     * @param
     * @return java.lang.ClassLoader
     * @author Hlingoes 2020/5/13
     */
    public ClassLoader classLoader() {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader == null) {
            loader = ClassLoader.getSystemClassLoader();
        }
        return loader;
    }

}

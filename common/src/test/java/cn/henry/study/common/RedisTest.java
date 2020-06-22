package cn.henry.study.common;

import cn.henry.study.common.utils.RedisPoolUtils;
import cn.henry.study.common.utils.ThreadPoolExecutorUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * description:
 *
 * @author Hlingoes
 * @date 2020/6/23 0:21
 */
public class RedisTest {
    private static Logger logger = LoggerFactory.getLogger(RedisTest.class);

    private static String lockKey = "hulin_redis";
    private static String requestId = "hulin_requestId";
    private static int expireTime = 300;

    class RedisLockThread implements Runnable {

        @Override
        public void run() {
            while (true) {
                logger.info("{} begin ...", Thread.currentThread().getName());
                if (RedisPoolUtils.tryGetDistributedLock(lockKey, requestId, expireTime)) {
                    logger.info("{} get lock", Thread.currentThread().getName());
                    try {
                        Thread.sleep(100L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    RedisPoolUtils.releaseDistributedLock(lockKey, requestId);
                    logger.info("{} release lock", Thread.currentThread().getName());
                } else {
                    logger.info("{} miss the lock", Thread.currentThread().getName());
                }
                try {
                    Thread.sleep(100L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Test
    public void redisLockTest() {
        RedisPoolUtils.releaseDistributedLock(lockKey, requestId);
        ThreadPoolExecutor executor = ThreadPoolExecutorUtils.getExecutorPool();
        for (int i = 0; i < ThreadPoolExecutorUtils.defaultCoreSize; i++) {
            executor.execute(new RedisLockThread());
        }
        executor.shutdown();
        ThreadPoolExecutorUtils.closeAfterComplete(executor);
    }
}

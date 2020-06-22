package cn.henry.study.common.utils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * description:
 * Redis连接数与最大连接数
 * 在redis-cli命令行使用：info clients可以查看当前的redis连接数
 * 127.0.0.1:6379> info clients
 * config get maxclients 可以查询redis允许的最大连接数
 * 127.0.0.1:6379> CONFIG GET maxclients
 * config set maxclients num 可以设置redis允许的最大连接数
 * CONFIG set maxclients 10000
 *
 * @author Hlingoes
 * @date 2020/6/23 0:02
 */
public class RedisPool {
    /**
     * jedis连接池
     */
    private static JedisPool pool;
    /**
     * 最大连接数
     */
    private static int maxTotal = 10;
    /**
     * 最大空闲连接数
     */
    private static int maxIdle = 5;
    /**
     * 最小空闲连接数
     */
    private static int minIdle = 0;
    /**
     * 在取连接时测试连接的可用性
     */
    private static boolean testOnBorrow = true;
    /**
     * 再还连接时不测试连接的可用性
     */
    private static boolean testOnReturn = false;

    static {
        // 初始化连接池
        initPool();
    }

    /**
     * description: 获取jedis实例
     *
     * @param
     * @return redis.clients.jedis.Jedis
     * @author Hlingoes 2020/6/23
     */
    public static Jedis getJedis() {
        return pool.getResource();
    }

    /**
     * description: 关闭jedis连接
     *
     * @param jedis
     * @return void
     * @author Hlingoes 2020/6/23
     */
    public static void close(Jedis jedis) {
        jedis.close();
    }

    /**
     * description: 初始化redis连接池
     *
     * @param
     * @return void
     * @author Hlingoes 2020/6/23
     */
    private static void initPool() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdle);
        config.setMinIdle(minIdle);
        config.setTestOnBorrow(testOnBorrow);
        config.setTestOnReturn(testOnReturn);
        config.setBlockWhenExhausted(true);
        pool = new JedisPool(config, "127.0.0.1", 6379, 3000);
    }
}

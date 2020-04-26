package cn.henry.study.web.database;

/**
 * description: threadlocal实现的，线程安全的datebase容器
 *
 * @author Hlingoes
 * @date 2020/4/2 22:54
 */
public class DatabaseContextHolder {
    private static ThreadLocal<String> routeKey = new ThreadLocal<String>();

    /**
     * description: 绑定当前线程数据源路由的key，在使用完成之后，必须调用removeRouteKey()方法删除
     *
     * @param type
     * @return void
     * @author Hlingoes 2020/4/3
     */
    public static void setRouteKey(String type) {
        routeKey.set(type);
    }

    /**
     * description: 获取当前线程的数据源路由的key
     *
     * @param 
     * @return java.lang.String
     * @author Hlingoes 2020/4/3
     */
    public static String getRouteKey() {
        return routeKey.get();
    }

    /**
     * description:  删除与当前线程绑定的数据源路由的key
     *
     * @param
     * @return void
     * @author Hlingoes 2020/4/3
     */
    public static void removeRouteKey() {
        routeKey.remove();
    }
}

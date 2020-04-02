package cn.henry.study.database;

/**
 * description: threadlocal实现的，线程安全的datebase容器
 *
 * @author Hlingoes
 * @date 2020/4/2 22:54
 */
public class DatabaseContextHolder {
    private static final ThreadLocal<String> CONTEXT_HOLDER = new ThreadLocal<>();

    public static void setDatabaseType(String type) {
        CONTEXT_HOLDER.set(type);
    }

    public static String getDatabaseType() {
        return CONTEXT_HOLDER.get();
    }

    public static void remove() {
        CONTEXT_HOLDER.remove();
    }
}

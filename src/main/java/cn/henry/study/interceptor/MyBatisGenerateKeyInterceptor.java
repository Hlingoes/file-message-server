package cn.henry.study.interceptor;

import cn.henry.study.base.Metas;
import cn.henry.study.utils.SnowflakeIdWorker;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.sql.Timestamp;
import java.util.*;

/**
 * description: 生成主键拦截器，自动生成id
 *
 * @author Hlingoes
 * @date 2020/4/3 19:58
 */
@Component
@Intercepts({@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})})
public class MyBatisGenerateKeyInterceptor implements Interceptor {

    private static final SnowflakeIdWorker SNOW_FLAKE = new SnowflakeIdWorker(1, 1);
    /**
     * 主键名
     */
    private static final String KEY_NAME = "id";
    /**
     * 创建时间
     */
    private static final String CREATE_TIME = "createTime";
    /**
     * 修改时间
     */
    private static final String UPDATE_TIME = "updateTime";
    /**
     * 主键类型
     */
    private static final String KEY_TYPE = "long";
    /**
     * 时间类型
     */
    private static final String TIME_TYPE = "Timestamp";

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        // 获取 SQL
        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
        // 不是 insert 类型的跳过
        if (!SqlCommandType.INSERT.equals(sqlCommandType)) {
            return invocation.proceed();
        }
        // 获取参数
        Object parameter = invocation.getArgs()[1];
        generatedMetas(parameter);
        return invocation.proceed();
    }

    /**
     * 获取私有成员变量 ,并设置Metas的值
     *
     * @param parameter 参数
     */
    private void generatedMetas(Object parameter) throws Throwable {
        // 如果继承了必备属性
        if (parameter instanceof Metas) {
            ReflectionUtils.doWithFields(parameter.getClass(), field -> {
                ReflectionUtils.makeAccessible(field);
                if (KEY_NAME.equals(field.getName()) && KEY_TYPE.equals(field.getType().getSimpleName())) {
                    // 设置雪花id
                    field.set(parameter, SNOW_FLAKE.nextId());
                }
                if (CREATE_TIME.equals(field.getName()) && TIME_TYPE.equals(field.getType().getSimpleName())
                        && field.get(parameter) == null) {
                    // 设置创建时间
                    field.set(parameter, new Timestamp(System.currentTimeMillis()));
                }
                if (UPDATE_TIME.equals(field.getName()) && TIME_TYPE.equals(field.getType().getSimpleName())) {
                    // 设置修改时间
                    field.set(parameter, new Timestamp(System.currentTimeMillis()));
                }
            });
        }
    }

    /**
     * Plugin.wrap生成拦截代理对象
     */
    @Override
    public Object plugin(Object o) {
        if (o instanceof Executor) {
            return Plugin.wrap(o, this);
        } else {
            return o;
        }
    }

    @Override
    public void setProperties(Properties properties) {

    }
}

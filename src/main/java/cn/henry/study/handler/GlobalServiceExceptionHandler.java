package cn.henry.study.handler;

import cn.henry.study.constants.HeaderConstants;
import cn.henry.study.exceptions.DataSendFailRetryException;
import com.google.common.base.Stopwatch;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * description: 通过AOP统一处理业务异常
 *
 * @author Hlingoes
 * @date 2020/3/23 22:26
 */

@Component
@Aspect
public class GlobalServiceExceptionHandler {
    public static final Logger LOGGER = LoggerFactory.getLogger(GlobalServiceExceptionHandler.class);
    public static final Logger RETRY_LOGGER = LoggerFactory.getLogger(DataSendFailRetryException.class);

    @Pointcut("execution(* cn.henry.study.service..*.*(..)) " +
            "&& !execution(* cn.henry.study.service..WebSocketService.*(..))" +
            "&& !execution(* cn.henry.study.service..rabbitmq.*.*(..))")
    public void pointCut() {

    }

    @Around("pointCut()")
    public Object handleControllerMethod(ProceedingJoinPoint pjp) throws Throwable {
        Stopwatch stopwatch = Stopwatch.createStarted();
        Object result = null;
        String siftLogName = null;
        LOGGER.info("执行开始: {}, 参数: ", pjp.getSignature(), pjp.getArgs());
        try {
            // 执行方法体
            result = pjp.proceed(pjp.getArgs());
            Long consumeTime = stopwatch.stop().elapsed(TimeUnit.MILLISECONDS);
            LOGGER.info("耗时: {}ms", consumeTime);
            LOGGER.info("执行结束: {}, 返回值: {}, 耗时: {}ms", pjp.getSignature(), result, consumeTime);
        } catch (DataSendFailRetryException ex) {
            /**
             * logback.xml中discriminator根据siftLogName这个key的value来决定
             * siftLogName的value通过这种方式设置， 这里设置的key-value对是保存在一个ThreadLocal<Map>中
             * 不会对其他线程中的siftLogName这个key产生影响
             */
            siftLogName = ex.getService().getEntityClazz().getSimpleName() + HeaderConstants.DATA_RETRY_SUFFIX;
            MDC.put("siftLogName", siftLogName);
            RETRY_LOGGER.info("{}", ex.getData());
            LOGGER.error(pjp.getSignature() + " 接口记录返回结果失败！，原因为：{}", ex.getMessage());
        } finally {
            // remember remove this
            if (StringUtils.isNotEmpty(siftLogName)) {
                MDC.remove(siftLogName);
            }
        }
        return result;
    }

}

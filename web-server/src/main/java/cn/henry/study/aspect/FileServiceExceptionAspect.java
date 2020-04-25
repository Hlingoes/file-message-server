package cn.henry.study.aspect;

import cn.henry.study.entity.MessageBrief;
import cn.henry.study.exceptions.FileFailRetryException;
import cn.henry.study.factory.FileServiceFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * description: 通过AOP统一处理业务异常
 *
 * @author Hlingoes
 * @date 2020/3/23 22:26
 */

@Component
@Aspect
public class FileServiceExceptionAspect {
    public static Logger logger = LoggerFactory.getLogger(FileServiceExceptionAspect.class);

    @Autowired
    private FileServiceFactory fileServiceFactory;

    /**
     * 定义命名的切点，拦截cn.henry.study.service.files包中的upload开头的方法
     */
    @Pointcut("execution(* cn.henry.study.service.files..*.*load*(..)) ")
    public void uploadException() {

    }

    /**
     * 环绕通知方法
     */
    @Around("uploadException()")
    public Object watchMethodException(ProceedingJoinPoint pjp) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = null;
        logger.info("执行开始: {}, 参数: ", pjp.getSignature(), pjp.getArgs());
        try {
            // 执行方法体
            result = pjp.proceed(pjp.getArgs());
            long consumeTime = System.currentTimeMillis() - startTime;
            logger.info("执行结束: {}, 返回值: {}, 耗时: {}ms", pjp.getSignature(), result, consumeTime);
        } catch (FileFailRetryException ex) {
            fileServiceFactory.cacheFailData((MessageBrief) ex.getData());
            logger.error("{} 接口记录返回结果失败！，原因为：{}", pjp.getSignature(), ex.getMessage());
        }
        return result;
    }

}

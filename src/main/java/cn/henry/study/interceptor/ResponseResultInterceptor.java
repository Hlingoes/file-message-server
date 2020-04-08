package cn.henry.study.interceptor;

import cn.henry.study.anno.ResponseResult;
import com.google.common.util.concurrent.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.HandlerMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

/**
 * description: 接口响应体控制拦截器
 *
 * @author Hlingoes
 * @date 2020/1/1 23:44
 */
@Component
public class ResponseResultInterceptor implements HandlerInterceptor {
    private static Logger logger = LoggerFactory.getLogger(ResponseResultInterceptor.class);
    public static final String RESPONSE_RESULT = "RESPONSE-RESULT";
    /**
     *  set the number of requests per second
     */
    private static final ConcurrentHashMap<String, RateLimiter> LIMITET_MAP = new ConcurrentHashMap<>();

    static {
        LIMITET_MAP.put("/hello1", RateLimiter.create(1));
        LIMITET_MAP.put("/hello2", RateLimiter.create(2));
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 令牌桶功能，超过了则提示
        // http://localhost:8080/jqueryLearn/resources/request.jsp
        String url = request.getRequestURL().toString();
        // /jqueryLearn/resources/request.jsp
        String uri = request.getRequestURI();
        // /jqueryLearn
        String contextPath = request.getContextPath();
        // /resources/request.jsp
        String servletPath = request.getServletPath();
        logger.info("访问参数：url: {}, uri: {}, contextPath: {}, servletPath: {}", url, uri, contextPath, servletPath);
        if (LIMITET_MAP.get(uri) != null && !LIMITET_MAP.get(uri).tryAcquire()) {
            logger.error("访问过于频繁，请稍后再试");
            return false;
        }
        if (handler instanceof HandlerMethod) {
            final HandlerMethod handlerMethod = (HandlerMethod) handler;
            final Class<?> clazz = handlerMethod.getBeanType();
            final Method method = handlerMethod.getMethod();
            if (clazz.isAnnotationPresent(ResponseResult.class)) {
                request.setAttribute(RESPONSE_RESULT, clazz.getAnnotation(ResponseResult.class));
            } else if (method.isAnnotationPresent(ResponseResult.class)) {
                request.setAttribute(RESPONSE_RESULT, method.getAnnotation(ResponseResult.class));
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // nothing to do
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // nothing to do
    }
}

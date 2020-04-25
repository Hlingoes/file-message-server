package cn.henry.study.handler;

import cn.henry.study.anno.ResponseResult;
import cn.henry.study.interceptor.ResponseResultInterceptor;
import cn.henry.study.result.CommonResult;
import cn.henry.study.result.DefaultWebErrorResult;
import cn.henry.study.result.Result;
import cn.henry.study.utils.JacksonUtils;
import cn.henry.study.utils.RequestContextHolderUtils;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * description: 接口响应体处理器
 *
 * @author Hlingoes
 * @date 2020/1/2 0:01
 */
@ControllerAdvice
public class GlobalResponseResultHandler implements ResponseBodyAdvice<Object> {

    /**
     * description: 这个方法表示对于哪些请求要执行beforeBodyWrite，返回true执行，返回false不执行
     *
     * @param methodParameter
     * @param aClass
     * @return boolean
     * @author Hlingoes 2020/1/2
     */
    @Override
    public boolean supports(MethodParameter methodParameter, Class aClass) {
        HttpServletRequest request = RequestContextHolderUtils.getRequest();
        ResponseResult responseResultAnn = (ResponseResult) request.getAttribute(ResponseResultInterceptor.RESPONSE_RESULT);
        return responseResultAnn != null;
    }

    /**
     * description: 对于返回的对象如果不是最终对象ResponseResult，则包装一下
     *
     * @param body
     * @param methodParameter
     * @param mediaType
     * @param aClass
     * @param serverHttpRequest
     * @param serverHttpResponse
     * @return java.lang.Object
     * @author Hlingoes 2020/1/2
     */
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter methodParameter, MediaType mediaType,
                                  Class aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        ResponseResult responseResultAnn = (ResponseResult) RequestContextHolderUtils.getRequest()
                .getAttribute(ResponseResultInterceptor.RESPONSE_RESULT);
        Class<? extends Result> resultClazz = responseResultAnn.value();
        if (resultClazz.isAssignableFrom(CommonResult.class)) {
            if (body instanceof DefaultWebErrorResult) {
                DefaultWebErrorResult defaultWebErrorResult = (DefaultWebErrorResult) body;
                CommonResult result = new CommonResult();
                result.setCode(defaultWebErrorResult.getCode());
                result.setMsg(defaultWebErrorResult.getMessage());
                result.setData(defaultWebErrorResult.getErrors());
                return result;
            } else if (body instanceof String) {
                return JacksonUtils.object2Str(CommonResult.success(body));
            }
            return CommonResult.success(body);
        }
        return body;
    }

}

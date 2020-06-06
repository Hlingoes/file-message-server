package cn.henry.study.web.anno;

import cn.henry.study.common.result.CommonResult;

import java.lang.annotation.*;

/**
 * @author zhumaer
 * @desc 接口返回结果增强  会通过拦截器拦截后放入标记，在ResponseResultHandler 进行结果处理
 * @since 4/1/2018 3:00 PM
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ResponseResult {
    Class<? extends CommonResult> value() default CommonResult.class;
}

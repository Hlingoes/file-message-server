package cn.henry.study.web.base;

import cn.henry.study.common.bo.ParameterInvalidItem;
import cn.henry.study.web.utils.ConvertUtils;
import cn.henry.study.common.enums.HttpBasedStatusEnum;
import cn.henry.study.common.exceptions.BaseException;
import cn.henry.study.web.result.DefaultWebErrorResult;
import cn.henry.study.common.result.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.util.List;

/**
 * description: 全局异常处理基础类
 *
 * @author Hlingoes
 * @date 2020/1/1 23:29
 */
public abstract class BaseGlobalExceptionHandler {

    private static Logger logger = LoggerFactory.getLogger(BaseGlobalExceptionHandler.class);

    /**
     * 违反约束异常
     */
    protected DefaultWebErrorResult handleConstraintViolationException(ConstraintViolationException e, HttpServletRequest request) {
        logger.info("handleConstraintViolationException start, uri:{}, caused by: ", request.getRequestURI(), e);
        List<ParameterInvalidItem> parameterInvalidItemList = ConvertUtils.convertCVSetToParameterInvalidItemList(e.getConstraintViolations());
        return DefaultWebErrorResult.failure(ResultCode.PARAM_IS_INVALID, e, HttpBasedStatusEnum.BAD_REQUEST, parameterInvalidItemList);
    }

    /**
     * 处理验证参数封装错误时异常
     */
    protected DefaultWebErrorResult handleConstraintViolationException(HttpMessageNotReadableException e, HttpServletRequest request) {
        logger.info("handleConstraintViolationException start, uri:{}, caused by: ", request.getRequestURI(), e);
        return DefaultWebErrorResult.failure(ResultCode.PARAM_IS_INVALID, e, HttpBasedStatusEnum.BAD_REQUEST);
    }

    /**
     * 处理参数绑定时异常（反400错误码）
     */
    protected DefaultWebErrorResult handleBindException(BindException e, HttpServletRequest request) {
        logger.info("handleBindException start, uri:{}, caused by: ", request.getRequestURI(), e);
        List<ParameterInvalidItem> parameterInvalidItemList = ConvertUtils.convertBindingResultToMapParameterInvalidItemList(e.getBindingResult());
        return DefaultWebErrorResult.failure(ResultCode.PARAM_IS_INVALID, e, HttpBasedStatusEnum.BAD_REQUEST, parameterInvalidItemList);
    }

    /**
     * 处理使用@Validated注解时，参数验证错误异常（反400错误码）
     */
    protected DefaultWebErrorResult handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        logger.info("handleMethodArgumentNotValidException start, uri:{}, caused by: ", request.getRequestURI(), e);
        List<ParameterInvalidItem> parameterInvalidItemList = ConvertUtils.convertBindingResultToMapParameterInvalidItemList(e.getBindingResult());
        return DefaultWebErrorResult.failure(ResultCode.PARAM_IS_INVALID, e, HttpBasedStatusEnum.BAD_REQUEST, parameterInvalidItemList);
    }

    /**
     * 处理通用自定义业务异常
     */
    protected ResponseEntity<DefaultWebErrorResult> handleBusinessException(BaseException e, HttpServletRequest request) {
        logger.info("handleBusinessException start, uri:{}, exception:{}, caused by: {}", request.getRequestURI(), e.getClass(), e.getMessage());
        DefaultWebErrorResult defaultWebErrorResult = DefaultWebErrorResult.failure(e);
        return ResponseEntity.status(HttpStatus.valueOf(defaultWebErrorResult.getStatus())).body(defaultWebErrorResult);
    }

    /**
     * 处理运行时系统异常（反500错误码）
     */
    protected DefaultWebErrorResult handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        logger.error("handleRuntimeException start, uri:{}, caused by: ", request.getRequestURI(), e);
        return DefaultWebErrorResult.failure(ResultCode.SYSTEM_INNER_ERROR, e, HttpBasedStatusEnum.INTERNAL_SERVER_ERROR);
    }

    /**
     * 处理未预测到的其他错误（反500错误码）
     */
    protected DefaultWebErrorResult handleThrowable(Throwable e, HttpServletRequest request) {
        logger.error("handleThrowable start, uri:{}, caused by: ", request.getRequestURI(), e);
        return DefaultWebErrorResult.failure(ResultCode.SYSTEM_INNER_ERROR, e, HttpBasedStatusEnum.INTERNAL_SERVER_ERROR);
    }

}

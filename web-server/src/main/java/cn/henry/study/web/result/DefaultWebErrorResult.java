package cn.henry.study.web.result;

import cn.henry.study.common.exceptions.BaseException;
import cn.henry.study.common.result.Result;
import cn.henry.study.common.result.ResultCode;
import cn.henry.study.web.utils.RequestContextHolderUtils;
import cn.henry.study.common.enums.ExceptionEnum;
import cn.henry.study.common.enums.HttpBasedStatusEnum;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * description: 默认全局错误返回格式
 * <p>
 * 该返回信息是spring boot的默认异常时返回结果，也是服务的默认的错误返回结果
 *
 * @author Hlingoes
 * @date 2020/1/1 22:42
 */
public class DefaultWebErrorResult implements Serializable, Result {
    private static final long serialVersionUID = 1899083570489722793L;

    /**
     * HTTP响应状态码
     */
    private Integer status;

    /**
     * HTTP响应状态码的英文提示
     */
    private String error;

    /**
     * 异常堆栈的精简信息
     */
    private String message;

    /**
     * 系统内部自定义的返回值编码，{@link ResultCode} 它是对错误更加详细的编码
     * <p>
     * 备注：spring boot默认返回异常时，该字段为null
     */
    private Integer code;

    /**
     * 调用接口路径
     */
    private String path;

    /**
     * 异常的名字
     */
    private String exception;

    /**
     * 异常的错误传递的数据
     */
    private Object errors;

    /**
     * 时间戳
     */
    private Date timestamp;

    public static DefaultWebErrorResult failure(ResultCode resultCode, Throwable e, HttpBasedStatusEnum httpStatus, Object errors) {
        DefaultWebErrorResult result = DefaultWebErrorResult.failure(resultCode, e, httpStatus);
        result.setErrors(errors);
        return result;
    }

    public static DefaultWebErrorResult failure(ResultCode resultCode, Throwable e, HttpBasedStatusEnum httpStatus) {
        DefaultWebErrorResult result = new DefaultWebErrorResult();
        result.setCode(resultCode.code());
        result.setMessage(resultCode.message());
        result.setStatus(httpStatus.value());
        result.setError(httpStatus.getReasonPhrase());
        result.setException(e.getClass().getName());
        result.setPath(RequestContextHolderUtils.getRequest().getRequestURI());
        result.setTimestamp(new Date());
        return result;
    }

    public static DefaultWebErrorResult failure(BaseException e) {
        ExceptionEnum ee = ExceptionEnum.getByEClass(e.getClass());
        if (ee != null) {
            return DefaultWebErrorResult.failure(ee.getResultCode(), e, ee.getHttpStatus(), e.getData());
        }

        DefaultWebErrorResult defaultWebErrorResult = DefaultWebErrorResult.failure(e.getResultCode() == null ? ResultCode.SUCCESS : e.getResultCode(), e, HttpBasedStatusEnum.OK, e.getData());
        if (StringUtils.isNotEmpty(e.getMessage())) {
            defaultWebErrorResult.setMessage(e.getMessage());
        }
        return defaultWebErrorResult;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public Object getErrors() {
        return errors;
    }

    public void setErrors(Object errors) {
        this.errors = errors;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}

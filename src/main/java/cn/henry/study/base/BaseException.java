package cn.henry.study.base;

import cn.henry.study.result.ResultCode;

/**
 * @author zhumaer
 * @desc 业务异常类，定义的项目中异常的基本方法
 * 1. 在exceptions包中有具体的实现类，将项目中的问题进行归类
 * 2. 在BaseGlobalExceptionHandler中处理exceptions包的每个实例方法
 *
 * @since 9/18/2017 3:00 PM
 */
public class BaseException extends RuntimeException {

    private static final long serialVersionUID = 194906846739586856L;

    protected String code;

    protected String message;

    protected ResultCode resultCode;

    protected Object data;

    public BaseException() {
        ExceptionEnum exceptionEnum = ExceptionEnum.getByEClass(this.getClass());
        if (exceptionEnum != null) {
            resultCode = exceptionEnum.getResultCode();
            code = exceptionEnum.getResultCode().code().toString();
            message = exceptionEnum.getResultCode().message();
        }

    }

    public BaseException(String message) {
        this();
        this.message = message;
    }

    public BaseException(String format, Object... objects) {
        this();
        this.message = String.format(format, objects);
    }

    public BaseException(ResultCode resultCode, Object data) {
        this(resultCode);
        this.data = data;
    }

    public BaseException(ResultCode resultCode) {
        this.resultCode = resultCode;
        this.code = resultCode.code().toString();
        this.message = resultCode.message();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ResultCode getResultCode() {
        return resultCode;
    }

    public void setResultCode(ResultCode resultCode) {
        this.resultCode = resultCode;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}

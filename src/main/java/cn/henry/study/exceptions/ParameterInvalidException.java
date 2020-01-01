package cn.henry.study.exceptions;


import cn.henry.study.base.BaseException;
import cn.henry.study.base.ResultCode;

/**
 * @author zhumaer
 * @desc 参数无效异常
 * @since 9/18/2017 3:00 PM
 */
public class ParameterInvalidException extends BaseException {

    private static final long serialVersionUID = 3721036867889297081L;

    public ParameterInvalidException() {
        super();
    }

    public ParameterInvalidException(Object data) {
        super();
        super.data = data;
    }

    public ParameterInvalidException(ResultCode resultCode) {
        super(resultCode);
    }

    public ParameterInvalidException(ResultCode resultCode, Object data) {
        super(resultCode, data);
    }

    public ParameterInvalidException(String msg) {
        super(msg);
    }

    public ParameterInvalidException(String formatMsg, Object... objects) {
        super(formatMsg, objects);
    }

}

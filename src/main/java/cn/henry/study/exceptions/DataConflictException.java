package cn.henry.study.exceptions;


import cn.henry.study.base.BaseException;
import cn.henry.study.result.ResultCode;

/**
 * @author zhumaer
 * @desc 数据已经存在异常
 * @since 9/18/2017 3:00 PM
 */
public class DataConflictException extends BaseException {

    private static final long serialVersionUID = 3721036867889297081L;

    public DataConflictException() {
        super();
    }

    public DataConflictException(Object data) {
        super.data = data;
    }

    public DataConflictException(ResultCode resultCode) {
        super(resultCode);
    }

    public DataConflictException(ResultCode resultCode, Object data) {
        super(resultCode, data);
    }

    public DataConflictException(String msg) {
        super(msg);
    }

    public DataConflictException(String formatMsg, Object... objects) {
        super(formatMsg, objects);
    }


}

package cn.henry.study.exceptions;


import cn.henry.study.base.BaseException;
import cn.henry.study.base.ResultCode;

/**
 * @author zhumaer
 * @desc 权限不足异常
 * @since 9/18/2017 3:00 PM
 */
public class PermissionForbiddenException extends BaseException {

    private static final long serialVersionUID = 3721036867889297081L;

    public PermissionForbiddenException() {
        super();
    }

    public PermissionForbiddenException(Object data) {
        super.data = data;
    }

    public PermissionForbiddenException(ResultCode resultCode) {
        super(resultCode);
    }

    public PermissionForbiddenException(ResultCode resultCode, Object data) {
        super(resultCode, data);
    }

    public PermissionForbiddenException(String msg) {
        super(msg);
    }

    public PermissionForbiddenException(String formatMsg, Object... objects) {
        super(formatMsg, objects);
    }

}

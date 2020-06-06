package cn.henry.study.common.exceptions;

import cn.henry.study.common.result.ResultCode;

/**
 * description: id生成器的通用异常处理
 *
 * @author du_imba
 * @modify Hlingoes 2020/6/6
 */
public class TinyIdSysException extends BaseException {

    private static final long serialVersionUID = 1L;

    public TinyIdSysException() {
        super();
    }

    public TinyIdSysException(Object data) {
        super.data = data;
    }

    public TinyIdSysException(ResultCode resultCode) {
        super(resultCode);
    }

    public TinyIdSysException(ResultCode resultCode, Object data) {
        super(resultCode, data);
    }

    public TinyIdSysException(String msg) {
        super(msg);
    }

    public TinyIdSysException(String formatMsg, Object... objects) {
        super(formatMsg, objects);
    }
}

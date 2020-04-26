package cn.henry.study.common.exceptions;

import cn.henry.study.common.result.ResultCode;

/**
 * description: 处理通用的文件、消息发送失败的异常
 *
 * @author Hlingoes
 * @date 2020/3/22 23:19
 */
public class FileFailRetryException extends BaseException {

    public FileFailRetryException() {
        super();
    }

    public FileFailRetryException(Object data) {
        super();
        super.data = data;
    }

    public FileFailRetryException(ResultCode resultCode) {
        super(resultCode);
    }

    public FileFailRetryException(ResultCode resultCode, Object data) {
        super(resultCode, data);
    }

    public FileFailRetryException(String msg) {
        super(msg);
    }

    public FileFailRetryException(String formatMsg, Object... objects) {
        super(formatMsg, objects);
    }

}

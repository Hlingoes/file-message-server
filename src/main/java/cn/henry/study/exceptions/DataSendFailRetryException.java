package cn.henry.study.exceptions;

import cn.henry.study.base.BaseException;
import cn.henry.study.base.DefaultFileService;
import cn.henry.study.base.RetryMessage;
import cn.henry.study.result.ResultCode;

/**
 * description: 处理通用的文件、消息发送失败的异常
 *
 * @author Hlingoes
 * @date 2020/3/22 23:19
 */
public class DataSendFailRetryException extends BaseException {

    private DefaultFileService service;

    public DataSendFailRetryException() {
        super();
    }

    public DataSendFailRetryException(DefaultFileService service, RetryMessage retryMessage) {
        super();
        super.data = retryMessage;
        this.service = service;
    }

    public DataSendFailRetryException(Object data) {
        super();
        super.data = data;
    }

    public DataSendFailRetryException(ResultCode resultCode) {
        super(resultCode);
    }

    public DataSendFailRetryException(ResultCode resultCode, Object data) {
        super(resultCode, data);
    }

    public DataSendFailRetryException(String msg) {
        super(msg);
    }

    public DataSendFailRetryException(String formatMsg, Object... objects) {
        super(formatMsg, objects);
    }

    public DefaultFileService getService() {
        return service;
    }

}

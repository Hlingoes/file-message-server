package cn.henry.study.exceptions;

import cn.henry.study.base.BaseException;
import cn.henry.study.base.DefaultFileService;
import cn.henry.study.entity.MessageBrief;
import cn.henry.study.result.ResultCode;

/**
 * description: 处理通用的文件、消息发送失败的异常
 *
 * @author Hlingoes
 * @date 2020/3/22 23:19
 */
public class FailRetryException extends BaseException {

    public FailRetryException() {
        super();
    }

    public FailRetryException(DefaultFileService service, MessageBrief brief) {
        super();
        super.data = brief;
    }

    public FailRetryException(Object data) {
        super();
        super.data = data;
    }

    public FailRetryException(ResultCode resultCode) {
        super(resultCode);
    }

    public FailRetryException(ResultCode resultCode, Object data) {
        super(resultCode, data);
    }

    public FailRetryException(String msg) {
        super(msg);
    }

    public FailRetryException(String formatMsg, Object... objects) {
        super(formatMsg, objects);
    }

}

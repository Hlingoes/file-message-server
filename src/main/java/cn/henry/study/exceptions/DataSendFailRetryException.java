package cn.henry.study.exceptions;

import cn.henry.frame.base.DefaultService;
import cn.henry.study.base.BaseException;
import cn.henry.study.constants.HeaderConstants;
import cn.henry.study.result.ResultCode;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.MDC;

/**
 * description: 处理通用的文件、消息发送失败的异常
 *
 * @author Hlingoes
 * @date 2020/3/22 23:19
 */
public class DataSendFailRetryException extends BaseException {

    public DataSendFailRetryException() {
        super();
    }

    public DataSendFailRetryException(DefaultService service, JSONObject data) {
        super();
        super.data = data;
        /**
         * logback.xml中discriminator根据siftLogName这个key的value来决定
         * siftLogName的value通过这种方式设置， 这里设置的key-value对是保存在一个ThreadLocal<Map>中
         * 不会对其他线程中的siftLogName这个key产生影响
         */
        MDC.put("siftLogName", service.getEntityClazz().getSimpleName() + HeaderConstants.DATA_RETRY_SUFFIX);
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
}

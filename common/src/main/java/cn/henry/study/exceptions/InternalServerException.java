package cn.henry.study.exceptions;

/**
 * @author zhumaer
 * @desc 内部服务异常
 * @since 9/18/2017 3:00 PM
 */
public class InternalServerException extends BaseException {

    private static final long serialVersionUID = 2659909836556958676L;

    public InternalServerException() {
        super();
    }

    public InternalServerException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public InternalServerException(String msg, Throwable cause, Object... objects) {
        super(msg, cause, objects);
    }

    public InternalServerException(String msg) {
        super(msg);
    }

    public InternalServerException(String formatMsg, Object... objects) {
        super(formatMsg, objects);
    }

}

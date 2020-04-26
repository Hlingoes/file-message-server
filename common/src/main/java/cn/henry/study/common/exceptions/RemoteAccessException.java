package cn.henry.study.common.exceptions;


import cn.henry.study.common.result.ResultCode;

/**
 * @desc 远程访问异常
 * 
 * @author zhumaer
 * @since 7/18/2017 3:00 PM
 */
public class RemoteAccessException extends BaseException {

	private static final long serialVersionUID = -832464574076215195L;

	public RemoteAccessException() {
		super();
	}

	public RemoteAccessException(Object data) {
		super.data = data;
	}

	public RemoteAccessException(ResultCode resultCode) {
		super(resultCode);
	}

	public RemoteAccessException(ResultCode resultCode, Object data) {
		super(resultCode, data);
	}

	public RemoteAccessException(String msg) {
		super(msg);
	}

	public RemoteAccessException(String formatMsg, Object... objects) {
		super(formatMsg, objects);
	}

}

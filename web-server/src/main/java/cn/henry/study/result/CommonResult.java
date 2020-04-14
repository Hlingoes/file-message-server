package cn.henry.study.result;

/**
 * description: 通用的返回结果
 *
 * @author Hlingoes
 * @date 2020/1/1 22:20
 */
public class CommonResult implements Result {
    private static final long serialVersionUID = 874200365941306385L;

    private Integer code;

    private String msg;

    private Object data;

    public static CommonResult success() {
        CommonResult commonResult = new CommonResult();
        commonResult.setResultCode(ResultCode.SUCCESS);
        return commonResult;
    }

    public static CommonResult success(Object data) {
        CommonResult commonResult = new CommonResult();
        commonResult.setResultCode(ResultCode.SUCCESS);
        commonResult.setData(data);
        return commonResult;
    }

    public static CommonResult failure(ResultCode resultCode) {
        CommonResult commonResult = new CommonResult();
        commonResult.setResultCode(resultCode);
        return commonResult;
    }

    public static CommonResult failure(ResultCode resultCode, Object data) {
        CommonResult commonResult = new CommonResult();
        commonResult.setResultCode(resultCode);
        commonResult.setData(data);
        return commonResult;
    }

    public static CommonResult failure(String message) {
        CommonResult commonResult = new CommonResult();
        commonResult.setCode(ResultCode.PARAM_IS_INVALID.code());
        commonResult.setMsg(message);
        return commonResult;
    }

    private void setResultCode(ResultCode code) {
        this.code = code.code();
        this.msg = code.message();
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}

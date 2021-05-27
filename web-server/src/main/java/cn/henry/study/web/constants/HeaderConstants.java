package cn.henry.study.web.constants;

/**
 * @author zhumaer
 * @desc Header的key罗列
 * @since 8/31/2017 3:00 PM
 */
public class HeaderConstants {

    /**
     * 用户的登录token
     */
    public static final String X_TOKEN = "X-Token";

    /**
     * api的版本号
     */
    public static final String API_VERSION = "Api-Version";

    /**
     * app版本号
     */
    public static final String APP_VERSION = "App-Version";

    /**
     * 调用来源 {CallSourceEnum}
     */
    public static final String CALL_SOURCE = "Call-Source";

    /**
     * API的返回格式 {ApiStyleEnum}
     */
    public static final String API_STYLE = "Api-ExcelStyle";

    /**
     * 失败重传的文件日志前缀名
     */
    public static final String SIFT_LOG_PREFIX = "_fail_retry";

    /**
     * 失败重传的文件日志后缀名
     */
    public static final String SIFT_LOG_SUFFIX = ".log";
}

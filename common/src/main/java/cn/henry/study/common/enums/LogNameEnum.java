package cn.henry.study.common.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * description: 日志枚举类，防止随意生成日志文件
 *
 * @author Hlingoes 2020/6/10
 */
public enum LogNameEnum {
    COMMON("common"),
    WEB_SERVER("webServer"),
    TEST("test"),
    ;

    private String logName;

    LogNameEnum(String fileName) {
        this.logName = fileName;
    }

    public String getLogName() {
        return logName;
    }

    public void setLogName(String logName) {
        this.logName = logName;
    }

    /**
     * description: 获取枚举类
     *
     * @param value
     * @return cn.henry.study.common.enums.LogNameEnum
     * @author Hlingoes 2020/6/10
     */
    public static LogNameEnum getAwardTypeEnum(String value) {
        LogNameEnum[] arr = values();
        for (LogNameEnum item : arr) {
            if (null != item && StringUtils.isNotBlank(item.logName)) {
                return item;
            }
        }
        return null;
    }
}

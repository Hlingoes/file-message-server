package cn.henry.study.enums;
/**
 * description: quartz的任务状态
 *
 * @author Hlingoes 2020/3/29
 */
public enum JobStatus {

    /**
     * 运行状态
     */
    RUNNING("RUNNING"),
    /**
     * 完成状态
     */
    COMPLETE("COMPLETE"),
    /**
     * 暂停状态
     */
    PAUSED("PAUSED");

    /**
     * 响应代码
     */
    private String status;

    JobStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}

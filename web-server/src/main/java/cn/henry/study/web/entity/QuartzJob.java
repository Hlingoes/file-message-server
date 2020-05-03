package cn.henry.study.web.entity;

import cn.henry.study.common.BO.Metas;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;

/**
 * description: 持久化的quartz任务
 *
 * @author Hlingoes 2020/3/29
 * @citation https://gitee.com/youzhibing/spring-boot-2.0.3/tree/master/spring-boot-quartz-plus
 */
@ContentRowHeight(20)
@HeadRowHeight(22)
@ColumnWidth(26)
public class QuartzJob extends Metas {
    private static final long serialVersionUID = 1L;

    /**
     * 任务名称
     */
    @ExcelProperty("Job Name")
    private String jobName;
    /**
     * 任务分组
     */
    @ExcelProperty("Job Group")
    private String jobGroup;
    /**
     * 任务描述
     */
    @ExcelProperty("Description")
    private String description;
    /**
     * 执行类
     */
    /**
     * 宽度为50
     */
    @ColumnWidth(50)
    @ExcelProperty("Job Class Name")
    private String jobClassName;
    /**
     * 执行时间
     */
    @ExcelProperty("Cron Expression")
    private String cronExpression;
    /**
     * 执行时间
     */
    @ExcelProperty("Trigger Name")
    private String triggerName;
    /**
     * 任务状态
     */
    @ExcelProperty("Trigger State")
    private String triggerState;
    /**
     * 任务名称 用于修改
     */
    @ExcelProperty("Old Job Name")
    private String oldJobName;
    /**
     * 任务分组 用于修改
     */
    @ExcelProperty("Old Job Group")
    private String oldJobGroup;

    public QuartzJob() {
        super();
    }

    public QuartzJob(String jobName, String jobGroup, String description, String jobClassName, String cronExpression, String triggerName) {
        super();
        this.jobName = jobName;
        this.jobGroup = jobGroup;
        this.description = description;
        this.jobClassName = jobClassName;
        this.cronExpression = cronExpression;
        this.triggerName = triggerName;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobGroup() {
        return jobGroup;
    }

    public void setJobGroup(String jobGroup) {
        this.jobGroup = jobGroup;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getJobClassName() {
        return jobClassName;
    }

    public void setJobClassName(String jobClassName) {
        this.jobClassName = jobClassName;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public String getTriggerName() {
        return triggerName;
    }

    public void setTriggerName(String triggerName) {
        this.triggerName = triggerName;
    }

    public String getTriggerState() {
        return triggerState;
    }

    public void setTriggerState(String triggerState) {
        this.triggerState = triggerState;
    }

    public String getOldJobName() {
        return oldJobName;
    }

    public void setOldJobName(String oldJobName) {
        this.oldJobName = oldJobName;
    }

    public String getOldJobGroup() {
        return oldJobGroup;
    }

    public void setOldJobGroup(String oldJobGroup) {
        this.oldJobGroup = oldJobGroup;
    }

    @Override
    public String toString() {
        return "QuartzJob{" +
                "jobName='" + jobName + '\'' +
                ", jobGroup='" + jobGroup + '\'' +
                ", description='" + description + '\'' +
                ", jobClassName='" + jobClassName + '\'' +
                ", cronExpression='" + cronExpression + '\'' +
                ", triggerName='" + triggerName + '\'' +
                ", triggerState='" + triggerState + '\'' +
                ", oldJobName='" + oldJobName + '\'' +
                ", oldJobGroup='" + oldJobGroup + '\'' +
                "} " + super.toString();
    }
}

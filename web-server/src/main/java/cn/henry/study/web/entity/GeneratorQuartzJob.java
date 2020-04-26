package cn.henry.study.web.entity;

import cn.henry.study.common.base.Metas;

import java.io.Serializable;

/**
 * 这是MyBatis Generator自动生成的Model Class.
 * 对应的数据表是 : quartz_job
 * @author Administrator
 * @date 2020-04-12 18:24:09
 */
public class GeneratorQuartzJob extends Metas implements Serializable {
    /**
     * 任务名称
     */
    private String jobName;

    /**
     * 任务分组
     */
    private String jobGroup;

    /**
     * 执行类
     */
    private String jobClassName;

    /**
     * cron表达式
     */
    private String cronExpression;

    /**
     * 任务状态
     */
    private String triggerState;

    /**
     * 修改之前的任务名称
     */
    private String oldJobName;

    /**
     * 修改之前的任务分组
     */
    private String oldJobGroup;

    /**
     * 描述
     */
    private String description;

    private static final long serialVersionUID = 1L;

    /**
     * 任务名称
     * @return job_name 任务名称
     */
    public String getJobName() {
        return jobName;
    }

    /**
     * 任务名称
     * @param jobName 任务名称
     */
    public void setJobName(String jobName) {
        this.jobName = jobName == null ? null : jobName.trim();
    }

    /**
     * 任务分组
     * @return job_group 任务分组
     */
    public String getJobGroup() {
        return jobGroup;
    }

    /**
     * 任务分组
     * @param jobGroup 任务分组
     */
    public void setJobGroup(String jobGroup) {
        this.jobGroup = jobGroup == null ? null : jobGroup.trim();
    }

    /**
     * 执行类
     * @return job_class_name 执行类
     */
    public String getJobClassName() {
        return jobClassName;
    }

    /**
     * 执行类
     * @param jobClassName 执行类
     */
    public void setJobClassName(String jobClassName) {
        this.jobClassName = jobClassName == null ? null : jobClassName.trim();
    }

    /**
     * cron表达式
     * @return cron_expression cron表达式
     */
    public String getCronExpression() {
        return cronExpression;
    }

    /**
     * cron表达式
     * @param cronExpression cron表达式
     */
    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression == null ? null : cronExpression.trim();
    }

    /**
     * 任务状态
     * @return trigger_state 任务状态
     */
    public String getTriggerState() {
        return triggerState;
    }

    /**
     * 任务状态
     * @param triggerState 任务状态
     */
    public void setTriggerState(String triggerState) {
        this.triggerState = triggerState == null ? null : triggerState.trim();
    }

    /**
     * 修改之前的任务名称
     * @return old_job_name 修改之前的任务名称
     */
    public String getOldJobName() {
        return oldJobName;
    }

    /**
     * 修改之前的任务名称
     * @param oldJobName 修改之前的任务名称
     */
    public void setOldJobName(String oldJobName) {
        this.oldJobName = oldJobName == null ? null : oldJobName.trim();
    }

    /**
     * 修改之前的任务分组
     * @return old_job_group 修改之前的任务分组
     */
    public String getOldJobGroup() {
        return oldJobGroup;
    }

    /**
     * 修改之前的任务分组
     * @param oldJobGroup 修改之前的任务分组
     */
    public void setOldJobGroup(String oldJobGroup) {
        this.oldJobGroup = oldJobGroup == null ? null : oldJobGroup.trim();
    }

    /**
     * 描述
     * @return description 描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 描述
     * @param description 描述
     */
    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", jobName=").append(jobName);
        sb.append(", jobGroup=").append(jobGroup);
        sb.append(", jobClassName=").append(jobClassName);
        sb.append(", cronExpression=").append(cronExpression);
        sb.append(", triggerState=").append(triggerState);
        sb.append(", oldJobName=").append(oldJobName);
        sb.append(", oldJobGroup=").append(oldJobGroup);
        sb.append(", description=").append(description);
        sb.append("]");
        sb.append(", from super class ");
        sb.append(super.toString());
        return sb.toString();
    }
}
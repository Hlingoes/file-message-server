package cn.henry.study.service.quartz;

import cn.henry.study.entity.QuartzJob;
import cn.henry.study.enums.JobStatusEnum;
import cn.henry.study.job.FailFileRetryJob;
import cn.henry.study.mapper.JobMapper;
import cn.henry.study.utils.SnowflakeIdWorker;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * description: Quartz的web服务类
 *
 * @author Hlingoes 2020/3/29
 */
@Service
public class QuartzJobService {
    private static final SnowflakeIdWorker SNOW_FLAKE = new SnowflakeIdWorker(1, 1);
    private static final String TRIGGER_IDENTITY = "trigger";

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private JobMapper jobMapper;

    public PageInfo listQuartzJob(String jobName, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<QuartzJob> jobList = jobMapper.listJob(jobName);
        PageInfo pageInfo = new PageInfo(jobList);
        return pageInfo;
    }

    public int saveJob(QuartzJob quartz) throws Exception {
        schedulerJob(quartz);
        quartz.setTriggerState(JobStatusEnum.RUNNING.getStatus());
        quartz.setOldJobGroup(quartz.getJobGroup());
        quartz.setOldJobName(quartz.getJobName());
        return jobMapper.saveJob(quartz);
    }

    public void triggerJob(String jobName, String jobGroup) throws SchedulerException {
        JobKey key = new JobKey(jobName, jobGroup);
        scheduler.triggerJob(key);
    }

    public int pauseJob(String jobName, String jobGroup) throws SchedulerException {
        JobKey key = new JobKey(jobName, jobGroup);
        scheduler.pauseJob(key);
        return jobMapper.updateJobStatus(jobName, jobGroup, JobStatusEnum.PAUSED.getStatus());
    }

    public int resumeJob(String jobName, String jobGroup) throws SchedulerException {
        JobKey key = new JobKey(jobName, jobGroup);
        scheduler.resumeJob(key);
        return jobMapper.updateJobStatus(jobName, jobGroup, JobStatusEnum.RUNNING.getStatus());
    }

    public void removeJob(String jobName, String jobGroup) throws SchedulerException {
        TriggerKey triggerKey = TriggerKey.triggerKey(TRIGGER_IDENTITY + jobName, jobGroup);
        scheduler.pauseTrigger(triggerKey);
        scheduler.unscheduleJob(triggerKey);
        scheduler.deleteJob(JobKey.jobKey(jobName, jobGroup));
        jobMapper.removeQuartzJob(jobName, jobGroup);
    }

    public QuartzJob getJob(String jobName, String jobGroup) {
        return jobMapper.getJob(jobName, jobGroup);
    }

    public int updateJob(QuartzJob quartz) throws Exception {
        scheduler.deleteJob(new JobKey(quartz.getOldJobName(), quartz.getOldJobGroup()));
        schedulerJob(quartz);
        quartz.setOldJobGroup(quartz.getJobGroup());
        quartz.setOldJobName(quartz.getJobName());
        return jobMapper.updateJob(quartz);
    }

    public void schedulerJob(QuartzJob job) throws Exception {
        //构建job信息
        Class cls = Class.forName(job.getJobClassName());
        // cls.newInstance(); // 检验类是否存在
        JobDetail jobDetail = JobBuilder.newJob(cls).withIdentity(job.getJobName(), job.getJobGroup())
                .withDescription(job.getDescription()).build();
        // 触发时间点
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCronExpression().trim());
        Trigger trigger = TriggerBuilder.newTrigger().withIdentity(TRIGGER_IDENTITY + job.getJobName(), job.getJobGroup())
                .startNow().withSchedule(cronScheduleBuilder).build();
        //交由Scheduler安排触发
        scheduler.scheduleJob(jobDetail, trigger);
    }

    /**
     * description: 测试新增
     *
     * @param jobName
     * @return cn.henry.study.entity.QuartzJob
     * @author Hlingoes 2020/4/3
     */
    public QuartzJob getTestQuartzJob(String jobName) {
        QuartzJob quartz = new QuartzJob();
        long index = SNOW_FLAKE.nextId();
        String name = jobName + "_" + index;
        quartz.setJobName(name);
        quartz.setJobClassName(FailFileRetryJob.class.getName());
        quartz.setJobGroup(name + "_group");
        quartz.setTriggerName(name + "_trigger");
        quartz.setCronExpression("0 0/5 * * * ? 2020");
        quartz.setDescription("hello metas");
        quartz.setTriggerState(JobStatusEnum.RUNNING.getStatus());
        quartz.setOldJobName(name);
        quartz.setOldJobGroup(name + "_group");
        return quartz;
    }
}
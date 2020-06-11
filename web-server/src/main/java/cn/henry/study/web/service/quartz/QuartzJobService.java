package cn.henry.study.web.service.quartz;

import cn.henry.study.common.utils.SnowflakeIdWorker;
import cn.henry.study.web.entity.QuartzJob;
import cn.henry.study.web.enums.JobStatusEnum;
import cn.henry.study.web.job.FailFileRetryJob;
import cn.henry.study.web.mapper.JobMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

    public List<QuartzJob> findAll() throws DataAccessException {
        return this.jobMapper.findAll();
    }

    public int insertBatch(List<QuartzJob> quartzJobs) throws DataAccessException {
        return this.jobMapper.insertBatch(quartzJobs);
    }

    public int insertSingle(QuartzJob quartz) throws DataAccessException {
        return this.jobMapper.saveJob(quartz);
    }

    public PageInfo listQuartzJob(String jobName, Integer pageNum, Integer pageSize) throws DataAccessException {
        PageHelper.startPage(pageNum, pageSize);
        List<QuartzJob> jobList = this.jobMapper.listJob(jobName);
        PageInfo pageInfo = new PageInfo(jobList);
        return pageInfo;
    }

    public int saveJob(QuartzJob quartz) throws Exception {
        schedulerJob(quartz);
        quartz.setTriggerState(JobStatusEnum.RUNNING.getStatus());
        quartz.setOldJobGroup(quartz.getJobGroup());
        quartz.setOldJobName(quartz.getJobName());
        return this.jobMapper.saveJob(quartz);
    }

    public void triggerJob(String jobName, String jobGroup) throws SchedulerException {
        JobKey key = new JobKey(jobName, jobGroup);
        this.scheduler.triggerJob(key);
    }

    public int pauseJob(String jobName, String jobGroup) throws SchedulerException {
        JobKey key = new JobKey(jobName, jobGroup);
        this.scheduler.pauseJob(key);
        return this.jobMapper.updateJobStatus(jobName, jobGroup, JobStatusEnum.PAUSED.getStatus());
    }

    public int resumeJob(String jobName, String jobGroup) throws SchedulerException {
        JobKey key = new JobKey(jobName, jobGroup);
        this.scheduler.resumeJob(key);
        return this.jobMapper.updateJobStatus(jobName, jobGroup, JobStatusEnum.RUNNING.getStatus());
    }

    public void removeJob(String jobName, String jobGroup) throws SchedulerException {
        TriggerKey triggerKey = TriggerKey.triggerKey(TRIGGER_IDENTITY + jobName, jobGroup);
        this.scheduler.pauseTrigger(triggerKey);
        this.scheduler.unscheduleJob(triggerKey);
        this.scheduler.deleteJob(JobKey.jobKey(jobName, jobGroup));
        this.jobMapper.removeQuartzJob(jobName, jobGroup);
    }

    public void removeGroupJobs(String jobGroup) throws SchedulerException {
        GroupMatcher<JobKey> matcher = GroupMatcher.groupEquals(jobGroup);
        Set<JobKey> jobKeySet = this.scheduler.getJobKeys(matcher);
        List<JobKey> jobKeyList = new ArrayList<JobKey>();
        jobKeyList.addAll(jobKeySet);
        this.scheduler.deleteJobs(jobKeyList);
        jobKeyList.forEach(jobKey -> {
            this.jobMapper.removeQuartzJob(jobKey.getName(), jobGroup);
        });
    }

    public QuartzJob getJob(String jobName, String jobGroup) throws DataAccessException {
        return jobMapper.getJob(jobName, jobGroup);
    }

    public int updateJob(QuartzJob quartz) throws Exception {
        this.scheduler.deleteJob(new JobKey(quartz.getOldJobName(), quartz.getOldJobGroup()));
        schedulerJob(quartz);
        quartz.setOldJobGroup(quartz.getJobGroup());
        quartz.setOldJobName(quartz.getJobName());
        return this.jobMapper.updateJob(quartz);
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
        this.scheduler.scheduleJob(jobDetail, trigger);
    }

    /**
     * description: 测试新增
     *
     * @param jobName
     * @return QuartzJob
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
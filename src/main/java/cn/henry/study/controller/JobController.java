package cn.henry.study.controller;

import cn.henry.study.entity.QuartzJob;
import cn.henry.study.enums.JobStatus;
import cn.henry.study.job.FailFileRetryJob;
import cn.henry.study.mapper.JobMapper;
import cn.henry.study.result.CommonResult;
import cn.henry.study.service.jdbc.QuartzJobService;
import com.github.pagehelper.PageInfo;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

/**
 * description: 定时任务的web接口
 *
 * @author Hlingoes 2020/3/29
 */
@RestController
@RequestMapping("/job")
public class JobController {

    @Autowired
    private QuartzJobService jobService;

    @Autowired
    private JobMapper jobMapper;

    @GetMapping("/testAdding")
    public CommonResult add(String jobName) throws Exception {
        QuartzJob quartz = new QuartzJob();
        long index = System.currentTimeMillis();
        jobName = jobName + "_" + index;
        quartz.setJobName(jobName);
        quartz.setJobClassName(FailFileRetryJob.class.getName());
        quartz.setJobGroup(jobName + "_group");
        quartz.setTriggerName(jobName + "_trigger");
        quartz.setCronExpression("0 0/5 * * * ? 2020");
        quartz.setDescription("hello metas");
        quartz.setTriggerState(JobStatus.RUNNING.getStatus());
        quartz.setOldJobGroup(quartz.getJobGroup());
        quartz.setOldJobName(quartz.getJobName());
        return CommonResult.success(jobMapper.saveJob(quartz));
    }

    @PostMapping("/add")
    public CommonResult save(QuartzJob quartz) throws Exception {
        return CommonResult.success(jobService.saveJob(quartz));
    }

    @PostMapping("/edit")
    public CommonResult edit(QuartzJob quartz) throws Exception {
        return CommonResult.success(jobService.updateJob(quartz));
    }

    @GetMapping("/list")
    public PageInfo list(String jobName, Integer pageNo, Integer pageSize) {
        PageInfo pageInfo = jobService.listQuartzJob(jobName, pageNo, pageSize);
        return pageInfo;
    }

    @GetMapping("/trigger")
    public void trigger(String jobName, String jobGroup) throws SchedulerException {
        jobService.triggerJob(jobName, jobGroup);
    }

    @GetMapping("/pause")
    public CommonResult pause(String jobName, String jobGroup) throws SchedulerException {
        return CommonResult.success(jobService.pauseJob(jobName, jobGroup));
    }

    @GetMapping("/resume")
    public CommonResult resume(String jobName, String jobGroup) throws SchedulerException {
        return CommonResult.success(jobService.resumeJob(jobName, jobGroup));
    }

    @GetMapping("/remove")
    public void remove(String jobName, String jobGroup) throws SchedulerException {
        jobService.removeJob(jobName, jobGroup);
    }
}

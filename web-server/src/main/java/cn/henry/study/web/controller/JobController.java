package cn.henry.study.web.controller;

import cn.henry.study.common.result.CommonResult;
import cn.henry.study.common.result.Result;
import cn.henry.study.web.entity.QuartzJob;
import cn.henry.study.web.mapper.JobMapper;
import cn.henry.study.web.service.quartz.QuartzJobService;
import com.github.pagehelper.PageInfo;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * description: 定时任务的web接口
 *
 * @author Hlingoes 2020/3/29
 */
@RestController
@RequestMapping("/job")
public class JobController {
    private static Logger logger = LoggerFactory.getLogger(JobController.class);

    @Autowired
    private QuartzJobService jobService;

    @Autowired
    private JobMapper jobMapper;

    @GetMapping("/testAdding")
    public Result add(String jobName) throws Exception {
        QuartzJob job = jobService.getTestQuartzJob(jobName);
        jobMapper.saveJob(job);
        return CommonResult.success(job);
    }

    @GetMapping("/testBatchAdding")
    public Result batchAdding(String jobName) throws Exception {
        List<QuartzJob> jobs = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            jobs.add(jobService.getTestQuartzJob(jobName));
        }
        return CommonResult.success(jobMapper.insertBatch(jobs));
    }

    @PostMapping("/add")
    public Result save(QuartzJob quartz) throws Exception {
        return CommonResult.success(jobService.saveJob(quartz));
    }

    @PostMapping("/edit")
    public Result edit(QuartzJob quartz) throws Exception {
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
    public Result pause(String jobName, String jobGroup) throws SchedulerException {
        return CommonResult.success(jobService.pauseJob(jobName, jobGroup));
    }

    @GetMapping("/resume")
    public Result resume(String jobName, String jobGroup) throws SchedulerException {
        return CommonResult.success(jobService.resumeJob(jobName, jobGroup));
    }

    @GetMapping("/remove")
    public void remove(String jobName, String jobGroup) throws SchedulerException {
        jobService.removeJob(jobName, jobGroup);
    }
}

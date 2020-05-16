package cn.henry.study.web.configuration;

import cn.henry.study.web.job.DynamicTableJob;
import cn.henry.study.web.job.FailFileRetryJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

/**
 * description: quartz定时任务的配置类
 *
 * @author Hlingoes
 * @date 2020/3/29 23:14
 */
@Configuration
public class QuartzConfig {

    @Bean
    public JobDetail myJobDetail() {
        JobDetail jobDetail = JobBuilder.newJob(FailFileRetryJob.class)
                .withIdentity("myJob1", "myJobGroup1")
                // JobDataMap可以给任务execute传递参数
                .usingJobData("job_param", "job_param1")
                .storeDurably()
                .build();
        return jobDetail;
    }

    @Bean
    public Trigger myTrigger() {
        Trigger trigger = TriggerBuilder.newTrigger()
                .forJob(myJobDetail())
                .withIdentity("myTrigger1", "myTriggerGroup1")
                .usingJobData("job_trigger_param", "job_trigger_param1")
                .startNow()
                //.withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(5).repeatForever())
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0/2 * * * ? 2020"))
                .build();
        return trigger;
    }

    @Bean
    public JobDetail dynamicTableJobDetail() {
        List<String> serveNames = Arrays.asList("quartz_job");
        JobDataMap jobData = new JobDataMap();
        jobData.putIfAbsent("serveNames", serveNames);
        JobDetail jobDetail = JobBuilder.newJob(DynamicTableJob.class)
                .withIdentity("dynamicTableJob", "dynamicTableJobGroup")
                // JobDataMap可以给任务execute传递参数
                .usingJobData(jobData)
                .storeDurably()
                .build();
        return jobDetail;
    }

    @Bean
    public Trigger dynamicTableTrigger() {
        Trigger trigger = TriggerBuilder.newTrigger()
                .forJob(dynamicTableJobDetail())
                .withIdentity("dynamicTableTrigger", "dynamicTableTriggerGroup")
                .usingJobData("trigger_param_table", "trigger_param_value")
                .startNow()
                // 每月最后一日的上午10:15 触发
                .withSchedule(CronScheduleBuilder.cronSchedule("0 15 10 L * ?"))
                .build();
        return trigger;
    }

}

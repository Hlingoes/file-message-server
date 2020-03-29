package cn.henry.study.configuration;

import cn.henry.study.job.FailFileRetryJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * description: quartz定时任务的配置类
 *
 * @author Hlingoes
 * @date 2020/3/29 23:14
 */
@Configuration
public class QuartzConfig {

    @Bean
    public JobDetail myJobDetail(){
        JobDetail jobDetail = JobBuilder.newJob(FailFileRetryJob.class)
                .withIdentity("myJob1","myJobGroup1")
                // JobDataMap可以给任务execute传递参数
                .usingJobData("job_param","job_param1")
                .storeDurably()
                .build();
        return jobDetail;
    }

    @Bean
    public Trigger myTrigger(){
        Trigger trigger = TriggerBuilder.newTrigger()
                .forJob(myJobDetail())
                .withIdentity("myTrigger1","myTriggerGroup1")
                .usingJobData("job_trigger_param","job_trigger_param1")
                .startNow()
                //.withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(5).repeatForever())
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0/1 * * * ? 2020"))
                .build();
        return trigger;
    }
}

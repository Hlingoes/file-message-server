package cn.henry.study.job;

import cn.henry.study.factory.FileServiceFactory;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * description: 使用样例
 *
 * @author Hlingoes
 * @date 2020/3/29 23:11
 */
@DisallowConcurrentExecution
public class FailFileRetryJob extends QuartzJobBean {
    private static Logger logger = LoggerFactory.getLogger(FailFileRetryJob.class);

    @Autowired
    private FileServiceFactory serviceFactory;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        serviceFactory.reUploadFiles();
        logger.info("FailFileRetryJob job: {}", jobExecutionContext.getJobDetail().getKey());
    }
}

package cn.henry.study.mapper;

import cn.henry.study.entity.QuartzJob;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * description: quartz的数据库调用接口
 *
 * @author Hlingoes 2020/3/29
 */
public interface JobMapper {

    List<QuartzJob> listJob(@Param("jobName") String jobName);

    QuartzJob getJob(@Param("jobName") String jobName, @Param("jobGroup") String jobGroup);

    int saveJob(QuartzJob job);

    int insertBatch(List<QuartzJob> quartzJobs);

    int updateJobStatus(@Param("jobName") String jobName, @Param("jobGroup") String jobGroup, @Param("status") String status);

    int removeQuartzJob(@Param("jobName") String jobName, @Param("jobGroup") String jobGroup);

    int updateJob(QuartzJob quartz);
}

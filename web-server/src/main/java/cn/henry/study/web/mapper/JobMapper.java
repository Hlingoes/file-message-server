package cn.henry.study.web.mapper;

import cn.henry.study.web.entity.QuartzJob;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * description: quartz的数据库调用接口
 *
 * @author Hlingoes 2020/3/29
 */
public interface JobMapper {

    /**
     * description: 获取所有任务
     *
     * @param
     * @return java.util.List<cn.henry.study.web.entity.QuartzJob>
     * @author Hlingoes 2020/5/2
     */
    List<QuartzJob> findAll();
    /**
     * description: 根据jobName查询所有任务
     *
     * @param jobName
     * @return java.util.List<cn.henry.study.web.entity.QuartzJob>
     * @author Hlingoes 2020/5/2
     */
    List<QuartzJob> listJob(@Param("jobName") String jobName);

    /**
     * description: 根据jobName, jobGroup查询所有任务
     *
     * @param jobName
     * @param jobGroup
     * @return cn.henry.study.web.entity.QuartzJob
     * @author Hlingoes 2020/5/2
     */
    QuartzJob getJob(@Param("jobName") String jobName, @Param("jobGroup") String jobGroup);

    /**
     * description: 单个任务保存
     *
     * @param job
     * @return int
     * @author Hlingoes 2020/5/2
     */
    int saveJob(QuartzJob job);

    /**
     * description: 批量保存任务
     *
     * @param quartzJobs
     * @return int
     * @author Hlingoes 2020/5/2
     */
    int insertBatch(List<QuartzJob> quartzJobs);

    /**
     * description: 批量更新任务
     *
     * @param jobName
     * @param jobGroup
     * @param status
     * @return int
     * @author Hlingoes 2020/5/2
     */
    int updateJobStatus(@Param("jobName") String jobName, @Param("jobGroup") String jobGroup, @Param("status") String status);

    /**
     * description: 根据jobName，jobGroup删除任务
     *
     * @param jobName
     * @param jobGroup
     * @return int
     * @author Hlingoes 2020/5/2
     */
    int removeQuartzJob(@Param("jobName") String jobName, @Param("jobGroup") String jobGroup);

    /**
     * description: 更新单条记录
     *
     * @param quartz
     * @return int
     * @author Hlingoes 2020/5/2
     */
    int updateJob(QuartzJob quartz);
}

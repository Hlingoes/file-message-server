package cn.henry.study.web.mapper;

import cn.henry.study.web.entity.QuartzJob;
import org.apache.ibatis.annotations.Param;
import org.springframework.dao.DataAccessException;

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
     * @throws DataAccessException
     * @author Hlingoes 2020/5/2
     */
    List<QuartzJob> findAll() throws DataAccessException;

    /**
     * description: 根据jobName查询所有任务
     *
     * @param jobName
     * @return java.util.List<cn.henry.study.web.entity.QuartzJob>
     * @throws DataAccessException
     * @author Hlingoes 2020/5/2
     */
    List<QuartzJob> listJob(@Param("jobName") String jobName) throws DataAccessException;

    /**
     * description: 根据jobName, jobGroup查询所有任务
     *
     * @param jobName
     * @param jobGroup
     * @return cn.henry.study.web.entity.QuartzJob
     * @throws DataAccessException
     * @author Hlingoes 2020/5/2
     */
    QuartzJob getJob(@Param("jobName") String jobName, @Param("jobGroup") String jobGroup) throws DataAccessException;

    /**
     * description: 单个任务保存
     *
     * @param job
     * @return int
     * @throws DataAccessException
     * @author Hlingoes 2020/5/2
     */
    int saveJob(QuartzJob job) throws DataAccessException;

    /**
     * description: 批量保存任务
     *
     * @param quartzJobs
     * @return int
     * @throws DataAccessException
     * @author Hlingoes 2020/5/2
     */
    int insertBatch(List<QuartzJob> quartzJobs) throws DataAccessException;

    /**
     * description: 批量更新任务
     *
     * @param jobName
     * @param jobGroup
     * @param status
     * @return int
     * @throws DataAccessException
     * @author Hlingoes 2020/5/2
     */
    int updateJobStatus(@Param("jobName") String jobName,
                        @Param("jobGroup") String jobGroup,
                        @Param("status") String status) throws DataAccessException;

    /**
     * description: 根据jobName，jobGroup删除任务
     *
     * @param jobName
     * @param jobGroup
     * @return int
     * @throws DataAccessException
     * @author Hlingoes 2020/5/2
     */
    int removeQuartzJob(@Param("jobName") String jobName,
                        @Param("jobGroup") String jobGroup) throws DataAccessException;

    /**
     * description: 更新单条记录
     *
     * @param quartz
     * @return int
     * @throws DataAccessException
     * @author Hlingoes 2020/5/2
     */
    int updateJob(QuartzJob quartz) throws DataAccessException;
}

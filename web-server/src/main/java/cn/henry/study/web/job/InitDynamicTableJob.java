package cn.henry.study.web.job;

import cn.henry.study.web.configuration.DataSourceConfig;
import cn.henry.study.web.database.DynamicDataSource;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.sql.SQLException;

/**
 * description: 支持多数据源的业务分表
 *
 * @author Hlingoes
 * @date 2020/5/15 23:02
 */
public class InitDynamicTableJob extends QuartzJobBean {

    @Autowired
    private DataSourceConfig dataSourceConfig;

    @Autowired
    private DynamicDataSource dynamicDataSource;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {

    }

    private void init() throws SQLException, ClassNotFoundException{
    }
}

package cn.henry.study.web.job;

import cn.henry.study.web.configuration.DataSourceConfig;
import cn.henry.study.web.database.DatabaseContextHolder;
import cn.henry.study.web.mapper.DynamicTableMapper;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * description: 支持多数据源的业务分表
 *
 * @author Hlingoes
 * @date 2020/5/15 23:02
 */
@DisallowConcurrentExecution
public class DynamicTableJob extends QuartzJobBean {
    private Logger logger = LoggerFactory.getLogger(DynamicTableJob.class);

    @Autowired
    private DataSourceConfig dataSourceConfig;

    @Autowired
    private DynamicTableMapper dynamicTableMapper;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        if (!checkShardingRule()) {
            return;
        }
        List<String> serveNames = (List<String>) jobExecutionContext.getJobDetail().getJobDataMap().get("serveNames");
        serveNames.forEach(serveName -> {
            String tableName = getShardingTableName(serveName);
            shardingTableByDataSource(tableName);
        });
    }

    /**
     * description: 兼容动态数据源，创建分表
     *
     * @param tableName
     * @return void
     * @author Hlingoes 2020/5/16
     */
    private void shardingTableByDataSource(String tableName) {
        // 兼容动态数据源
        this.dataSourceConfig.getMysqlClients().forEach(client -> {
            // 设定指定数据源
            DatabaseContextHolder.setRouteKey(client.getRouteKey());
            try {
                if (checkTableNotExists(tableName)) {
                    createShardingTable(tableName);
                }
            } catch (Exception e) {
                logger.error("创建分表失败, tableName={}", tableName, e);
            } finally {
                // 清除绑定的数据源
                DatabaseContextHolder.removeRouteKey();
            }
        });
    }

    /**
     * description: 分表规则每月的最后一天，生成第二个月的动态表
     *
     * @param
     * @return boolean
     * @author Hlingoes 2020/5/16
     */
    private boolean checkShardingRule() {
        LocalDate today = LocalDate.now();
        // 本月的第一天
        LocalDate firstDay = LocalDate.of(today.getYear(), today.getMonth(), 1);
        // 本月的最后一天
        LocalDate lastDay = today.with(TemporalAdjusters.lastDayOfMonth());
        logger.info("本月的第一天: {}, 本月的最后一天: {} ", firstDay, lastDay);
        // 判断是不是本月的最后一天
        return Objects.equals(today, lastDay);
    }

    /**
     * description: 动态表名，用业务表+下一个月的时间指定，eg: quartz_job_202005
     *
     * @param
     * @return java.lang.String
     * @author Hlingoes 2020/5/16
     */
    private String getShardingTableName(String serverName) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMM");
        LocalDate date = LocalDate.now();
        date = date.plusMonths(1);
        return serverName + "_" + df.format(date);
    }

    /**
     * description: 检查分表是否已经存在
     *
     * @param tableName
     * @return boolean
     * @author Hlingoes 2020/5/16
     */
    private boolean checkTableNotExists(String tableName) throws DataAccessException {
        Map<String, String> result = this.dynamicTableMapper.checkTableExistsWithShow(tableName);
        return CollectionUtils.isEmpty(result);
    }

    /**
     * description: 动态建新表
     *
     * @param tableName
     * @return void
     * @author Hlingoes 2020/5/16
     */
    private void createShardingTable(String tableName) throws DataAccessException {
        this.dynamicTableMapper.createDynamicTable(tableName);
    }
}

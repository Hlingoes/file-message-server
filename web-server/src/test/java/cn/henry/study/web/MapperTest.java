package cn.henry.study.web;

import cn.henry.study.common.utils.JacksonUtils;
import cn.henry.study.web.configuration.DataSourceConfig;
import cn.henry.study.web.database.DatabaseContextHolder;
import cn.henry.study.web.mapper.DynamicTableMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * description: 测试mybatis的crud操作
 *
 * @author Hlingoes
 * @date 2020/5/16 0:24
 */
@SpringBootTest(classes = WebMessageServer.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class MapperTest {
    private static Logger logger = LoggerFactory.getLogger(MapperTest.class);

    @Autowired
    private DataSourceConfig dataSourceConfig;

    @Autowired
    private DynamicTableMapper dynamicTableMapper;

    @Test
    public void testDynamicTable() {
        String tableName = getShardingTableName("quartz_job");
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

    @Test
    public void testCheckTableExists() {
        Map<String, String> result = this.dynamicTableMapper.checkTableExistsWithShow("quartz_job_202005");
        Map<String, String> result2 = this.dynamicTableMapper.checkTableExistsWithShow("quartz_job");
        logger.info("查询结果：result = {}，result2={}", JacksonUtils.object2Str(result), JacksonUtils.object2Str(result2));
    }
}

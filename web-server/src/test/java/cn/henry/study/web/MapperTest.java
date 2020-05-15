package cn.henry.study.web;

import cn.henry.study.web.mapper.DynamicTableMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * description: 测试mybatis的crud操作
 *
 * @author Hlingoes
 * @date 2020/5/16 0:24
 */
@SpringBootTest(classes = WebMessageServer.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class MapperTest {

    @Autowired
    private DynamicTableMapper dynamicTableMapper;

    @Test
    public void testCreateDynamicTable() {
        this.dynamicTableMapper.createDynamicTable("QuartzJob_202005");
    }
}

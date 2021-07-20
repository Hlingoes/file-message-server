package cn.henry.study.common;

import cn.henry.study.common.bo.PartitionElements;
import cn.henry.study.common.service.OperationThreadService;
import cn.henry.study.common.utils.MultiThreadOperationUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * description:
 *
 * @author Hlingoes
 * @date 2020/6/12 20:52
 */
public class MultiThreadServiceTest implements OperationThreadService {
    private static Logger logger = LoggerFactory.getLogger(MultiThreadServiceTest.class);

    @Override
    public long count(Object[] args) throws Exception {
        return 100L;
    }

    @Override
    public Object prepare(Object[] args) throws Exception {
        return "success";
    }

    @Override
    public Object invoke(PartitionElements elements) throws Exception {
        List<Object> list = new ArrayList<>((int) elements.getBatchCounts());
        for (int i = 0; i < elements.getIndex(); i++) {
            list.add("test_" + i);
        }
        return list;
    }

    @Override
    public void post(PartitionElements elements, Object object) throws Exception {
        String insertSql = "insert into test (id) values ";
        StringBuilder sb = new StringBuilder();
        List<Object> datas = (List<Object>) elements.getData();
        for (int i = 0; i < datas.size(); i++) {
            sb.append("('" + datas.get(i) + "')");
            if ((i + 1) % 5 == 0 || (i + 1) == datas.size()) {
                logger.info("{}: 测试insert sql: {}", elements, insertSql + sb.toString());
                sb = new StringBuilder();
            }
        }
    }

    @Override
    public Object finished(Object object) throws Exception {
        return object;
    }

    @Test
    public void testBatchExecute() {
        try {
            Object object = MultiThreadOperationUtils.batchExecute(new MultiThreadServiceTest(), 10, new Object[]{"test"});
            logger.info("测试完成: {}", object.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
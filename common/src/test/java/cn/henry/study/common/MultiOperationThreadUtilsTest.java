package cn.henry.study.common;

import cn.henry.study.common.bo.PartitionElements;
import cn.henry.study.common.service.OperationThreadService;
import cn.henry.study.common.utils.MultiOperationThreadUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * description: 多线程业务分治归并处理测试
 *
 * @author Hlingoes
 * @date 2020/5/23 0:23
 */
public class MultiOperationThreadUtilsTest {
    private static Logger logger = LoggerFactory.getLogger(MultiOperationThreadUtilsTest.class);

    class MulitiTestThreadService implements OperationThreadService {

        @Override
        public long count(Object[] args) {
            return 100L;
        }

        @Override
        public List<Object> find(PartitionElements elements) {
            List<Object> list = new ArrayList<>((int)elements.getRows());
            for (int i = 0; i < elements.getRows(); i++) {
                list.add("test_" + i);
            }
            return list;
        }

        @Override
        public void update(PartitionElements elements) {

        }

        @Override
        public void delete(PartitionElements elements) {

        }

        @Override
        public void prepare(PartitionElements elements) {
            String insertSql = "insert into test (id) values ";
            StringBuilder sb = new StringBuilder();
            List<Object> datas = elements.getDatas();
            for (int i = 0; i < datas.size(); i++) {
                if ((i + 1) % 5 == 0 || (i + 1) == datas.size()) {
                    sb.append("('" + datas.get(i) + "')");
                    logger.info("测试insert sql: {}", insertSql + sb.toString());
                    sb = new StringBuilder();
                } else {
                    sb.append("('" + datas.get(i) + "'),");
                }
            }
        }
    }

    @Test
    public void testBatchExecute() {
        MultiOperationThreadUtils.batchExecute(new MulitiTestThreadService(), 10, new Object[]{"test"});
    }
}

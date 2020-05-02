package cn.henry.study.web.listener;

import cn.henry.study.common.utils.JacksonUtils;
import cn.henry.study.web.entity.QuartzJob;
import cn.henry.study.web.service.quartz.QuartzJobService;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * description: 使用alibaba的easyexcel操作文件
 *
 * @author Hlingoes
 * @date 2019/12/21 18:25
 */
public class QuartzJobExcelListener extends AnalysisEventListener<QuartzJob> {
    private static Logger logger = LoggerFactory.getLogger(QuartzJobExcelListener.class);

    /**
     * 每隔5条存储数据库，实际使用中可以3000条，然后清理list ，方便内存回收
     */
    private static final int BATCH_COUNT = 5;

    List<QuartzJob> list = new ArrayList<>();
    /**
     * 假设这个是一个DAO，当然有业务逻辑这个也可以是一个service。当然如果不用存储这个对象没用。
     */
    private QuartzJobService jobService;

    /**
     * 如果使用了spring,请使用这个构造方法。每次创建Listener的时候需要把spring管理的类传进来
     *
     * @param jobService
     */
    public QuartzJobExcelListener(QuartzJobService jobService) {
        this.jobService = jobService;
    }

    /**
     * 这个每一条数据解析都会来调用
     *
     * @param data
     *            one row value. Is is same as {@link AnalysisContext#readRowHolder()}
     * @param context
     */
    @Override
    public void invoke(QuartzJob data, AnalysisContext context) {
        logger.info("解析到一条数据:{}", JacksonUtils.object2Str(data));
        list.add(data);
        // 达到BATCH_COUNT了，需要去存储一次数据库，防止数据几万条数据在内存，容易OOM
        if (list.size() >= BATCH_COUNT) {
            saveData();
            // 存储完成清理 list
            list.clear();
        }
    }

    /**
     * 所有数据解析完成了 都会来调用
     *
     * @param context
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 这里也要保存数据，确保最后遗留的数据也存储到数据库
        saveData();
        logger.info("所有数据解析完成！");
    }

    /**
     * 加上存储数据库
     */
    private void saveData() {
        logger.info("{}条数据，开始存储数据库！", list.size());
        jobService.insertBatch(list);
        logger.info("存储数据库成功！");
    }
}

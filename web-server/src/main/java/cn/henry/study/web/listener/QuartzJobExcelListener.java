package cn.henry.study.web.listener;

import cn.henry.study.common.utils.JacksonUtils;
import cn.henry.study.common.bo.ExcelImportDescription;
import cn.henry.study.web.entity.QuartzJob;
import cn.henry.study.web.service.quartz.QuartzJobService;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;

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

    /**
     * 导入导出的成功或失败信息
     */
    private ExcelImportDescription description = new ExcelImportDescription();

    private List<QuartzJob> list = new ArrayList<>();

    private List<Integer> flags = new ArrayList<>();
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
     * @param data    one row value. Is is same as {@link AnalysisContext#readRowHolder()}
     * @param context
     */
    @Override
    public void invoke(QuartzJob data, AnalysisContext context) {
        logger.info("解析到一条数据:{}", JacksonUtils.object2Str(data));
        list.add(data);
        flags.add(1);
        // 达到BATCH_COUNT了，需要去存储一次数据库，防止数据几万条数据在内存，容易OOM
//        if (list.size() >= BATCH_COUNT) {
//            saveData();
//            // 存储完成清理 list
//            list.clear();
//        }
        singleInsert(data);
    }

    /**
     * 所有数据解析完成了 都会来调用
     *
     * @param context
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 这里也要保存数据，确保最后遗留的数据也存储到数据库
//        saveData();
        list.clear();
        flags.clear();
        logger.info("所有数据解析完成！");
    }

    /**
     * 加上存储数据库
     */
    private void saveData() {
        logger.info("{}条数据，开始存储数据库！", list.size());
        try {
            this.jobService.insertBatch(list);
            logger.info("存储数据库成功！");
        } catch (DataAccessException e) {
            logger.error("存储数据库失败{}条，[{}]", list.size(), JacksonUtils.object2Str(list), e);
        }
    }

    /**
     * description: 单条写入，记录成功，失败和纪录重复
     *
     * @param data
     * @return void
     * @author Hlingoes 2020/5/3
     */
    private void singleInsert(QuartzJob data) {
        try {
            this.jobService.insertSingle(data);
            this.description.addSuccessMark(flags.size());
        } catch (DataAccessException e) {
            Throwable cause = e.getCause();
            if (cause instanceof MySQLIntegrityConstraintViolationException) {
                this.description.addRepeatMark(flags.size());
            } else {
                this.description.addFailMark(flags.size());
            }
        }
    }

    public ExcelImportDescription getDescription() {
        return description;
    }

}

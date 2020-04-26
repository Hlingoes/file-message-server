package cn.henry.study.web.listener;

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
public class EasyExcelListener extends AnalysisEventListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(EasyExcelListener.class);

    List<Object> list = new ArrayList<Object>();

    private static void accept(Object o) {
        LOGGER.info(o.toString());
    }

    @Override
    public void invoke(Object o, AnalysisContext analysisContext) {
        list.add(o);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        list.forEach(EasyExcelListener::accept);
    }
}

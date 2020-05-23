package cn.henry.study.common.thread;

import cn.henry.study.common.bo.PartitionElements;
import cn.henry.study.common.service.OperationThreadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

/**
 * description: 分段操作
 *
 * @author Hlingoes
 * @date 2020/5/22 0:13
 */
public class OperationThread implements Callable<PartitionElements> {
    private static Logger logger = LoggerFactory.getLogger(OperationThread.class);

    private PartitionElements elements;
    private OperationThreadService service;

    public OperationThread(PartitionElements elements, OperationThreadService service) {
        this.elements = elements;
        this.service = service;
    }

    @Override
    public PartitionElements call() {
        long startTime = System.currentTimeMillis();
        this.elements.setDatas(this.service.find(this.elements));
        long endTime = System.currentTimeMillis();
        logger.info("partition operation finished, cost: {}ms", (endTime - startTime));
        return this.elements;
    }

    public PartitionElements getElements() {
        return elements;
    }

    public void setElements(PartitionElements elements) {
        this.elements = elements;
    }

    public OperationThreadService getService() {
        return service;
    }

    public void setService(OperationThreadService service) {
        this.service = service;
    }

}

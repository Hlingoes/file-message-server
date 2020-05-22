package cn.henry.study.common.thread;

import cn.henry.study.common.bo.PartitionElements;
import cn.henry.study.common.service.OperationService;

import java.util.concurrent.Callable;

/**
 * description: 数据库的批量操作
 *
 * @author Hlingoes
 * @date 2020/5/22 0:13
 */
public class OperationThread implements Callable<PartitionElements> {
    private PartitionElements elements;
    private OperationService service;

    public OperationThread(PartitionElements elements, OperationService service) {
        this.elements = elements;
        this.service = service;
    }

    @Override
    public PartitionElements call() {
        int start = (this.elements.getPage() - 1) * this.elements.getRows();
        Object[] args = this.elements.getArgs();
        int len = args.length;
        Object[] newArgs = new Object[len + 2];
        System.arraycopy(args, 0, newArgs, 0, len);
        // 分段参数
        newArgs[len] = start;
        newArgs[len + 1] = this.elements.getRows();
        this.elements.setDatas(this.service.find(this.elements, newArgs));
        return this.elements;
    }

    public PartitionElements getElements() {
        return elements;
    }

    public void setElements(PartitionElements elements) {
        this.elements = elements;
    }

    public OperationService getService() {
        return service;
    }

    public void setService(OperationService service) {
        this.service = service;
    }

}

package cn.henry.study.common.thread;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * description: 数据库的批量操作
 *
 * @author Hlingoes
 * @date 2020/5/22 0:13
 */
public class DbOperationThread implements Callable<List<Object>> {

    private int page;

    private int count;

    private OperationService service;

    public DbOperationThread(int page, int count, OperationService service) {
        this.page = page;
        this.count = count;
        this.service = service;
    }

    @Override
    public List<Object> call() {
        int start = (this.page - 1) * this.count;
        Object[] args = new Object[]{start, count};
        return this.service.find(args);
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public OperationService getService() {
        return service;
    }

    public void setService(OperationService service) {
        this.service = service;
    }
}

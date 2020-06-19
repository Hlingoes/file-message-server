package com.xiaoju.uemc.tinyid.generator.impl;

import cn.henry.study.common.exceptions.TinyIdSysException;
import cn.henry.study.common.result.CommonResult;
import cn.henry.study.common.result.ResultCode;
import cn.henry.study.common.utils.ThreadPoolExecutorUtils;
import com.xiaoju.uemc.tinyid.entity.SegmentId;
import com.xiaoju.uemc.tinyid.generator.IdGenerator;
import com.xiaoju.uemc.tinyid.service.SegmentIdService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * @author du_imba
 */
public class CachedIdGenerator implements IdGenerator {
    protected String bizType;
    protected SegmentIdService segmentIdService;
    protected volatile SegmentId current;
    protected volatile SegmentId next;
    private volatile boolean isLoadingNext;
    private Object lock = new Object();
    private ExecutorService executorService = ThreadPoolExecutorUtils.getExecutorPool("tinyid-generator");

    public CachedIdGenerator(String bizType, SegmentIdService segmentIdService) {
        this.bizType = bizType;
        this.segmentIdService = segmentIdService;
        loadCurrent();
    }

    public synchronized void loadCurrent() {
        if (current == null || !current.useful()) {
            if (next == null) {
                SegmentId segmentId = querySegmentId();
                this.current = segmentId;
            } else {
                current = next;
                next = null;
            }
        }
    }

    private SegmentId querySegmentId() {
        String message = null;
        try {
            SegmentId segmentId = segmentIdService.getNextSegmentId(bizType);
            if (segmentId != null) {
                return segmentId;
            }
        } catch (Exception e) {
            message = e.getMessage();
        }
        throw new TinyIdSysException("error query segmentId: " + message);
    }

    public void loadNext() {
        if (next == null && !isLoadingNext) {
            synchronized (lock) {
                if (next == null && !isLoadingNext) {
                    isLoadingNext = true;
                    executorService.submit(new Runnable() {

                        @Override
                        public void run() {
                            try {
                                // 无论获取下个segmentId成功与否，都要将isLoadingNext赋值为false
                                next = querySegmentId();
                            } finally {
                                isLoadingNext = false;
                            }
                        }
                    });
                }
            }
        }
    }

    @Override
    public Long nextId() {
        while (true) {
            if (current == null) {
                loadCurrent();
                continue;
            }
            CommonResult result = current.nextId();
            if (result.getCode().equals(ResultCode.OVER)) {
                loadCurrent();
            } else {
                if (result.getCode().equals(ResultCode.LOADING)) {
                    loadNext();
                }
                return (Long) result.getData();
            }
        }
    }

    @Override
    public List<Long> nextId(Integer batchSize) {
        List<Long> ids = new ArrayList<>();
        for (int i = 0; i < batchSize; i++) {
            Long id = nextId();
            ids.add(id);
        }
        return ids;
    }

}

package com.xiaoju.uemc.tinyid;

import cn.henry.study.common.bo.SegmentId;
import cn.henry.study.common.generator.IdGenerator;
import cn.henry.study.common.result.CommonResult;
import cn.henry.study.common.result.ResultCode;
import cn.henry.study.common.service.SegmentIdService;
import com.xiaoju.uemc.tinyid.factory.impl.IdGeneratorFactoryServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * description:
 *
 * @author Hlingoes
 * @date 2020/6/7 11:36
 */
@Service
public class TinyIdServer {
    private static Logger logger = LoggerFactory.getLogger(TinyIdServer.class);

    @Autowired
    private IdGeneratorFactoryServer idGeneratorFactoryServer;
    @Autowired
    private SegmentIdService segmentIdService;
    @Value("${batch.size.max}")
    private Integer batchSizeMax;

    public Long nextId(String bizType) {
        if (bizType == null) {
            throw new IllegalArgumentException("type is null");
        }
        IdGenerator idGenerator = idGeneratorFactoryServer.getIdGenerator(bizType);
        return idGenerator.nextId();
    }

    public List<Long> nextId(String bizType, Integer batchSize) {
        if (batchSize == null) {
            Long id = nextId(bizType);
            List<Long> list = new ArrayList<>();
            list.add(id);
            return list;
        }
        IdGenerator idGenerator = idGeneratorFactoryServer.getIdGenerator(bizType);
        return idGenerator.nextId(batchSize);
    }

    public CommonResult nextId(String bizType, Integer batchSize, String token) {
        Integer newBatchSize = checkBatchSize(batchSize);
        CommonResult result = new CommonResult();
        try {
            IdGenerator idGenerator = idGeneratorFactoryServer.getIdGenerator(bizType);
            List<Long> ids = idGenerator.nextId(newBatchSize);
            result.setResultCode(ResultCode.SUCCESS);
            result.setData(ids);
        } catch (Exception e) {
            result.setResultCode(ResultCode.INTERFACE_INNER_INVOKE_ERROR);
            logger.error("nextId error", e);
        }
        return result;
    }

    private Integer checkBatchSize(Integer batchSize) {
        if (batchSize == null) {
            batchSize = 1;
        }
        if (batchSize > batchSizeMax) {
            batchSize = batchSizeMax;
        }
        return batchSize;
    }

    public String nextIdSimple(String bizType, Integer batchSize, String token) {
        Integer newBatchSize = checkBatchSize(batchSize);
        String result = "";
        try {
            IdGenerator idGenerator = idGeneratorFactoryServer.getIdGenerator(bizType);
            if (newBatchSize == 1) {
                Long id = idGenerator.nextId();
                result = id + "";
            } else {
                List<Long> idList = idGenerator.nextId(newBatchSize);
                StringBuilder sb = new StringBuilder();
                for (Long id : idList) {
                    sb.append(id).append(",");
                }
                result = sb.deleteCharAt(sb.length() - 1).toString();
            }
        } catch (Exception e) {
            logger.error("nextIdSimple error", e);
        }
        return result;
    }

    public CommonResult nextSegmentId(String bizType, String token) {
        CommonResult result = new CommonResult();
        try {
            SegmentId segmentId = segmentIdService.getNextSegmentId(bizType);
            result.setData(segmentId);
        } catch (Exception e) {
            result.setResultCode(ResultCode.INTERFACE_INNER_INVOKE_ERROR);
            logger.error("nextSegmentId error", e);
        }
        return result;
    }

    public String nextSegmentIdSimple(String bizType, String token) {
        String result = "";
        try {
            SegmentId segmentId = segmentIdService.getNextSegmentId(bizType);
            result = segmentId.getCurrentId() + "," + segmentId.getLoadingId() + "," + segmentId.getMaxId()
                    + "," + segmentId.getDelta() + "," + segmentId.getRemainder();
        } catch (Exception e) {
            logger.error("nextSegmentIdSimple error", e);
        }
        return result;
    }
}

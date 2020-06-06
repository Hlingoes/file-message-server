package cn.henry.study.common.service;

import cn.henry.study.common.bo.SegmentId;

/**
 * @author du_imba
 */
public interface SegmentIdService {

    /**
     * 根据bizType获取下一个SegmentId对象
     *
     * @param bizType
     * @return
     */
    SegmentId getNextSegmentId(String bizType);

}

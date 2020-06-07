package com.xiaoju.uemc.tinyid.factory.impl;

import cn.henry.study.common.factory.AbstractIdGeneratorFactory;
import cn.henry.study.common.generator.IdGenerator;
import cn.henry.study.common.generator.impl.CachedIdGenerator;
import cn.henry.study.common.service.SegmentIdService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author du_imba
 */
@Component
public class IdGeneratorFactoryServer extends AbstractIdGeneratorFactory {

    private static final Logger logger = LoggerFactory.getLogger(CachedIdGenerator.class);
    @Autowired
    private SegmentIdService tinyIdService;

    @Override
    public IdGenerator createIdGenerator(String bizType) {
        logger.info("createIdGenerator :{}", bizType);
        return new CachedIdGenerator(bizType, tinyIdService);
    }
}

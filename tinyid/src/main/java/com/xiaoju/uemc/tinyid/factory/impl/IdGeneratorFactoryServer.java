package com.xiaoju.uemc.tinyid.factory.impl;

import com.xiaoju.uemc.tinyid.factory.AbstractIdGeneratorFactory;
import com.xiaoju.uemc.tinyid.generator.IdGenerator;
import com.xiaoju.uemc.tinyid.generator.impl.CachedIdGenerator;
import com.xiaoju.uemc.tinyid.service.SegmentIdService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author du_imba
 */
@Component
public class IdGeneratorFactoryServer extends AbstractIdGeneratorFactory {
    private static Logger logger = LoggerFactory.getLogger(IdGeneratorFactoryServer.class);

    @Autowired
    private SegmentIdService tinyIdService;

    @Override
    public IdGenerator createIdGenerator(String bizType) {
        logger.info("createIdGenerator :{}", bizType);
        return new CachedIdGenerator(bizType, tinyIdService);
    }

}

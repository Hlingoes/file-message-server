package com.xiaoju.uemc.tinyid.factory;

import com.xiaoju.uemc.tinyid.generator.IdGenerator;

/**
 * @author du_imba
 */
public interface IdGeneratorFactory {
    /**
     * 根据bizType创建id生成器
     *
     * @param bizType
     * @return
     */
    IdGenerator getIdGenerator(String bizType);
}

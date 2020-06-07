package com.xiaoju.uemc.tinyid.service;

/**
 * @author du_imba
 */
public interface TinyIdTokenService {
    /**
     * 是否有权限
     * @param bizType
     * @param token
     * @return
     */
    boolean canVisit(String bizType, String token);
}

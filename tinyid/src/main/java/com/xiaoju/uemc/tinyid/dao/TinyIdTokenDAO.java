package com.xiaoju.uemc.tinyid.dao;

import com.xiaoju.uemc.tinyid.entity.TinyIdToken;

import java.util.List;

/**
 * @author du_imba
 */
public interface TinyIdTokenDAO {
    /**
     * 查询db中所有的token信息
     * @return
     */
    List<TinyIdToken> selectAll();
}

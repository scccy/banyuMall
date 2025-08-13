package com.origin.banyu.user.service;

import com.origin.banyu.common.entity.SysUser;
import java.util.List;

/**
 * 用户批量操作服务接口
 * 专注于用户批量操作功能
 * 
 * @author scccy
 * @since 2025-08-12
 */
public interface UserBatchService {
    
    /**
     * 批量删除用户（软删除）
     *
     * @param userIds 用户ID列表
     * @return 是否删除成功
     */
    boolean batchDeleteUsers(List<String> userIds);
    
    /**
     * 批量获取用户信息
     *
     * @param userIds 用户ID列表
     * @return 用户信息列表
     */
    List<SysUser> getBatchUserInfo(List<String> userIds);
}


package com.origin.banyu.wechatWork.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.origin.banyu.wechatWork.entity.WechatworkUser;
import com.origin.banyu.wechatWork.mapper.WechatworkUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 企业微信用户业务服务类
 * 专门负责业务逻辑和API接口，给控制器使用
 * 专注于业务层面的操作，调用WechatworkUserAdapterService进行数据操作
 * 
 * @author scccy
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class WechatworkUserService extends ServiceImpl<WechatworkUserMapper, WechatworkUser> {
    private final WechatworkUserAdapterService userAdapterService;

    /**
     * 同步企业微信用户信息（控制器专用）
     * 调用适配器服务进行实际的数据同步操作
     * 
     * @param depId 部门ID，为null时同步所有部门
     * @param fetchChild 是否递归获取子部门下面的成员：1-是，0-否
     * @return 同步的用户总数
     */
    public int syncWechatWorkUsers(Integer depId, Integer fetchChild) {
        log.info("控制器开始同步企业微信用户信息，部门ID: {}, 递归获取: {}", depId, fetchChild);
        
        try {
            // 调用适配器服务进行数据同步
            int totalCount = userAdapterService.syncWechatWorkUsers(depId, fetchChild);
            
            log.info("控制器企业微信用户信息同步完成，共处理 {} 条记录", totalCount);
            return totalCount;
            
        } catch (Exception e) {
            log.error("控制器同步企业微信用户信息失败", e);
            throw new RuntimeException("控制器同步企业微信用户信息失败: " + e.getMessage(), e);
        }
    }

    /**
     * 根据企业微信用户ID获取用户信息（控制器专用）
     * 
     * @param wechatworkUserId 企业微信用户ID
     * @return 用户信息
     */
    public WechatworkUser getUserByWechatworkUserId(String wechatworkUserId) {
        if (wechatworkUserId == null || wechatworkUserId.trim().isEmpty()) {
            log.warn("控制器查询用户信息失败：用户ID为空");
            return null;
        }
        
        try {
            log.info("控制器查询用户信息，用户ID: {}", wechatworkUserId);
            WechatworkUser user = this.getById(wechatworkUserId);
            
            if (user == null) {
                log.info("控制器查询用户信息：用户不存在，用户ID: {}", wechatworkUserId);
            } else {
                log.info("控制器查询用户信息成功，用户ID: {}, 用户名: {}", wechatworkUserId, user.getName());
            }
            
            return user;
        } catch (Exception e) {
            log.error("控制器根据用户ID查询用户信息失败，用户ID: {}", wechatworkUserId, e);
            throw new RuntimeException("控制器查询用户信息失败: " + e.getMessage(), e);
        }
    }
}

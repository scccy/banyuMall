package com.origin.banyu.wechatWork.service;

import com.alibaba.fastjson2.JSON;
import com.origin.banyu.common.dto.WechatWorkUserInfo;
import com.origin.banyu.wechatWork.adapter.WechatWorkApiAdapter;
import com.origin.banyu.wechatWork.entity.WechatWorkUser;
import com.origin.banyu.wechatWork.exception.WechatWorkServiceException;
import com.origin.banyu.wechatWork.mapper.WechatWorkUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 企业微信用户服务
 * 符合第三方架构特殊规则：使用适配器模式封装第三方API
 * 
 * @author scccy
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class WechatWorkUserService {
    
    private final WechatWorkUserMapper userMapper;
    private final AccessTokenService accessTokenService;
    private final WechatWorkApiAdapter wechatWorkApiAdapter;
    
    /**
     * 同步企业微信用户信息
     * 
     * @param departmentId 部门ID，为null时同步所有部门
     * @return 同步的用户数量
     * @throws WechatWorkServiceException 当同步失败时抛出
     */
    public int syncWechatWorkUsers(Integer departmentId) {
        try {
            String accessToken = accessTokenService.getAccessToken();
            int totalCount = 0;
            
            if (departmentId != null) {
                // 同步指定部门用户
                totalCount = syncDepartmentUsers(accessToken, departmentId);
            } else {
                // 同步所有部门用户
                List<Integer> departmentIds = wechatWorkApiAdapter.getDepartmentIds(accessToken);
                for (Integer deptId : departmentIds) {
                    try {
                        totalCount += syncDepartmentUsers(accessToken, deptId);
                    } catch (Exception e) {
                        log.error("同步部门 {} 用户失败", deptId, e);
                        // 继续处理其他部门，不中断整个流程
                    }
                }
            }
            
            log.info("企业微信用户同步完成，共同步 {} 个用户", totalCount);
            return totalCount;
            
        } catch (Exception e) {
            log.error("同步企业微信用户失败", e);
            throw new WechatWorkServiceException("WECHATWORK_SYNC_FAILED", 
                    "同步企业微信用户失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 同步部门用户
     * 
     * @param accessToken 访问令牌
     * @param departmentId 部门ID
     * @return 同步的用户数量
     * @throws WechatWorkServiceException 当同步失败时抛出
     */
    private int syncDepartmentUsers(String accessToken, Integer departmentId) {
        try {
            // 使用适配器获取部门用户列表
            List<WechatWorkUserInfo> userInfoList = wechatWorkApiAdapter.getDepartmentUsers(accessToken, departmentId);
            int count = 0;
            
            // 逐个保存用户信息
            for (WechatWorkUserInfo userInfo : userInfoList) {
                try {
                    saveOrUpdateWechatWorkUser(userInfo);
                    count++;
                } catch (Exception e) {
                    log.error("保存用户信息失败: userid={}", userInfo.getWechatworkUserId(), e);
                    // 继续处理其他用户，不中断整个流程
                }
            }
            
            log.info("同步部门 {} 用户完成，成功同步 {} 个用户", departmentId, count);
            return count;
            
        } catch (WechatWorkServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("同步部门 {} 用户失败", departmentId, e);
            throw new WechatWorkServiceException("WECHATWORK_DEPARTMENT_SYNC_FAILED", 
                    "同步部门用户失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 根据企业微信用户ID获取用户信息
     * 
     * @param wechatworkUserId 企业微信用户ID
     * @return 用户信息
     * @throws WechatWorkServiceException 当获取失败时抛出
     */
    public WechatWorkUser getUserByWechatworkUserId(String wechatworkUserId) {
        try {
            WechatWorkUser user = userMapper.selectByWechatworkUserId(wechatworkUserId);
            if (user == null) {
                throw new WechatWorkServiceException("WECHATWORK_USER_NOT_FOUND", 
                        "用户不存在: " + wechatworkUserId);
            }
            return user;
        } catch (WechatWorkServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取用户信息失败: wechatworkUserId={}", wechatworkUserId, e);
            throw new WechatWorkServiceException("WECHATWORK_USER_GET_FAILED", 
                    "获取用户信息失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 保存或更新企业微信用户信息
     * 
     * @param userInfo 用户信息
     * @throws WechatWorkServiceException 当保存失败时抛出
     */
    public void saveOrUpdateWechatWorkUser(WechatWorkUserInfo userInfo) {
        try {
            // 检查用户是否已存在
            WechatWorkUser existingUser = userMapper.selectByWechatworkUserId(userInfo.getWechatworkUserId());
            
            WechatWorkUser user = new WechatWorkUser();
            user.setWechatworkUserId(userInfo.getWechatworkUserId());
            user.setName(userInfo.getName());
            user.setMobile(userInfo.getMobile());
            user.setDepartment(userInfo.getDepartment() != null ? JSON.toJSONString(userInfo.getDepartment()) : null);
            user.setPosition(userInfo.getPosition());
            user.setGender(userInfo.getGender());
            user.setEmail(userInfo.getEmail());
            user.setAvatar(userInfo.getAvatar());
            user.setStatus(userInfo.getStatus());
            user.setEnable(userInfo.getEnable());
            user.setIsleader(userInfo.getIsleader());
            user.setUpdatedTime(LocalDateTime.now());
            
            if (existingUser == null) {
                // 新增用户
                user.setCreatedTime(LocalDateTime.now());
                userMapper.insert(user);
                log.info("新增企业微信用户: userid={}, name={}", userInfo.getWechatworkUserId(), userInfo.getName());
            } else {
                // 更新用户 - 使用MyBatis-Plus的内置方法
                user.setCreatedTime(existingUser.getCreatedTime());
                user.setCreatedBy(existingUser.getCreatedBy());
                userMapper.updateById(user);
                log.info("更新企业微信用户: userid={}, name={}", userInfo.getWechatworkUserId(), userInfo.getName());
            }
            
        } catch (Exception e) {
            log.error("保存企业微信用户信息失败: userid={}", userInfo.getWechatworkUserId(), e);
            throw new WechatWorkServiceException("WECHATWORK_USER_SAVE_FAILED", 
                    "保存企业微信用户信息失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 同步外部联系人
     * 
     * @param wechatworkUserId 企业微信用户ID
     * @return 同步的外部联系人数量
     * @throws WechatWorkServiceException 当同步失败时抛出
     */
    public int syncExternalContacts(String wechatworkUserId) {
        try {
            String accessToken = accessTokenService.getAccessToken();
            
            // 使用适配器获取外部联系人列表
            List<String> externalUserIds = wechatWorkApiAdapter.getExternalContacts(accessToken, wechatworkUserId);
            
            log.info("同步外部联系人完成: userid={}, count={}", wechatworkUserId, externalUserIds.size());
            return externalUserIds.size();
            
        } catch (WechatWorkServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("同步外部联系人失败: userid={}", wechatworkUserId, e);
            throw new WechatWorkServiceException("WECHATWORK_EXTERNAL_CONTACTS_SYNC_FAILED", 
                    "同步外部联系人失败: " + e.getMessage(), e);
        }
    }
} 
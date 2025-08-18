package com.origin.banyu.wechatWork.service;

import com.origin.banyu.wechatWork.adapter.WechatWorkUserApiAdapter;
import com.origin.banyu.wechatWork.dto.WechatWorkUserInfo;
import com.origin.banyu.wechatWork.entity.WechatworkUser;
import com.origin.banyu.wechatWork.mapper.WechatworkUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import com.origin.banyu.wechatWork.entity.WechatworkDepartment;

/**
 * 企业微信用户服务类
 * 负责用户信息的同步和查询业务逻辑
 * 
 * @author scccy
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class WechatworkUserService {
    
    private final WechatworkUserMapper userMapper;
    private final WechatWorkUserApiAdapter userApiAdapter;
    private final AccessTokenService accessTokenService;
    private final WechatworkDepartmentService departmentService;

    /**
     * 同步企业微信用户信息
     * 
     * @param depId 部门ID，为null时同步所有部门
     * @param fetchChild 是否递归获取子部门下面的成员：1-是，0-否
     * @return 同步的用户总数
     */
    @Transactional
    public int syncWechatWorkUsers(Integer depId, Integer fetchChild) {
        log.info("开始同步企业微信用户信息，部门ID: {}, 递归获取: {}", depId, fetchChild);
        
        try {
            // 获取访问令牌
            String accessToken = accessTokenService.getAccessToken();
            if (accessToken == null) {
                throw new RuntimeException("无法获取企业微信访问令牌");
            }
            
            int totalCount = 0;
            
            if (depId != null) {
                // 同步指定部门的用户
                totalCount = syncDepartmentUsers(accessToken, depId, fetchChild != null ? fetchChild : 0);
            } else {
                // 同步所有部门的用户
                totalCount = syncAllDepartmentUsers(accessToken, fetchChild != null ? fetchChild : 0);
            }
            
            log.info("企业微信用户信息同步完成，共处理 {} 条记录", totalCount);
            return totalCount;
            
        } catch (Exception e) {
            log.error("同步企业微信用户信息失败", e);
            throw new RuntimeException("同步企业微信用户信息失败: " + e.getMessage(), e);
        }
    }

    /**
     * 同步指定部门的用户信息
     * 
     * @param accessToken 访问令牌
     * @param depId 部门ID
     * @param fetchChild 是否递归获取子部门用户
     * @return 同步的用户数量
     */
    private int syncDepartmentUsers(String accessToken, Integer depId, Integer fetchChild) {
        log.info("同步部门 {} 的用户信息，递归获取: {}", depId, fetchChild);
        
        try {
            // 从企业微信API获取部门用户信息
            List<WechatWorkUserInfo> users = userApiAdapter.getDepartmentUsers(accessToken, depId, fetchChild);
            
            if (users != null && !users.isEmpty()) {
                // 批量保存用户信息
                return batchSaveUsers(users);
            }
            
            log.info("部门 {} 没有用户信息", depId);
            return 0;
            
        } catch (Exception e) {
            log.error("同步部门 {} 用户信息失败", depId, e);
            throw new RuntimeException("同步部门用户信息失败: " + e.getMessage(), e);
        }
    }

    /**
     * 同步所有部门的用户信息
     * 
     * @param accessToken 访问令牌
     * @param fetchChild 是否递归获取子部门用户
     * @return 同步的用户总数
     */
    private int syncAllDepartmentUsers(String accessToken, Integer fetchChild) {
        log.info("开始同步所有部门的用户信息，递归获取: {}", fetchChild);
        
        try {
            // 获取所有部门信息
            List<WechatworkDepartment> allDepartments = departmentService.getAllDepartments();
            
            int totalCount = 0;
            
            // 遍历每个部门，同步用户信息
            for (WechatworkDepartment dept : allDepartments) {
                try {
                    int count = syncDepartmentUsers(accessToken, dept.getDepId(), fetchChild);
                    totalCount += count;
                    log.info("部门 {} 用户同步完成，同步 {} 个用户", dept.getDepId(), count);
                } catch (Exception e) {
                    log.error("同步部门 {} 用户失败，继续处理其他部门", dept.getDepId(), e);
                    // 继续处理其他部门，不中断整个同步过程
                }
            }
            
            log.info("所有部门用户信息同步完成，共处理 {} 条记录", totalCount);
            return totalCount;
            
        } catch (Exception e) {
            log.error("同步所有部门用户信息失败", e);
            throw new RuntimeException("同步所有部门用户信息失败: " + e.getMessage(), e);
        }
    }

    /**
     * 批量保存用户信息
     * 
     * @param users 用户信息列表
     * @return 保存的用户数量
     */
    private int batchSaveUsers(List<WechatWorkUserInfo> users) {
        if (users == null || users.isEmpty()) {
            return 0;
        }
        
        log.info("开始批量保存 {} 个用户信息", users.size());
        
        try {
            // 转换为实体对象
            List<WechatworkUser> entityList = new java.util.ArrayList<>();
            for (WechatWorkUserInfo userInfo : users) {
                WechatworkUser entity = userInfo.toEntity();
                entityList.add(entity);
            }
            
            // 批量保存
            int savedCount = userMapper.batchInsert(entityList);
            log.info("批量保存用户信息完成，成功保存 {} 条记录", savedCount);
            
            return savedCount;
            
        } catch (Exception e) {
            log.error("批量保存用户信息失败", e);
            throw new RuntimeException("批量保存用户信息失败: " + e.getMessage(), e);
        }
    }

    /**
     * 根据企业微信用户ID获取用户信息
     * 
     * @param wechatworkUserId 企业微信用户ID
     * @return 用户信息
     */
    public WechatworkUser getUserByWechatworkUserId(String wechatworkUserId) {
        if (wechatworkUserId == null || wechatworkUserId.trim().isEmpty()) {
            return null;
        }
        
        try {
            return userMapper.selectByWechatworkUserId(wechatworkUserId);
        } catch (Exception e) {
            log.error("根据用户ID查询用户信息失败，用户ID: {}", wechatworkUserId, e);
            throw new RuntimeException("查询用户信息失败: " + e.getMessage(), e);
        }
    }

    /**
     * 根据部门ID获取用户列表
     * 
     * @param depId 部门ID
     * @return 用户列表
     */
    public List<WechatworkUser> getUsersByDepId(Integer depId) {
        if (depId == null) {
            return new java.util.ArrayList<>();
        }
        
        try {
            return userMapper.selectByDepId(depId);
        } catch (Exception e) {
            log.error("根据部门ID查询用户列表失败，部门ID: {}", depId, e);
            throw new RuntimeException("查询用户列表失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取所有用户信息
     * 
     * @return 用户列表
     */
    public List<WechatworkUser> getAllUsers() {
        try {
            return userMapper.selectAll();
        } catch (Exception e) {
            log.error("获取所有用户信息失败", e);
            throw new RuntimeException("获取所有用户信息失败: " + e.getMessage(), e);
        }
    }
}

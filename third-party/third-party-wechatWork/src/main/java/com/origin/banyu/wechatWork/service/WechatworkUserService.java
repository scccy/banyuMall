package com.origin.banyu.wechatWork.service;

import com.origin.banyu.wechatWork.entity.WechatworkUser;
import com.origin.banyu.wechatWork.mapper.WechatworkUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

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
public class WechatworkUserService {
    
    private final WechatworkUserMapper userMapper;
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
            WechatworkUser user = userMapper.selectByWechatworkUserId(wechatworkUserId);
            
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

    /**
     * 根据部门ID获取用户列表（控制器专用）
     * 
     * @param depId 部门ID
     * @return 用户列表
     */
    public List<WechatworkUser> getUsersByDepId(Integer depId) {
        if (depId == null) {
            log.warn("控制器查询部门用户列表失败：部门ID为空");
            return new java.util.ArrayList<>();
        }
        
        try {
            log.info("控制器查询部门用户列表，部门ID: {}", depId);
            List<WechatworkUser> users = userMapper.selectByDepId(depId);
            log.info("控制器查询部门用户列表成功，部门ID: {}, 用户数量: {}", depId, users.size());
            return users;
        } catch (Exception e) {
            log.error("控制器根据部门ID查询用户列表失败，部门ID: {}", depId, e);
            throw new RuntimeException("控制器查询用户列表失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取所有用户信息（控制器专用）
     * 
     * @return 用户列表
     */
    public List<WechatworkUser> getAllUsers() {
        try {
            log.info("控制器开始获取所有用户信息");
            List<WechatworkUser> users = userMapper.selectAll();
            log.info("控制器获取所有用户信息成功，用户数量: {}", users.size());
            return users;
        } catch (Exception e) {
            log.error("控制器获取所有用户信息失败", e);
            throw new RuntimeException("控制器获取所有用户信息失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取用户统计信息（控制器专用）
     * 
     * @return 用户统计信息
     */
    public Object getUserStatistics() {
        try {
            log.info("控制器开始获取用户统计信息");
            
            // 获取所有用户
            List<WechatworkUser> allUsers = userMapper.selectAll();
            
            // 统计启用和禁用的用户数量
            long enabledUsers = allUsers.stream().filter(user -> user.getEnable() != null && user.getEnable() == 1).count();
            long disabledUsers = allUsers.stream().filter(user -> user.getEnable() != null && user.getEnable() == 0).count();
            
            // 构建统计信息
            var statistics = new Object() {
                public final long totalUsers = allUsers.size();
                public final long enabledUsers = enabledUsers;
                public final long disabledUsers = disabledUsers;
                public final String lastUpdateTime = java.time.LocalDateTime.now().toString();
            };
            
            log.info("控制器获取用户统计信息成功，总用户数: {}, 启用用户: {}, 禁用用户: {}", 
                    statistics.totalUsers, statistics.enabledUsers, statistics.disabledUsers);
            
            return statistics;
            
        } catch (Exception e) {
            log.error("控制器获取用户统计信息失败", e);
            throw new RuntimeException("控制器获取用户统计信息失败: " + e.getMessage(), e);
        }
    }

    /**
     * 搜索用户信息（控制器专用）
     * 
     * @param keyword 搜索关键词（用户名、邮箱等）
     * @return 匹配的用户列表
     */
    public List<WechatworkUser> searchUsers(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            log.warn("控制器搜索用户失败：搜索关键词为空");
            return new java.util.ArrayList<>();
        }
        
        try {
            log.info("控制器开始搜索用户，关键词: {}", keyword);
            
            // 获取所有用户，然后进行本地搜索
            List<WechatworkUser> allUsers = userMapper.selectAll();
            
            // 根据关键词过滤用户
            List<WechatworkUser> matchedUsers = allUsers.stream()
                    .filter(user -> 
                        (user.getName() != null && user.getName().contains(keyword)) ||
                        (user.getEmail() != null && user.getEmail().contains(keyword)) ||
                        (user.getMobile() != null && user.getMobile().contains(keyword)) ||
                        (user.getWechatworkUserId() != null && user.getWechatworkUserId().contains(keyword))
                    )
                    .collect(java.util.stream.Collectors.toList());
            
            log.info("控制器搜索用户完成，关键词: {}, 匹配用户数: {}", keyword, matchedUsers.size());
            return matchedUsers;
            
        } catch (Exception e) {
            log.error("控制器搜索用户失败，关键词: {}", keyword, e);
            throw new RuntimeException("控制器搜索用户失败: " + e.getMessage(), e);
        }
    }
}

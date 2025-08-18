package com.origin.banyu.wechatWork.controller;

import com.origin.banyu.common.dto.ResultData;
import com.origin.banyu.wechatWork.entity.WechatworkUser;
import com.origin.banyu.wechatWork.service.WechatworkUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 企业微信用户管理控制器
 * 提供用户同步和查询功能
 * 
 * @author scccy
 */
@RestController
@RequestMapping("/tp/wechatWork/user")
@Slf4j
@RequiredArgsConstructor
public class WechatWorkUserController {
    
    private final WechatworkUserService userService;

    /**
     * 同步企业微信用户信息
     * 从企业微信API获取最新用户信息并更新数据库
     * 
     * @param depId 部门ID（可选）
     * @param fetchChild 是否递归获取子部门用户（可选，默认0）
     * @return 同步结果
     */
    @PostMapping("/sync")
    public ResultData<String> syncWechatWorkUsers(
            @RequestParam(value = "depId", required = false) Integer depId,
            @RequestParam(value = "fetchChild", required = false, defaultValue = "0") Integer fetchChild) {
        
        log.info("开始同步企业微信用户信息，部门ID: {}, 递归获取: {}", depId, fetchChild);
        
        try {
            int totalCount = userService.syncWechatWorkUsers(depId, fetchChild);
            String message = String.format("用户信息同步完成，共处理 %d 条记录", totalCount);
            log.info(message);
            return ResultData.success(message);
            
        } catch (Exception e) {
            log.error("同步企业微信用户信息失败", e);
            return ResultData.error("同步失败: " + e.getMessage());
        }
    }

    /**
     * 根据用户ID获取用户信息
     * 从MySQL数据库查询用户详细信息
     * 
     * @param wechatworkUserId 企业微信用户ID
     * @return 用户信息
     */
    @GetMapping("/{wechatworkUserId}")
    public ResultData<Object> getUserById(@PathVariable("wechatworkUserId") String wechatworkUserId) {
        
        log.info("查询用户信息，用户ID: {}", wechatworkUserId);
        
        try {
            WechatworkUser user = userService.getUserByWechatworkUserId(wechatworkUserId);
            if (user != null) {
                return ResultData.success(user);
            } else {
                return ResultData.error("用户不存在");
            }
            
        } catch (Exception e) {
            log.error("查询用户信息失败，用户ID: {}", wechatworkUserId, e);
            return ResultData.error("查询失败: " + e.getMessage());
        }
    }
}

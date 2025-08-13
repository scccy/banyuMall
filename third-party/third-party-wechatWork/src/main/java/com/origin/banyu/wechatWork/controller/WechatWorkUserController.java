package com.origin.banyu.wechatWork.controller;
import com.origin.banyu.common.dto.ResultData;
import com.origin.banyu.common.entity.ErrorCode;
import com.origin.banyu.wechatWork.service.WechatWorkUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 企业微信用户管理控制器
 * 
 * @author scccy
 */
@RestController
@RequestMapping("/tp/wechatWork/user")
@Slf4j
@RequiredArgsConstructor
public class WechatWorkUserController {
    
    private final WechatWorkUserService userService;
    
    /**
     * 同步企业微信用户信息
     */
    @PostMapping("/sync")
    public ResultData<String> syncWechatWorkUsers(@RequestParam(value = "departmentId", required = false) Integer departmentId) {
        try {
            int count = userService.syncWechatWorkUsers(departmentId);
            return ResultData.success("同步成功，共同步 " + count + " 个用户");
        } catch (Exception e) {
            log.error("同步企业微信用户失败", e);
            return ResultData.fail(ErrorCode.WECHATWORK_SERVICE_ERROR, "同步失败");
        }
    }
    
    /**
     * 获取用户详情
     */
    @GetMapping("/{wechatworkUserId}")
    public ResultData<Object> getUserInfo(@PathVariable("wechatworkUserId") String wechatworkUserId) {
        try {
            Object user = userService.getUserByWechatworkUserId(wechatworkUserId);
            return ResultData.success(user);
        } catch (Exception e) {
            log.error("获取用户信息失败", e);
            return ResultData.fail(ErrorCode.WECHATWORK_SERVICE_ERROR, "获取用户信息失败");
        }
    }
} 
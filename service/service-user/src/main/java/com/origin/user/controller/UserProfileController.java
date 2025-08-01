package com.origin.user.controller;

import com.origin.common.dto.ResultData;
import com.origin.user.dto.UserUpdateRequest;
import com.origin.user.entity.UserProfile;
import com.origin.user.service.UserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 用户扩展信息管理控制器
 * 
 * @author scccy
 * @since 2024-07-30
 */
@Tag(name = "用户扩展信息", description = "用户扩展信息管理接口")
@RestController
@RequestMapping("/service/user/profile")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserProfileController {

    private final UserProfileService userProfileService;

    /**
     * 获取用户扩展信息
     *
     * @param userId 用户ID
     * @param httpRequest HTTP请求
     * @return 用户扩展信息
     */
    @Operation(summary = "获取用户扩展信息", description = "获取用户的详细资料和公司信息")
    @GetMapping("/{userId}")
    public ResultData<UserProfile> getUserProfile(@Parameter(description = "用户ID") @PathVariable String userId,
                                                 HttpServletRequest httpRequest) {
        // 从请求头中获取链路追踪信息
        String requestId = httpRequest.getHeader("X-Request-ID");
        String clientIp = httpRequest.getHeader("X-Client-IP");
        String userAgent = httpRequest.getHeader("X-User-Agent");
        
        log.info("获取用户扩展信息 - RequestId: {}, ClientIP: {}, UserAgent: {}, UserId: {}", 
                requestId, clientIp, userAgent, userId);
        
        UserProfile profile = userProfileService.getProfileByUserId(userId);
        if (profile == null) {
            return ResultData.fail("用户扩展信息不存在");
        }
        
        return ResultData.ok("获取用户扩展信息成功", profile);
    }

    /**
     * 创建或更新用户扩展信息
     *
     * @param userId 用户ID
     * @param request 更新请求
     * @param httpRequest HTTP请求
     * @return 操作结果
     */
    @Operation(summary = "创建或更新用户扩展信息", description = "创建或更新用户的扩展信息（真实姓名、公司信息等）")
    @PostMapping("/{userId}")
    public ResultData<UserProfile> createOrUpdateProfile(@Parameter(description = "用户ID") @PathVariable String userId,
                                                        @RequestBody @Valid UserUpdateRequest request,
                                                        HttpServletRequest httpRequest) {
        // 从请求头中获取链路追踪信息
        String requestId = httpRequest.getHeader("X-Request-ID");
        String clientIp = httpRequest.getHeader("X-Client-IP");
        String userAgent = httpRequest.getHeader("X-User-Agent");
        
        log.info("创建或更新用户扩展信息 - RequestId: {}, ClientIP: {}, UserAgent: {}, UserId: {}", 
                requestId, clientIp, userAgent, userId);
        
        UserProfile profile = userProfileService.createOrUpdateProfile(userId, request);
        return ResultData.ok("用户扩展信息保存成功", profile);
    }

    /**
     * 删除用户扩展信息
     *
     * @param userId 用户ID
     * @param httpRequest HTTP请求
     * @return 删除结果
     */
    @Operation(summary = "删除用户扩展信息", description = "删除指定用户的扩展信息")
    @DeleteMapping("/{userId}")
    public ResultData<String> deleteProfile(@Parameter(description = "用户ID") @PathVariable String userId,
                                           HttpServletRequest httpRequest) {
        // 从请求头中获取链路追踪信息
        String requestId = httpRequest.getHeader("X-Request-ID");
        String clientIp = httpRequest.getHeader("X-Client-IP");
        String userAgent = httpRequest.getHeader("X-User-Agent");
        
        log.info("删除用户扩展信息 - RequestId: {}, ClientIP: {}, UserAgent: {}, UserId: {}", 
                requestId, clientIp, userAgent, userId);
        
        boolean success = userProfileService.deleteProfileByUserId(userId);
        if (success) {
            return ResultData.ok("用户扩展信息删除成功");
        } else {
            return ResultData.fail("用户扩展信息删除失败");
        }
    }
} 
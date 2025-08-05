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
 * 基于简化的权限控制，通过profile_id进行关联
 * 
 * @author scccy
 * @since 2025-07-31
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
     * @param profileId 扩展信息ID
     * @param httpRequest HTTP请求
     * @return 用户扩展信息
     */
    @Operation(summary = "获取用户扩展信息", description = "根据扩展信息ID获取用户的详细资料和公司信息")
    @GetMapping("/{profileId}")
    public ResultData<UserProfile> getUserProfile(@Parameter(description = "扩展信息ID") @PathVariable String profileId,
                                                 HttpServletRequest httpRequest) {
        // 从请求头中获取链路追踪信息
        String requestId = httpRequest.getHeader("X-Request-ID");
        String clientIp = httpRequest.getHeader("X-Client-IP");
        String userAgent = httpRequest.getHeader("X-User-Agent");
        
        log.info("获取用户扩展信息 - RequestId: {}, ClientIP: {}, UserAgent: {}, ProfileId: {}", 
                requestId, clientIp, userAgent, profileId);
        
        UserProfile profile = userProfileService.getProfileByProfileId(profileId);
        if (profile == null) {
            return ResultData.fail("用户扩展信息不存在");
        }
        
        return ResultData.ok("获取用户扩展信息成功", profile);
    }

    /**
     * 创建用户扩展信息
     *
     * @param request 创建请求
     * @param httpRequest HTTP请求
     * @return 操作结果
     */
    @Operation(summary = "创建用户扩展信息", description = "创建用户的扩展信息（真实姓名、公司信息等）")
    @PostMapping
    public ResultData<UserProfile> createProfile(@RequestBody @Valid UserUpdateRequest request,
                                                HttpServletRequest httpRequest) {
        // 从请求头中获取链路追踪信息
        String requestId = httpRequest.getHeader("X-Request-ID");
        String clientIp = httpRequest.getHeader("X-Client-IP");
        String userAgent = httpRequest.getHeader("X-User-Agent");
        
        log.info("创建用户扩展信息 - RequestId: {}, ClientIP: {}, UserAgent: {}", 
                requestId, clientIp, userAgent);
        
        UserProfile profile = userProfileService.createProfile(request);
        return ResultData.ok("用户扩展信息创建成功", profile);
    }

    /**
     * 更新用户扩展信息
     *
     * @param profileId 扩展信息ID
     * @param request 更新请求
     * @param httpRequest HTTP请求
     * @return 操作结果
     */
    @Operation(summary = "更新用户扩展信息", description = "更新用户的扩展信息（真实姓名、公司信息等）")
    @PutMapping("/{profileId}")
    public ResultData<UserProfile> updateProfile(@Parameter(description = "扩展信息ID") @PathVariable String profileId,
                                                @RequestBody @Valid UserUpdateRequest request,
                                                HttpServletRequest httpRequest) {
        // 从请求头中获取链路追踪信息
        String requestId = httpRequest.getHeader("X-Request-ID");
        String clientIp = httpRequest.getHeader("X-Client-IP");
        String userAgent = httpRequest.getHeader("X-User-Agent");
        
        log.info("更新用户扩展信息 - RequestId: {}, ClientIP: {}, UserAgent: {}, ProfileId: {}", 
                requestId, clientIp, userAgent, profileId);
        
        UserProfile profile = userProfileService.updateProfile(profileId, request);
        return ResultData.ok("用户扩展信息更新成功", profile);
    }

    /**
     * 删除用户扩展信息
     *
     * @param profileId 扩展信息ID
     * @param httpRequest HTTP请求
     * @return 删除结果
     */
    @Operation(summary = "删除用户扩展信息", description = "删除指定用户的扩展信息")
    @DeleteMapping("/{profileId}")
    public ResultData<String> deleteProfile(@Parameter(description = "扩展信息ID") @PathVariable String profileId,
                                           HttpServletRequest httpRequest) {
        // 从请求头中获取链路追踪信息
        String requestId = httpRequest.getHeader("X-Request-ID");
        String clientIp = httpRequest.getHeader("X-Client-IP");
        String userAgent = httpRequest.getHeader("X-User-Agent");
        
        log.info("删除用户扩展信息 - RequestId: {}, ClientIP: {}, UserAgent: {}, ProfileId: {}", 
                requestId, clientIp, userAgent, profileId);
        
        boolean result = userProfileService.deleteProfile(profileId);
        if (result) {
            return ResultData.ok("用户扩展信息删除成功");
        } else {
            return ResultData.fail("用户扩展信息删除失败");
        }
    }
} 
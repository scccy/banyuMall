package com.origin.banyu.user.controller;

import com.origin.banyu.common.dto.ResultData;
import com.origin.banyu.user.dto.AvatarResponse;
import com.origin.banyu.user.service.UserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
/**
 * 用户档案管理控制器
 * 负责用户档案相关功能，包括头像、个人信息等
 * 
 * @author scccy
 * @since 2025-08-12
 */
@Tag(name = "用户档案管理", description = "用户档案相关功能接口")
@Slf4j
@RestController
@RequestMapping("/service/user/profiles")
@RequiredArgsConstructor
@Validated
public class UserProfileController {

    private final UserProfileService userProfileService;

    /**
     * 获取用户头像信息
     *
     * @param userId      用户ID
     * @param httpRequest HTTP请求
     * @return 头像信息
     */
    @Operation(summary = "获取用户头像信息", description = "获取用户的头像URL和相关信息")
    @GetMapping("/{userId}/avatar")
    public ResultData<AvatarResponse> getAvatarInfo(
            @Parameter(description = "用户ID") @PathVariable String userId,
            HttpServletRequest httpRequest) {
        // 从请求头中获取链路追踪信息
        String requestId = httpRequest.getHeader("X-Request-ID");
        String clientIp = httpRequest.getHeader("X-Client-IP");
        String userAgent = httpRequest.getHeader("X-User-Agent");

        log.info("获取用户头像信息 - RequestId: {}, ClientIP: {}, UserAgent: {}, UserId: {}",
                requestId, clientIp, userAgent, userId);
            AvatarResponse response = userProfileService.getAvatarInfo(userId);
            return ResultData.success("获取头像信息成功", response);

    }
} 
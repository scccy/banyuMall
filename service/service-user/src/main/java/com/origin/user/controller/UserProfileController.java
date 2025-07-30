package com.origin.user.controller;

import com.origin.common.ResultData;
import com.origin.user.dto.UserUpdateRequest;
import com.origin.user.entity.UserProfile;
import com.origin.user.service.UserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

//import javax.servlet.http.HttpServletRequest;
//import javax.validation.Valid;

/**
 * 用户扩展信息控制器
 *
 * @author origin
 * @since 2025-01-27
 */
@Tag(name = "用户扩展信息管理", description = "用户扩展信息相关接口")
@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    @Operation(summary = "获取用户扩展信息")
    @GetMapping("/{userId}")
    public ResultData<UserProfile> getUserProfile(
            @PathVariable String userId,
            HttpServletRequest request) {
        
        // 从请求头获取认证token
        String token = request.getHeader("Authorization");
        
        UserProfile profile = userProfileService.getByUserId(userId);
        if (profile == null) {
            return ResultData.fail("用户扩展信息不存在");
        }
        return ResultData.ok("获取成功", profile);
    }

    @Operation(summary = "创建或更新用户扩展信息")
    @PostMapping("/{userId}")
    public ResultData<Boolean> createOrUpdateProfile(
            @PathVariable String userId,
            @Valid @RequestBody UserUpdateRequest request,
            HttpServletRequest httpRequest) {
        
        // 从请求头获取认证token
        String token = httpRequest.getHeader("Authorization");
        
        UserProfile profile = new UserProfile();
        profile.setRealName(request.getRealName());
        profile.setCompanyName(request.getCompanyName());
        profile.setCompanyAddress(request.getCompanyAddress());
        profile.setContactPerson(request.getContactPerson());
        profile.setContactPhone(request.getContactPhone());
        profile.setIndustry(request.getIndustry());
        profile.setDescription(request.getDescription());
        
        boolean success = userProfileService.createOrUpdateProfile(userId, profile);
        return success ? ResultData.ok("操作成功", true) : ResultData.fail("操作失败");
    }
} 
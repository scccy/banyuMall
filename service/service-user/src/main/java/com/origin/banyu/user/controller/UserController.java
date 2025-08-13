package com.origin.banyu.user.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.origin.banyu.common.dto.ResultData;
import com.origin.banyu.common.entity.ErrorCode;
import com.origin.banyu.common.entity.SysUser;
import com.origin.banyu.user.dto.UserCreateRequest;
import com.origin.banyu.user.dto.UserQueryRequest;
import com.origin.banyu.user.dto.UserUpdateRequest;
import com.origin.banyu.user.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
/**
 * 用户基础管理控制器
 * 负责用户的创建、查询、更新、删除等基础CRUD操作
 * 
 * @author scccy
 * @since 2024-07-30
 */
@Tag(name = "用户基础管理", description = "用户基础CRUD操作接口")
@RestController
@RequestMapping("/service/user")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

    private final SysUserService sysUserService;

    /**
     * 创建用户
     *
     * @param request     创建请求
     * @param avatarFile  头像文件（可选）
     * @param httpRequest HTTP请求
     * @return 创建结果
     */
    @Operation(summary = "创建用户", description = "创建新用户，支持管理员和发布者两种用户类型，可选择上传头像")
    @PostMapping
    public ResultData<SysUser> createUser(
            @RequestPart("userInfo") @Valid UserCreateRequest request,
            @RequestPart(value = "avatarFile", required = false) MultipartFile avatarFile,
            HttpServletRequest httpRequest) {
        // 从请求头中获取链路追踪信息
        String requestId = httpRequest.getHeader("X-Request-ID");
        String clientIp = httpRequest.getHeader("X-Client-IP");
        String userAgent = httpRequest.getHeader("X-User-Agent");

        log.info("创建用户 - RequestId: {}, ClientIP: {}, UserAgent: {}, Username: {}, 是否有头像: {}",
                requestId, clientIp, userAgent, request.getUsername(), avatarFile != null);

        SysUser user = sysUserService.createUserWithAvatar(request, avatarFile);
        return ResultData.success("用户创建成功", user);
    }

    /**
     * 获取用户信息
     *
     * @param userId      用户ID
     * @param httpRequest HTTP请求
     * @return 用户信息
     */
    @Operation(summary = "获取用户信息", description = "根据用户ID获取用户详细信息")
    @GetMapping("/{userId}")
    public ResultData<SysUser> getUserInfo(@Parameter(description = "用户ID") @PathVariable String userId,
                                           HttpServletRequest httpRequest) {
        // 从请求头中获取链路追踪信息
        String requestId = httpRequest.getHeader("X-Request-ID");
        String clientIp = httpRequest.getHeader("X-Client-IP");
        String userAgent = httpRequest.getHeader("X-User-Agent");

        log.info("获取用户信息 - RequestId: {}, ClientIP: {}, UserAgent: {}, UserId: {}",
                requestId, clientIp, userAgent, userId);

        SysUser user = sysUserService.getUserById(userId);
        if (user == null) {
            log.warn("用户不存在 - 用户ID: {}", userId);
            return ResultData.fail(ErrorCode.USER_NOT_FOUND, "用户不存在");
        }

        return ResultData.success("获取用户信息成功", user);
    }

    /**
     * 更新用户信息
     *
     * @param userId      用户ID
     * @param request     更新请求
     * @param avatarFile  头像文件（可选）
     * @param httpRequest HTTP请求
     * @return 更新结果
     */
    @Operation(summary = "更新用户信息", description = "更新用户的基础信息（昵称、头像、邮箱等），可选择上传头像")
    @PostMapping("/{userId}")
    public ResultData<SysUser> updateUser(
            @Parameter(description = "用户ID") @PathVariable String userId,
            @RequestPart("userInfo") @Valid UserUpdateRequest request,
            @RequestPart(value = "avatarFile", required = false) org.springframework.web.multipart.MultipartFile avatarFile,
            HttpServletRequest httpRequest) {
        // 从请求头中获取链路追踪信息
        String requestId = httpRequest.getHeader("X-Request-ID");
        String clientIp = httpRequest.getHeader("X-Client-IP");
        String userAgent = httpRequest.getHeader("X-User-Agent");

        log.info("更新用户信息 - RequestId: {}, ClientIP: {}, UserAgent: {}, UserId: {}, 是否有头像: {}",
                requestId, clientIp, userAgent, userId, avatarFile != null);

        SysUser user = sysUserService.updateUserWithAvatar(userId, request, avatarFile);
        return ResultData.success("用户信息更新成功", user);
    }

    /**
     * 删除用户
     *
     * @param userId      用户ID
     * @param httpRequest HTTP请求
     * @return 删除结果
     */
    @Operation(summary = "删除用户", description = "软删除指定用户")
    @PostMapping("/{userId}/delete")
    public ResultData<String> deleteUser(@Parameter(description = "用户ID") @PathVariable String userId,
                                         HttpServletRequest httpRequest) {
        // 从请求头中获取链路追踪信息
        String requestId = httpRequest.getHeader("X-Request-ID");
        String clientIp = httpRequest.getHeader("X-Client-IP");
        String userAgent = httpRequest.getHeader("X-User-Agent");

        log.info("删除用户 - RequestId: {}, ClientIP: {}, UserAgent: {}, UserId: {}",
                requestId, clientIp, userAgent, userId);

        boolean success = sysUserService.deleteUser(userId);
        if (success) {
            return ResultData.success("用户删除成功");
        } else {
            return ResultData.fail(ErrorCode.USER_UPDATE_FAILED, "用户删除失败");
        }
    }

    /**
     * 用户列表查询
     *
     * @param request     查询请求
     * @param httpRequest HTTP请求
     * @return 分页结果
     */
    @Operation(summary = "用户列表查询", description = "分页查询用户列表，支持多条件筛选")
    @GetMapping("/list")
    public ResultData<IPage<SysUser>> getUserList(@Valid UserQueryRequest request,
                                                  HttpServletRequest httpRequest) {
        // 从请求头中获取链路追踪信息
        String requestId = httpRequest.getHeader("X-Request-ID");
        String clientIp = httpRequest.getHeader("X-Client-IP");
        String userAgent = httpRequest.getHeader("X-User-Agent");

        log.info("用户列表查询 - RequestId: {}, ClientIP: {}, UserAgent: {}, 查询条件: {}",
                requestId, clientIp, userAgent, request);

        IPage<SysUser> page = sysUserService.getUserPage(request);
        return ResultData.success("查询成功", page);
    }

}
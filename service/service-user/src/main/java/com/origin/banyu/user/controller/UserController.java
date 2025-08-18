package com.origin.banyu.user.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.origin.banyu.common.dto.ResultData;
import com.origin.banyu.common.entity.ErrorCode;
import com.origin.banyu.common.entity.SysUser;
import com.origin.banyu.user.dto.UserQueryRequest;
import com.origin.banyu.user.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
/**
 * 用户基础管理控制器
 * 负责用户的创建、查询、更新、删除等基础CRUD操作
 * 
 * @author scccy
 * @since 2024-07-30
 */
@Slf4j
@RestController
@RequestMapping("/service/user")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final SysUserService sysUserService;

    /**
     * 创建用户
     *
     * @param request     创建请求
     * @param httpRequest HTTP请求
     * @return 创建结果
     */
    @Operation(summary = "创建用户", description = "创建新用户，支持管理员和发布者两种用户类型，可选择上传头像")
    @PostMapping("/create")
    public ResultData<SysUser> createUser(
            @RequestBody    SysUser request,
            HttpServletRequest httpRequest) {
        // 从请求头中获取链路追踪信息
        String requestId = httpRequest.getHeader("X-Request-ID");
        String clientIp = httpRequest.getHeader("X-Client-IP");
        String userAgent = httpRequest.getHeader("X-User-Agent");



        SysUser user = sysUserService.createUser(request );
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
     * @param request     更新请求
     * @param httpRequest HTTP请求
     * @return 更新结果
     */
    @Operation(summary = "更新用户信息", description = "")
    @PostMapping("/update")
    public ResultData<String> updateUser(
            @RequestBody SysUser request,
            HttpServletRequest httpRequest) {
        // 从请求头中获取链路追踪信息
        String requestId = httpRequest.getHeader("X-Request-ID");
        String clientIp = httpRequest.getHeader("X-Client-IP");
        String userAgent = httpRequest.getHeader("X-User-Agent");


        sysUserService.updateUser(request);
        return ResultData.success("用户信息更新成功");
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
    @PostMapping("/list")
    public ResultData<IPage<SysUser>> getUserList(@RequestBody UserQueryRequest request,
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

    /**
     * 更新用户最后登录时间
     *
     * @param userId      用户ID
     * @param httpRequest HTTP请求
     * @return 更新结果
     */
    @Operation(summary = "更新最后登录时间", description = "更新指定用户的最后登录时间")
    @PostMapping("/{userId}/last-login")
    public ResultData<String> updateLastLoginTime(@Parameter(description = "用户ID") @PathVariable String userId,
                                                 HttpServletRequest httpRequest) {
        // 从请求头中获取链路追踪信息
        String requestId = httpRequest.getHeader("X-Request-ID");
        String clientIp = httpRequest.getHeader("X-Client-IP");
        String userAgent = httpRequest.getHeader("X-User-Agent");

        log.info("更新用户最后登录时间 - RequestId: {}, ClientIP: {}, UserAgent: {}, UserId: {}",
                requestId, clientIp, userAgent, userId);

        boolean success = sysUserService.updateLastLoginTime(userId);
        if (success) {
            return ResultData.success("最后登录时间更新成功");
        } else {
            return ResultData.fail(ErrorCode.USER_UPDATE_FAILED, "最后登录时间更新失败");
        }
    }

}
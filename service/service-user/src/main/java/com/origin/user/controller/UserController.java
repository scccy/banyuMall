package com.origin.user.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.origin.common.dto.ResultData;
import com.origin.common.exception.BusinessException;
import com.origin.user.dto.UserCreateRequest;
import com.origin.user.dto.UserQueryRequest;
import com.origin.user.dto.UserUpdateRequest;
import com.origin.user.entity.SysUser;
import com.origin.user.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户管理控制器
 * 
 * @author scccy
 * @since 2024-07-30
 */
@Tag(name = "用户管理", description = "用户基础信息管理接口")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

    private final SysUserService sysUserService;

    /**
     * 创建用户
     *
     * @param request 创建请求
     * @param httpRequest HTTP请求
     * @return 创建结果
     */
    @Operation(summary = "创建用户", description = "创建新用户，支持管理员和发布者两种用户类型")
    @PostMapping
    public ResultData<SysUser> createUser(@RequestBody @Valid UserCreateRequest request,
                                         HttpServletRequest httpRequest) {
        // 从请求头中获取链路追踪信息
        String requestId = httpRequest.getHeader("X-Request-ID");
        String clientIp = httpRequest.getHeader("X-Client-IP");
        String userAgent = httpRequest.getHeader("X-User-Agent");
        
        log.info("创建用户 - RequestId: {}, ClientIP: {}, UserAgent: {}, Username: {}", 
                requestId, clientIp, userAgent, request.getUsername());
        
        try {
            SysUser user = sysUserService.createUser(request);
            return ResultData.ok("用户创建成功", user);
        } catch (BusinessException e) {
            log.warn("创建用户业务异常: {}", e.getMessage());
            return ResultData.fail(e.getErrorCode(), e.getMessage());
        } catch (RuntimeException e) {
            log.error("创建用户运行时异常: ", e);
            return ResultData.fail("业务处理异常: " + e.getMessage());
        } catch (Exception e) {
            log.error("创建用户系统异常: ", e);
            return ResultData.fail("系统异常，请联系管理员");
        }
    }

    /**
     * 获取用户信息
     *
     * @param userId 用户ID
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
            return ResultData.fail("用户不存在");
        }
        
        return ResultData.ok("获取用户信息成功", user);
    }

    /**
     * 更新用户信息
     *
     * @param userId 用户ID
     * @param request 更新请求
     * @param httpRequest HTTP请求
     * @return 更新结果
     */
    @Operation(summary = "更新用户信息", description = "更新用户的基础信息（昵称、头像、邮箱等）")
    @PutMapping("/{userId}")
    public ResultData<SysUser> updateUser(@Parameter(description = "用户ID") @PathVariable String userId,
                                         @RequestBody @Valid UserUpdateRequest request,
                                         HttpServletRequest httpRequest) {
        // 从请求头中获取链路追踪信息
        String requestId = httpRequest.getHeader("X-Request-ID");
        String clientIp = httpRequest.getHeader("X-Client-IP");
        String userAgent = httpRequest.getHeader("X-User-Agent");
        
        log.info("更新用户信息 - RequestId: {}, ClientIP: {}, UserAgent: {}, UserId: {}", 
                requestId, clientIp, userAgent, userId);
        
        SysUser user = sysUserService.updateUser(userId, request);
        return ResultData.ok("用户信息更新成功", user);
    }

    /**
     * 删除用户
     *
     * @param userId 用户ID
     * @param httpRequest HTTP请求
     * @return 删除结果
     */
    @Operation(summary = "删除用户", description = "软删除指定用户")
    @DeleteMapping("/{userId}")
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
            return ResultData.ok("用户删除成功");
        } else {
            return ResultData.fail("用户删除失败");
        }
    }

    /**
     * 用户列表查询
     *
     * @param request 查询请求
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
        return ResultData.ok("查询成功", page);
    }

    /**
     * 批量删除用户
     *
     * @param userIds 用户ID列表
     * @param httpRequest HTTP请求
     * @return 删除结果
     */
    @Operation(summary = "批量删除用户", description = "批量软删除多个用户")
    @DeleteMapping("/batch")
    public ResultData<String> batchDeleteUsers(@RequestBody List<String> userIds,
                                              HttpServletRequest httpRequest) {
        // 从请求头中获取链路追踪信息
        String requestId = httpRequest.getHeader("X-Request-ID");
        String clientIp = httpRequest.getHeader("X-Client-IP");
        String userAgent = httpRequest.getHeader("X-User-Agent");
        
        log.info("批量删除用户 - RequestId: {}, ClientIP: {}, UserAgent: {}, UserIds: {}", 
                requestId, clientIp, userAgent, userIds);
        
        int successCount = sysUserService.batchDeleteUsers(userIds);
        return ResultData.ok("批量删除完成，成功删除 " + successCount + " 个用户");
    }

    /**
     * 用户服务健康检查
     *
     * @param httpRequest HTTP请求
     * @return 健康检查结果
     */
    @Operation(summary = "用户服务健康检查", description = "用于验证用户服务是否正常运行的接口")
    @GetMapping("/test")
    public ResultData<String> test(HttpServletRequest httpRequest) {
        // 从请求头中获取链路追踪信息
        String requestId = httpRequest.getHeader("X-Request-ID");
        String clientIp = httpRequest.getHeader("X-Client-IP");
        String userAgent = httpRequest.getHeader("X-User-Agent");
        
        log.info("用户服务健康检查 - RequestId: {}, ClientIP: {}, UserAgent: {}", 
                requestId, clientIp, userAgent);
        
        return ResultData.ok("User Service is running!");
    }
} 
package com.origin.banyu.user.controller;

import com.origin.banyu.common.dto.ResultData;
import com.origin.banyu.common.entity.ErrorCode;
import com.origin.banyu.common.entity.SysUser;
import com.origin.banyu.user.service.UserBatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户批量操作控制器
 * 负责用户的批量操作功能，包括批量删除、批量获取等
 * 
 * @author scccy
 * @since 2025-08-12
 */
@Tag(name = "用户批量操作", description = "用户批量操作功能接口")
@RestController
@RequestMapping("/service/user/batch")
@RequiredArgsConstructor
@Slf4j
public class UserBatchController {

    private final UserBatchService userBatchService;

    /**
     * 批量删除用户
     *
     * @param userIds     用户ID列表
     * @param httpRequest HTTP请求
     * @return 删除结果
     */
    @Operation(summary = "批量删除用户", description = "批量软删除多个用户")
    @PostMapping("/delete")
    public ResultData<String> batchDeleteUsers(@RequestBody List<String> userIds,
                                               HttpServletRequest httpRequest) {
        // 从请求头中获取链路追踪信息
        String requestId = httpRequest.getHeader("X-Request-ID");
        String clientIp = httpRequest.getHeader("X-Client-IP");
        String userAgent = httpRequest.getHeader("X-User-Agent");

        boolean success = userBatchService.batchDeleteUsers(userIds);
        if (success) {
            return ResultData.success("批量删除完成");
        } else {
            return ResultData.fail(ErrorCode.INTERNAL_ERROR, "批量删除失败");
        }
    }

    /**
     * 批量获取用户信息
     *
     * @param userIdList  用户ID列表
     * @param httpRequest HTTP请求
     * @return 用户信息列表
     */
    @Operation(summary = "批量获取用户信息", description = "根据用户ID列表批量获取用户信息，用于发布者服务等场景")
    @PostMapping("/info")
    public ResultData<List<SysUser>> getBatchUserInfo(@RequestBody  List<String> userIdList,
                                                       HttpServletRequest httpRequest) {
        // 从请求头中获取链路追踪信息
        String requestId = httpRequest.getHeader("X-Request-ID");
        String clientIp = httpRequest.getHeader("X-Client-IP");
        String userAgent = httpRequest.getHeader("X-User-Agent");
        List<SysUser> users = userBatchService.getBatchUserInfo(userIdList);
        return ResultData.success("批量获取用户信息成功", users);
    }
}

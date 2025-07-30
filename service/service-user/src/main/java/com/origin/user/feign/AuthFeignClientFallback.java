package com.origin.user.feign;

import com.origin.common.ResultData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 认证服务Feign客户端降级处理
 *
 * @author origin
 * @since 2025-01-27
 */
@Slf4j
@Component
public class AuthFeignClientFallback implements AuthFeignClient {

    @Override
    public ResultData<Object> getUserInfo(String userId, String token) {
        log.error("调用认证服务获取用户信息失败，用户ID: {}, token: {}", userId, token);
        return ResultData.error("获取用户信息失败，服务暂时不可用");
    }

    @Override
    public ResultData<Boolean> checkUserExists(String userId, String token) {
        log.error("调用认证服务验证用户存在性失败，用户ID: {}, token: {}", userId, token);
        return ResultData.error("验证用户失败，服务暂时不可用");
    }

    @Override
    public ResultData<Integer> getUserStatus(String userId, String token) {
        log.error("调用认证服务获取用户状态失败，用户ID: {}, token: {}", userId, token);
        return ResultData.error("获取用户状态失败，服务暂时不可用");
    }
} 
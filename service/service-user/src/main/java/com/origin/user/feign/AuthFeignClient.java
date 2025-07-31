package com.origin.user.feign;

import com.origin.common.dto.ResultData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * 认证服务Feign客户端
 *
 * @author origin
 * @since 2025-01-27
 */
@FeignClient(name = "service-auth", fallback = AuthFeignClientFallback.class)
public interface AuthFeignClient {

    /**
     * 根据用户ID获取用户基础信息
     *
     * @param userId 用户ID
     * @param token 认证token
     * @return 用户基础信息
     */
    @GetMapping("/auth/user/{userId}")
    ResultData<Object> getUserInfo(@PathVariable("userId") String userId,
                                  @RequestHeader("Authorization") String token);

    /**
     * 验证用户是否存在
     *
     * @param userId 用户ID
     * @param token 认证token
     * @return 验证结果
     */
    @GetMapping("/auth/user/{userId}/exists")
    ResultData<Boolean> checkUserExists(@PathVariable("userId") String userId,
                                       @RequestHeader("Authorization") String token);

    /**
     * 获取用户状态
     *
     * @param userId 用户ID
     * @param token 认证token
     * @return 用户状态
     */
    @GetMapping("/auth/user/{userId}/status")
    ResultData<Integer> getUserStatus(@PathVariable("userId") String userId,
                                     @RequestHeader("Authorization") String token);
} 
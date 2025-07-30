package com.origin.auth.feign;

import com.origin.auth.dto.LoginRequest;
import com.origin.common.ResultData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * 认证服务Feign客户端
 * 
 * @author origin
 */
@FeignClient(
    name = "service-auth",
    path = "/auth",
    fallback = AuthFeignClientFallback.class
)
public interface AuthFeignClient {

    /**
     * 用户登录
     *
     * @param loginRequest 登录请求参数
     * @return 登录结果
     */
    @PostMapping("/login")
    ResultData login(@RequestBody LoginRequest loginRequest);

    /**
     * 用户登出
     *
     * @param authorization 授权头
     * @return 登出结果
     */
    @PostMapping("/logout")
    ResultData logout(@RequestHeader("Authorization") String authorization);
} 
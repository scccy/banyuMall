package com.origin.auth.feign;

import com.origin.auth.dto.LoginRequest;
import com.origin.common.ResultData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 认证服务Feign客户端降级处理
 * 
 * @author origin
 */
@Slf4j
@Component
public class AuthFeignClientFallback implements AuthFeignClient {

    @Override
    public ResultData login(LoginRequest loginRequest) {
        log.error("认证服务登录接口调用失败，触发降级处理");
        return ResultData.fail("认证服务暂时不可用，请稍后重试");
    }

    @Override
    public ResultData logout(String authorization) {
        log.error("认证服务登出接口调用失败，触发降级处理");
        return ResultData.fail("认证服务暂时不可用，请稍后重试");
    }
} 
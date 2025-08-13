package com.origin.banyu.publisher.feign;

import com.origin.banyu.common.dto.ResultData;
import com.origin.banyu.common.entity.SysUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 用户服务Feign客户端
 * 用于发布者模块获取用户信息
 * 
 * @author scccy
 * @since 2025-08-13
 */
@FeignClient(name = "service-user", path = "/service/user", fallback = UserFeignClientFallback.class)
public interface UserFeignClient {
    
    /**
     * 根据用户ID获取用户信息
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    @GetMapping("/{userId}")
    ResultData<SysUser> getUserById(@PathVariable("userId") String userId);
    
    /**
     * 批量获取用户信息
     *
     * @param userIds 用户ID列表
     * @return 用户信息列表
     */
    @PostMapping("/batch/info")
    ResultData<List<SysUser>> getBatchUserInfo(@RequestBody List<String> userIds);
}

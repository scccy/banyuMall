package com.origin.user.feign;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.origin.common.dto.ResultData;
import com.origin.user.entity.SysUser;
import com.origin.user.entity.UserProfile;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * 用户服务Feign客户端
 * 供其他服务调用用户服务的查询接口
 * 
 * @author scccy
 * @since 2024-07-30
 */
@FeignClient(name = "service-user", fallback = UserFeignClientFallback.class)
public interface UserFeignClient {
    
    /**
     * 获取用户信息
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    @GetMapping("/user/{userId}")
    ResultData<SysUser> getUserInfo(@PathVariable("userId") String userId);
    
    /**
     * 获取用户列表
     *
     * @param params 查询参数
     * @return 分页用户列表
     */
    @GetMapping("/user/list")
    ResultData<IPage<SysUser>> getUserList(@RequestParam Map<String, Object> params);
    
    /**
     * 获取用户扩展信息
     *
     * @param userId 用户ID
     * @return 用户扩展信息
     */
    @GetMapping("/user/profile/{userId}")
    ResultData<UserProfile> getUserProfile(@PathVariable("userId") String userId);
} 
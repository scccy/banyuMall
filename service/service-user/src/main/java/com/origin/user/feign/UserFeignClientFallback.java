package com.origin.user.feign;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.origin.common.dto.ResultData;
import com.origin.user.entity.SysUser;
import com.origin.user.entity.UserProfile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 用户服务Feign客户端降级处理
 * 
 * @author scccy
 * @since 2024-07-30
 */
@Slf4j
@Component
public class UserFeignClientFallback implements UserFeignClient {
    
    @Override
    public ResultData<SysUser> getUserInfo(String userId) {
        log.warn("用户服务调用失败，触发降级处理 - getUserInfo, userId: {}", userId);
        return ResultData.fail("用户服务暂时不可用");
    }
    
    @Override
    public ResultData<IPage<SysUser>> getUserList(Map<String, Object> params) {
        log.warn("用户服务调用失败，触发降级处理 - getUserList, params: {}", params);
        return ResultData.fail("用户服务暂时不可用");
    }
    
    @Override
    public ResultData<UserProfile> getUserProfile(String userId) {
        log.warn("用户服务调用失败，触发降级处理 - getUserProfile, userId: {}", userId);
        return ResultData.fail("用户服务暂时不可用");
    }
} 
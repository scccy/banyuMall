package com.origin.banyu.publisher.feign;

import com.origin.banyu.common.dto.ResultData;
import com.origin.banyu.common.entity.SysUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 用户服务Feign客户端降级处理
 * 当用户服务不可用时的降级逻辑
 * 
 * @author scccy
 * @since 2025-08-13
 */
@Slf4j
@Component
public class UserFeignClientFallback implements UserFeignClient {
    
    @Override
    public ResultData<SysUser> getUserById(String userId) {
        log.warn("用户服务不可用，降级处理 - getUserById: {}", userId);
        return ResultData.fail(500, "用户服务暂时不可用");
    }
    
    @Override
    public ResultData<List<SysUser>> getBatchUserInfo(List<String> userIds) {
        log.warn("用户服务不可用，降级处理 - getBatchUserInfo: {}", userIds);
        return ResultData.fail(500, "用户服务暂时不可用");
    }
}

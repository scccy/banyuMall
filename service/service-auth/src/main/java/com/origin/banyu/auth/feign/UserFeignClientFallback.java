package com.origin.banyu.auth.feign;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.origin.banyu.common.dto.ResultData;
import com.origin.banyu.common.entity.SysUser;
import org.springframework.stereotype.Component;

@Component
public class UserFeignClientFallback implements UserFeignClient {
    @Override
    public ResultData<SysUser> getUserById(String userId) {
        return ResultData.fail(503, "用户服务不可用", null);
    }

    @Override
    public ResultData<IPage<SysUser>> getUserList(String username, Integer current, Integer size) {
        return ResultData.fail(503, "用户服务不可用", null);
    }

    @Override
    public ResultData<String> updateLastLoginTime(String userId) {
        return ResultData.fail(503, "用户服务不可用", null);
    }
}



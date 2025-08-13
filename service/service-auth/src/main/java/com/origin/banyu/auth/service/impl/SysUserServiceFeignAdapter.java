package com.origin.banyu.auth.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.origin.banyu.auth.feign.UserFeignClient;
import com.origin.banyu.auth.service.SysUserService;
import com.origin.banyu.common.dto.ResultData;
import com.origin.banyu.common.entity.SysUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SysUserServiceFeignAdapter implements SysUserService {

    private final UserFeignClient userFeignClient;
    private final PasswordEncoder passwordEncoder;

    @Override
    public SysUser getByUsername(String username) {
        // 兼容保留，但登录已改为直接用 userId(=phone) 查询
        ResultData<IPage<SysUser>> pageResult =
                userFeignClient.getUserList(username, 1, 1);
        if (pageResult == null || pageResult.getData() == null || pageResult.getData().getRecords().isEmpty()) {
            return null;
        }
        return pageResult.getData().getRecords().getFirst();
    }

    @Override
    public SysUser getById(String userId) {
        ResultData<SysUser> result = userFeignClient.getUserById(userId);
        return result != null ? result.getData() : null;
    }

    @Override
    public void updateLastLoginTime(String userId) {
        try {
            userFeignClient.updateLastLoginTime(userId);
        } catch (Exception e) {
            log.warn("调用用户服务更新最后登录时间失败: {}", e.getMessage());
        }
    }

    @Override
    public boolean validatePassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    @Override
    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    @Override
    public boolean isUserStatusNormal(SysUser user) {
        return user != null && user.getStatus() != null && user.getStatus() == 1;
    }
}



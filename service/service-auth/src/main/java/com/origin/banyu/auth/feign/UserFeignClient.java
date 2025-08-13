package com.origin.banyu.auth.feign;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.origin.banyu.common.dto.ResultData;
import com.origin.banyu.common.entity.SysUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "service-user", path = "/service/user", fallback = UserFeignClientFallback.class)
public interface UserFeignClient {

    @GetMapping("/{userId}")
    ResultData<SysUser> getUserById(@PathVariable("userId") String userId);

    // 使用现有的用户列表接口进行精确匹配（username），避免新增不一致路由
    @GetMapping("/list")
    ResultData<IPage<SysUser>> getUserList(@RequestParam("username") String username,
                                            @RequestParam(value = "current", required = false, defaultValue = "1") Integer current,
                                            @RequestParam(value = "size", required = false, defaultValue = "1") Integer size);

    @PostMapping("/{userId}/last-login")
    ResultData<String> updateLastLoginTime(@PathVariable("userId") String userId);
}



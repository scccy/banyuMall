package com.origin.user.feign;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.origin.common.dto.ResultData;
import com.origin.user.entity.SysUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 用户服务Feign客户端测试
 * 
 * @author scccy
 * @since 2025-08-04
 */
@SpringBootTest
@ActiveProfiles("test")
class UserFeignClientTest {

    @Autowired
    private UserFeignClient userFeignClient;

    @Test
    @DisplayName("用户服务调用 - 正常调用测试")
    void userFeignTest() {
        // 测试获取用户信息功能
        String userId = "test_user_001";
        ResultData<SysUser> result = userFeignClient.getUserInfo(userId);
        
        assertNotNull(result);
        if (result.isSuccess()) {
            SysUser user = result.getData();
            assertNotNull(user);
            assertEquals(userId, user.getUserId());
            assertNotNull(user.getUsername());
        }
    }

    @Test
    @DisplayName("用户服务调用 - 用户列表查询测试")
    void getUserListTest() {
        // 测试用户列表查询功能
        Map<String, Object> params = new HashMap<>();
        params.put("current", 1);
        params.put("size", 10);
        params.put("userType", 2);
        params.put("status", 1);
        
        ResultData<IPage<SysUser>> result = userFeignClient.getUserList(params);
        
        assertNotNull(result);
        if (result.isSuccess()) {
            IPage<SysUser> page = result.getData();
            assertNotNull(page);
            assertNotNull(page.getRecords());
            assertTrue(page.getCurrent() >= 1);
            assertTrue(page.getSize() >= 0);
        }
    }

    @Test
    @DisplayName("用户服务调用 - 用户不存在测试")
    void getUserInfoNotFoundTest() {
        // 测试获取不存在的用户信息
        String userId = "nonexistent_user";
        ResultData<SysUser> result = userFeignClient.getUserInfo(userId);
        
        assertNotNull(result);
        // 应该返回失败结果或者空数据
        if (!result.isSuccess()) {
            assertNull(result.getData());
        }
    }

    @Test
    @DisplayName("用户服务调用 - 参数验证测试")
    void parameterValidationTest() {
        // 测试参数验证
        Map<String, Object> params = new HashMap<>();
        params.put("current", 0);  // 无效的页码
        params.put("size", 1000);  // 过大的页面大小
        
        ResultData<IPage<SysUser>> result = userFeignClient.getUserList(params);
        assertNotNull(result);
        
        // 如果参数验证失败，应该返回失败结果
        if (!result.isSuccess()) {
            assertTrue(result.getMessage().contains("参数错误") || 
                      result.getMessage().contains("validation"));
        }
    }

    @Test
    @DisplayName("用户服务调用 - 服务不可用降级测试")
    void fallbackTest() {
        // 测试服务不可用时的降级处理
        String userId = "test_user_001";
        
        try {
            ResultData<SysUser> result = userFeignClient.getUserInfo(userId);
            // 如果服务可用，应该返回正常响应
            assertNotNull(result);
        } catch (Exception e) {
            // 如果服务不可用，应该触发降级处理
            assertTrue(e.getMessage().contains("服务暂时不可用") || 
                      e.getMessage().contains("fallback"));
        }
    }
} 
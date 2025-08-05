package com.origin.user.feign;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.origin.common.dto.ResultData;
import com.origin.user.entity.SysUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * 用户服务Feign客户端测试
 * 
 * @author scccy
 * @since 2025-08-04
 */
@SpringBootTest
@ActiveProfiles("test")
class UserFeignClientTest {

    @MockBean
    private UserFeignClient userFeignClient;

    @Test
    @DisplayName("用户服务调用 - 正常调用测试")
    void userFeignTest() {
        // 模拟用户信息响应
        SysUser mockUser = new SysUser();
        mockUser.setUserId("test_user_001");
        mockUser.setUsername("testuser");
        mockUser.setNickname("测试用户");
        mockUser.setEmail("test@example.com");
        
        when(userFeignClient.getUserInfo("test_user_001"))
                .thenReturn(ResultData.ok("获取用户信息成功", mockUser));
        
        // 测试获取用户信息功能
        String userId = "test_user_001";
        ResultData<SysUser> result = userFeignClient.getUserInfo(userId);
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
        SysUser user = result.getData();
        assertNotNull(user);
        assertEquals(userId, user.getUserId());
        assertEquals("testuser", user.getUsername());
    }

    @Test
    @DisplayName("用户服务调用 - 用户列表查询测试")
    void getUserListTest() {
        // 模拟用户列表响应
        // 使用新的Page构造函数，避免过时API
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<SysUser> mockPage = 
            new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(1, 10);
        mockPage.setRecords(Arrays.asList(
            createMockUser("test_user_001", "testuser1"),
            createMockUser("test_user_002", "testuser2")
        ));
        mockPage.setTotal(2);
        
        when(userFeignClient.getUserList(any(Map.class)))
                .thenReturn(ResultData.ok("查询成功", mockPage));
        
        // 测试用户列表查询功能
        Map<String, Object> params = new HashMap<>();
        params.put("current", 1);
        params.put("size", 10);
        params.put("userType", 2);
        params.put("status", 1);
        
        ResultData<IPage<SysUser>> result = userFeignClient.getUserList(params);
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
        IPage<SysUser> page = result.getData();
        assertNotNull(page);
        assertNotNull(page.getRecords());
        assertEquals(2, page.getTotal());
        assertEquals(1, page.getCurrent());
        assertEquals(10, page.getSize());
    }

    @Test
    @DisplayName("用户服务调用 - 用户不存在测试")
    void getUserInfoNotFoundTest() {
        // 模拟用户不存在响应
        when(userFeignClient.getUserInfo("nonexistent_user"))
                .thenReturn(ResultData.fail("用户不存在"));
        
        // 测试获取不存在的用户信息
        String userId = "nonexistent_user";
        ResultData<SysUser> result = userFeignClient.getUserInfo(userId);
        
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("用户不存在"));
    }

    @Test
    @DisplayName("用户服务调用 - 参数验证测试")
    void parameterValidationTest() {
        // 模拟参数验证失败响应
        when(userFeignClient.getUserList(any(Map.class)))
                .thenReturn(ResultData.fail("参数错误：页码必须大于0"));
        
        // 测试参数验证
        Map<String, Object> params = new HashMap<>();
        params.put("current", 0);  // 无效的页码
        params.put("size", 1000);  // 过大的页面大小
        
        ResultData<IPage<SysUser>> result = userFeignClient.getUserList(params);
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("参数错误"));
    }

    @Test
    @DisplayName("用户服务调用 - 服务不可用降级测试")
    void fallbackTest() {
        // 模拟服务不可用时的降级响应
        when(userFeignClient.getUserInfo("test_user_001"))
                .thenReturn(ResultData.fail("服务暂时不可用"));
        
        // 测试服务不可用时的降级处理
        String userId = "test_user_001";
        ResultData<SysUser> result = userFeignClient.getUserInfo(userId);
        
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("服务暂时不可用"));
    }
    
    /**
     * 创建模拟用户
     */
    private SysUser createMockUser(String userId, String username) {
        SysUser user = new SysUser();
        user.setUserId(userId);
        user.setUsername(username);
        user.setNickname("测试用户");
        user.setEmail(username + "@example.com");
        user.setUserType(2);
        user.setStatus(1);
        return user;
    }
} 
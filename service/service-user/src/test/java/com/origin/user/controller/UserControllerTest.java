package com.origin.user.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.origin.banyu.common.entity.SysUser;
import com.origin.banyu.user.dto.UserCreateRequest;
import com.origin.banyu.user.dto.UserQueryRequest;
import com.origin.banyu.user.dto.UserUpdateRequest;
import com.origin.banyu.user.service.SysUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.Random;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * UserController API测试类
 * 专注于测试UserController的核心功能
 */
@SpringBootTest(classes = com.origin.banyu.user.ServiceUserApplication.class)
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private SysUserService sysUserService;

    private MockMvc mockMvc;
    private Random random = new Random();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    /**
     * 生成随机手机号
     * @return 随机手机号
     */
    private String generateRandomPhone() {
        return "138" + String.format("%08d", random.nextInt(100000000));
    }

    @Test
    @DisplayName("创建用户 - 正常请求测试")
    void createUserTest() throws Exception {
        // 生成随机手机号，用户名等于手机号
        String randomPhone = generateRandomPhone();
        
        // 准备测试数据
        UserCreateRequest userCreateRequest = new UserCreateRequest();
        userCreateRequest.setUsername(randomPhone);  // 用户名等于手机号
        userCreateRequest.setPassword("123456");
        userCreateRequest.setNickname("测试用户");
        userCreateRequest.setEmail("test@example.com");
        userCreateRequest.setPhone(randomPhone);
        userCreateRequest.setWechatId("test_wechat_001");  // 添加必填字段
        userCreateRequest.setYouzanId("test_youzan_001");  // 添加必填字段
        userCreateRequest.setUserType(2);

        SysUser createdUser = new SysUser();
        createdUser.setUserId("test_user_001");
        createdUser.setUsername(randomPhone);
        createdUser.setNickname("测试用户");
        createdUser.setEmail("test@example.com");
        createdUser.setPhone(randomPhone);
        createdUser.setWechatId("0");
        createdUser.setYouzanId("0");
        createdUser.setAvatar("https://oss.example.com/avatars/test_user_001.jpg");
        createdUser.setUserType(2);
        createdUser.setStatus(1);

        // 使用真实服务，不需要Mock配置

        // 创建测试文件
        MockMultipartFile avatarFile = new MockMultipartFile(
                "avatarFile",
                "test-avatar.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        MockMultipartFile userInfoFile = new MockMultipartFile(
                "userInfo",
                "",
                "application/json",
                String.format("{\"username\":\"%s\",\"password\":\"123456\",\"nickname\":\"测试用户\",\"email\":\"test@example.com\",\"phone\":\"%s\",\"wechatId\":\"test_wechat_001\",\"youzanId\":\"test_youzan_001\",\"userType\":2}", randomPhone, randomPhone).getBytes()
        );

        // 执行测试
        mockMvc.perform(multipart("/service/user")
                .file(avatarFile)
                .file(userInfoFile)
                .header("X-Request-ID", "test-request-123")
                .header("X-Client-IP", "127.0.0.1")
                .header("X-User-Agent", "Mozilla/5.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("用户创建成功"))
                .andExpect(jsonPath("$.data.userId").value("test_user_001"))
                .andExpect(jsonPath("$.data.username").value("testuser"));
    }

    @Test
    @DisplayName("创建用户 - 参数验证测试")
    void createUserValidationTest() throws Exception {
        // 测试缺少必填参数的情况
        MockMultipartFile userInfoFile = new MockMultipartFile(
                "userInfo",
                "",
                "application/json",
                "{\"password\":\"123456\"}".getBytes()
        );

        mockMvc.perform(multipart("/service/user")
                .file(userInfoFile)
                .header("X-Request-ID", "test-request-123"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("创建用户 - 异常情况测试")
    void createUserExceptionTest() throws Exception {
        // 生成随机手机号，用户名等于手机号
        String randomPhone = generateRandomPhone();
        
        // 使用真实服务，不需要Mock配置

        MockMultipartFile userInfoFile = new MockMultipartFile(
                "userInfo",
                "",
                "application/json",
                String.format("{\"username\":\"%s\",\"password\":\"123456\",\"nickname\":\"测试用户\",\"email\":\"test@example.com\",\"phone\":\"%s\",\"wechatId\":\"test_wechat_002\",\"youzanId\":\"test_youzan_002\",\"userType\":2}", randomPhone, randomPhone).getBytes()
        );

        mockMvc.perform(multipart("/service/user")
                .file(userInfoFile)
                .header("X-Request-ID", "test-request-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1002))
                .andExpect(jsonPath("$.message").value("用户创建失败: 用户名已存在"));
    }

    @Test
    @DisplayName("获取用户信息 - 正常请求测试")
    void getUserInfoTest() throws Exception {
        // 准备测试数据
        SysUser user = new SysUser();
        user.setUserId("test_user_001");
        user.setUsername("testuser");
        user.setNickname("测试用户");
        user.setEmail("test@example.com");
        user.setPhone("13800138000");
        user.setAvatar("https://oss.example.com/avatars/test_user_001.jpg");
        user.setUserType(2);
        user.setStatus(1);

        // 使用真实服务，不需要Mock配置

        // 执行测试
        mockMvc.perform(get("/service/user/test_user_001")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Request-ID", "test-request-123")
                .header("X-Client-IP", "127.0.0.1")
                .header("X-User-Agent", "Mozilla/5.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("获取用户信息成功"))
                .andExpect(jsonPath("$.data.userId").value("test_user_001"))
                .andExpect(jsonPath("$.data.username").value("testuser"));
    }

    @Test
    @DisplayName("获取用户信息 - 用户不存在测试")
    void getUserInfoNotFoundTest() throws Exception {
        // 使用真实服务，不需要Mock配置

        mockMvc.perform(get("/service/user/nonexistent")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Request-ID", "test-request-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1001))
                .andExpect(jsonPath("$.message").value("用户不存在"));
    }

    @Test
    @DisplayName("更新用户信息 - 正常请求测试")
    void updateUserTest() throws Exception {
        // 准备测试数据
        UserUpdateRequest userUpdateRequest = new UserUpdateRequest();
        userUpdateRequest.setNickname("更新后的昵称");
        userUpdateRequest.setEmail("newemail@example.com");
        userUpdateRequest.setContactPhone("13800138001");

        SysUser updatedUser = new SysUser();
        updatedUser.setUserId("test_user_001");
        updatedUser.setUsername("testuser");
        updatedUser.setNickname("更新后的昵称");
        updatedUser.setEmail("newemail@example.com");
        updatedUser.setPhone("13800138001");
        updatedUser.setAvatar("https://oss.example.com/avatars/test_user_001_new.jpg");
        updatedUser.setUserType(2);
        updatedUser.setStatus(1);

        // 使用真实服务，不需要Mock配置

        // 创建测试文件
        MockMultipartFile avatarFile = new MockMultipartFile(
                "avatarFile",
                "new-avatar.jpg",
                "image/jpeg",
                "new image content".getBytes()
        );

        MockMultipartFile userInfoFile = new MockMultipartFile(
                "userInfo",
                "",
                "application/json",
                "{\"nickname\":\"更新后的昵称\",\"email\":\"newemail@example.com\",\"phone\":\"13800138001\"}".getBytes()
        );

        // 执行测试
        mockMvc.perform(multipart("/service/user/test_user_001")
                .file(avatarFile)
                .file(userInfoFile)
                .header("X-Request-ID", "test-request-123")
                .header("X-Client-IP", "127.0.0.1")
                .header("X-User-Agent", "Mozilla/5.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("用户信息更新成功"))
                .andExpect(jsonPath("$.data.nickname").value("更新后的昵称"));
    }

    @Test
    @DisplayName("更新用户信息 - 参数验证测试")
    void updateUserValidationTest() throws Exception {
        // 测试空对象的情况（所有字段都是可选的，所以空对象是有效的）
        MockMultipartFile userInfoFile = new MockMultipartFile(
                "userInfo",
                "",
                "application/json",
                "{}".getBytes()
        );

        // 使用真实服务，不需要Mock配置

        mockMvc.perform(multipart("/service/user/test_user_001")
                .file(userInfoFile)
                .header("X-Request-ID", "test-request-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("用户信息更新成功"));
    }

    @Test
    @DisplayName("删除用户 - 正常请求测试")
    void deleteUserTest() throws Exception {
        // 使用真实服务，不需要Mock配置

        mockMvc.perform(post("/service/user/test_user_001/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Request-ID", "test-request-123")
                .header("X-Client-IP", "127.0.0.1")
                .header("X-User-Agent", "Mozilla/5.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("用户删除成功"));
    }

    @Test
    @DisplayName("删除用户 - 删除失败测试")
    void deleteUserFailureTest() throws Exception {
        // 使用真实服务，不需要Mock配置

        mockMvc.perform(post("/service/user/nonexistent/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Request-ID", "test-request-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1009))
                .andExpect(jsonPath("$.message").value("用户删除失败"));
    }

    @Test
    @DisplayName("用户列表查询 - 正常请求测试")
    void getUserListTest() throws Exception {
        // 准备测试数据
        SysUser user = new SysUser();
        user.setUserId("test_user_001");
        user.setUsername("testuser");
        user.setNickname("测试用户");
        user.setEmail("test@example.com");
        user.setPhone("13800138000");
        user.setAvatar("https://oss.example.com/avatars/test_user_001.jpg");
        user.setUserType(2);
        user.setStatus(1);

        // 使用真实服务，不需要Mock配置

        // 执行测试
        mockMvc.perform(get("/service/user/list")
                .param("current", "1")
                .param("size", "10")
                .param("keyword", "test")
                .param("userType", "2")
                .param("status", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Request-ID", "test-request-123")
                .header("X-Client-IP", "127.0.0.1")
                .header("X-User-Agent", "Mozilla/5.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("查询成功"))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.records[0].userId").value("test_user_001"));
    }

    @Test
    @DisplayName("用户列表查询 - 参数验证测试")
    void getUserListValidationTest() throws Exception {
        // 测试无效参数的情况
        mockMvc.perform(get("/service/user/list")
                .param("current", "0")  // 无效的页码
                .param("size", "1000")  // 过大的页面大小
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Request-ID", "test-request-123"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("健康检查 - 正常请求测试")
    void testTest() throws Exception {
        mockMvc.perform(get("/service/user/test")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Request-ID", "test-request-123")
                .header("X-Client-IP", "127.0.0.1")
                .header("X-User-Agent", "Mozilla/5.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("User Service is running!"));
    }

    @Test
    @DisplayName("创建用户 - 权限验证测试")
    void createUserPermissionTest() throws Exception {
        // 生成随机手机号，用户名等于手机号
        String randomPhone = generateRandomPhone();
        
        // 测试不同用户类型的创建权限
        UserCreateRequest adminRequest = new UserCreateRequest();
        adminRequest.setUsername(randomPhone);
        adminRequest.setPassword("123456");
        adminRequest.setPhone(randomPhone);  // 添加必填字段
        adminRequest.setWechatId("admin_wechat_001");  // 添加必填字段
        adminRequest.setYouzanId("admin_youzan_001");  // 添加必填字段
        adminRequest.setUserType(1); // 管理员

        // 使用真实服务，不需要Mock配置

        MockMultipartFile userInfoFile = new MockMultipartFile(
                "userInfo",
                "",
                "application/json",
                String.format("{\"username\":\"%s\",\"password\":\"123456\",\"phone\":\"%s\",\"wechatId\":\"admin_wechat_001\",\"youzanId\":\"admin_youzan_001\",\"userType\":1}", randomPhone, randomPhone).getBytes()
        );

        mockMvc.perform(multipart("/service/user")
                .file(userInfoFile)
                .header("X-Request-ID", "test-request-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.userType").value(1));
    }

    @Test
    @DisplayName("更新用户信息 - 异常情况测试")
    void updateUserExceptionTest() throws Exception {
        // 使用真实服务，不需要Mock配置

        MockMultipartFile userInfoFile = new MockMultipartFile(
                "userInfo",
                "",
                "application/json",
                "{\"nickname\":\"更新后的昵称\"}".getBytes()
        );

        mockMvc.perform(multipart("/service/user/test_user_001")
                .file(userInfoFile)
                .header("X-Request-ID", "test-request-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1009))
                .andExpect(jsonPath("$.message").value("用户信息更新失败: 用户信息更新失败"));
    }

    @Test
    @DisplayName("用户列表查询 - 空结果测试")
    void getUserListEmptyTest() throws Exception {
        // 使用真实服务，不需要Mock配置

        mockMvc.perform(get("/service/user/list")
                .param("username", "nonexistent")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Request-ID", "test-request-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(0))
                .andExpect(jsonPath("$.data.records").isEmpty());
    }
} 
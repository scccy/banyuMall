package com.origin.user.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.origin.user.dto.AvatarResponse;
import com.origin.user.dto.UserCreateRequest;
import com.origin.user.dto.UserQueryRequest;
import com.origin.user.dto.UserUpdateRequest;
import com.origin.user.entity.SysUser;
import com.origin.user.service.SysUserService;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * UserController API测试类
 */
@SpringBootTest
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private SysUserService sysUserService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @DisplayName("创建用户 - 正常请求测试")
    void createUserTest() throws Exception {
        // 准备测试数据
        UserCreateRequest userCreateRequest = new UserCreateRequest();
        userCreateRequest.setUsername("testuser");
        userCreateRequest.setPassword("123456");
        userCreateRequest.setNickname("测试用户");
        userCreateRequest.setEmail("test@example.com");
        userCreateRequest.setPhone("13800138000");
        userCreateRequest.setWechatId("test_wechat_001");  // 添加必填字段
        userCreateRequest.setYouzanId("test_youzan_001");  // 添加必填字段
        userCreateRequest.setUserType(2);

        SysUser createdUser = new SysUser();
        createdUser.setUserId("test_user_001");
        createdUser.setUsername("testuser");
        createdUser.setNickname("测试用户");
        createdUser.setEmail("test@example.com");
        createdUser.setPhone("13800138000");
        createdUser.setWechatId("0");
        createdUser.setYouzanId("0");
        createdUser.setAvatar("https://oss.example.com/avatars/test_user_001.jpg");
        createdUser.setUserType(2);
        createdUser.setStatus(1);

        when(sysUserService.createUserWithAvatar(any(UserCreateRequest.class), any()))
                .thenReturn(createdUser);

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
                "{\"username\":\"testuser\",\"password\":\"123456\",\"nickname\":\"测试用户\",\"email\":\"test@example.com\",\"phone\":\"13800138000\",\"wechatId\":\"test_wechat_001\",\"youzanId\":\"test_youzan_001\",\"userType\":2}".getBytes()
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
        // 模拟创建用户失败
        when(sysUserService.createUserWithAvatar(any(UserCreateRequest.class), any()))
                .thenThrow(new RuntimeException("用户名已存在"));

        MockMultipartFile userInfoFile = new MockMultipartFile(
                "userInfo",
                "",
                "application/json",
                "{\"username\":\"existinguser\",\"password\":\"123456\",\"nickname\":\"测试用户\",\"email\":\"test@example.com\",\"phone\":\"13800138000\",\"wechatId\":\"test_wechat_002\",\"youzanId\":\"test_youzan_002\",\"userType\":2}".getBytes()
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

        when(sysUserService.getUserById("test_user_001")).thenReturn(user);

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
        when(sysUserService.getUserById("nonexistent")).thenReturn(null);

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

        when(sysUserService.updateUserWithAvatar(anyString(), any(UserUpdateRequest.class), any()))
                .thenReturn(updatedUser);

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

        // 模拟服务层返回用户信息
        SysUser updatedUser = new SysUser();
        updatedUser.setUserId("test_user_001");
        updatedUser.setUsername("testuser");
        updatedUser.setUserType(2);
        updatedUser.setStatus(1);

        when(sysUserService.updateUserWithAvatar(anyString(), any(UserUpdateRequest.class), any()))
                .thenReturn(updatedUser);

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
        when(sysUserService.deleteUser("test_user_001")).thenReturn(true);

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
        when(sysUserService.deleteUser("nonexistent")).thenReturn(false);

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

        IPage<SysUser> page = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>();
        page.setRecords(Arrays.asList(user));
        page.setTotal(1);
        page.setSize(10);
        page.setCurrent(1);
        page.setPages(1);

        when(sysUserService.getUserPage(any(UserQueryRequest.class))).thenReturn(page);

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
    @DisplayName("批量删除用户 - 正常请求测试")
    void batchDeleteUsersTest() throws Exception {
        List<String> userIds = Arrays.asList("test_user_001", "test_user_002", "test_user_003");
        when(sysUserService.batchDeleteUsers(userIds)).thenReturn(3);

        mockMvc.perform(post("/service/user/batch/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .content("[\"test_user_001\",\"test_user_002\",\"test_user_003\"]")
                .header("X-Request-ID", "test-request-123")
                .header("X-Client-IP", "127.0.0.1")
                .header("X-User-Agent", "Mozilla/5.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("批量删除完成，成功删除 3 个用户"));
    }

    @Test
    @DisplayName("批量删除用户 - 部分删除失败测试")
    void batchDeleteUsersPartialFailureTest() throws Exception {
        List<String> userIds = Arrays.asList("test_user_001", "nonexistent", "test_user_003");
        when(sysUserService.batchDeleteUsers(userIds)).thenReturn(2);

        mockMvc.perform(post("/service/user/batch/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .content("[\"test_user_001\",\"nonexistent\",\"test_user_003\"]")
                .header("X-Request-ID", "test-request-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("批量删除完成，成功删除 2 个用户"));
    }

    @Test
    @DisplayName("获取用户头像信息 - 正常请求测试")
    void getAvatarInfoTest() throws Exception {
        // 准备测试数据
        AvatarResponse avatarResponse = new AvatarResponse();
        avatarResponse.setUserId("test_user_001");
        avatarResponse.setAvatarUrl("https://oss.example.com/avatars/test_user_001.jpg");
        avatarResponse.setAvatarThumbnail("https://oss.example.com/avatars/test_user_001_thumb.jpg");
        avatarResponse.setFileSize(102400L);
        avatarResponse.setFileType("image/jpeg");

        when(sysUserService.getAvatarInfo("test_user_001")).thenReturn(avatarResponse);

        // 执行测试
        mockMvc.perform(get("/service/user/test_user_001/avatar")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Request-ID", "test-request-123")
                .header("X-Client-IP", "127.0.0.1")
                .header("X-User-Agent", "Mozilla/5.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("获取头像信息成功"))
                .andExpect(jsonPath("$.data.userId").value("test_user_001"))
                .andExpect(jsonPath("$.data.avatarUrl").exists());
    }

    @Test
    @DisplayName("获取用户头像信息 - 异常情况测试")
    void getAvatarInfoExceptionTest() throws Exception {
        // 模拟获取头像信息失败
        when(sysUserService.getAvatarInfo(anyString()))
                .thenThrow(new RuntimeException("头像信息获取失败"));

        mockMvc.perform(get("/service/user/test_user_001/avatar")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Request-ID", "test-request-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1008))
                .andExpect(jsonPath("$.message").value("头像信息获取失败: 头像信息获取失败"));
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
        // 测试不同用户类型的创建权限
        UserCreateRequest adminRequest = new UserCreateRequest();
        adminRequest.setUsername("admin");
        adminRequest.setPassword("123456");
        adminRequest.setPhone("13800138001");  // 添加必填字段
        adminRequest.setWechatId("admin_wechat_001");  // 添加必填字段
        adminRequest.setYouzanId("admin_youzan_001");  // 添加必填字段
        adminRequest.setUserType(1); // 管理员

        SysUser adminUser = new SysUser();
        adminUser.setUserId("admin_001");
        adminUser.setUsername("admin");
        adminUser.setUserType(1);

        when(sysUserService.createUserWithAvatar(any(UserCreateRequest.class), any()))
                .thenReturn(adminUser);

        MockMultipartFile userInfoFile = new MockMultipartFile(
                "userInfo",
                "",
                "application/json",
                "{\"username\":\"admin\",\"password\":\"123456\",\"phone\":\"13800138001\",\"wechatId\":\"admin_wechat_001\",\"youzanId\":\"admin_youzan_001\",\"userType\":1}".getBytes()
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
        // 模拟更新用户信息失败
        when(sysUserService.updateUserWithAvatar(anyString(), any(UserUpdateRequest.class), any()))
                .thenThrow(new RuntimeException("用户信息更新失败"));

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
        // 测试空查询结果
        IPage<SysUser> emptyPage = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>();
        emptyPage.setRecords(Arrays.asList());
        emptyPage.setTotal(0);
        emptyPage.setSize(10);
        emptyPage.setCurrent(1);
        emptyPage.setPages(0);

        when(sysUserService.getUserPage(any(UserQueryRequest.class))).thenReturn(emptyPage);

        mockMvc.perform(get("/service/user/list")
                .param("keyword", "nonexistent")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Request-ID", "test-request-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(0))
                .andExpect(jsonPath("$.data.records").isEmpty());
    }
} 
package com.origin.banyu.wechatWork.service;

import com.origin.banyu.wechatWork.adapter.WechatWorkApiAdapter;
import com.origin.banyu.common.dto.WechatWorkUserInfo;
import com.origin.banyu.wechatWork.entity.WechatWorkUser;
import com.origin.banyu.wechatWork.exception.WechatWorkServiceException;
import com.origin.banyu.wechatWork.mapper.WechatWorkUserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 企业微信用户服务测试类
 * 
 * @author scccy
 */
@ExtendWith(MockitoExtension.class)
class WechatWorkUserServiceTest {

    @Mock
    private WechatWorkUserMapper userMapper;

    @Mock
    private AccessTokenService accessTokenService;

    @Mock
    private WechatWorkApiAdapter wechatWorkApiAdapter;

    @InjectMocks
    private WechatWorkUserService wechatWorkUserService;

    private WechatWorkUserInfo testUserInfo;
    private WechatWorkUser testUser;

    @BeforeEach
    void setUp() {
        // 初始化测试数据
        testUserInfo = new WechatWorkUserInfo();
        testUserInfo.setWechatworkUserId("test_user_id");
        testUserInfo.setName("测试用户");
        testUserInfo.setMobile("13800138000");
        testUserInfo.setEmail("test@example.com");
        testUserInfo.setStatus(1);
        testUserInfo.setEnable(1);

        testUser = new WechatWorkUser();
        testUser.setWechatworkUserId("test_user_id");
        testUser.setName("测试用户");
        testUser.setMobile("13800138000");
        testUser.setEmail("test@example.com");
        testUser.setStatus(1);
        testUser.setEnable(1);
    }

    @Test
    @DisplayName("获取用户信息 - 用户存在")
    void testGetUserByWechatworkUserId_UserExists() {
        // Given
        String wechatworkUserId = "test_user_id";
        when(userMapper.selectByWechatworkUserId(wechatworkUserId)).thenReturn(testUser);

        // When
        WechatWorkUser result = wechatWorkUserService.getUserByWechatworkUserId(wechatworkUserId);

        // Then
        assertNotNull(result);
        assertEquals(wechatworkUserId, result.getWechatworkUserId());
        assertEquals("测试用户", result.getName());
        verify(userMapper).selectByWechatworkUserId(wechatworkUserId);
    }

    @Test
    @DisplayName("获取用户信息 - 用户不存在")
    void testGetUserByWechatworkUserId_UserNotExists() {
        // Given
        String wechatworkUserId = "non_existent_user_id";
        when(userMapper.selectByWechatworkUserId(wechatworkUserId)).thenReturn(null);

        // When & Then
        WechatWorkServiceException exception = assertThrows(WechatWorkServiceException.class, 
                () -> wechatWorkUserService.getUserByWechatworkUserId(wechatworkUserId));
        
        assertEquals("WECHATWORK_USER_NOT_FOUND", exception.getErrorCode());
        assertTrue(exception.getMessage().contains("用户不存在"));
        verify(userMapper).selectByWechatworkUserId(wechatworkUserId);
    }

    @Test
    @DisplayName("保存用户信息 - 新增用户")
    void testSaveOrUpdateWechatWorkUser_NewUser() {
        // Given
        when(userMapper.selectByWechatworkUserId(testUserInfo.getWechatworkUserId())).thenReturn(null);
        when(userMapper.insert(any(WechatWorkUser.class))).thenReturn(1);

        // When
        wechatWorkUserService.saveOrUpdateWechatWorkUser(testUserInfo);

        // Then
        verify(userMapper).selectByWechatworkUserId(testUserInfo.getWechatworkUserId());
        verify(userMapper).insert(any(WechatWorkUser.class));
        verify(userMapper, never()).updateById(any(WechatWorkUser.class));
    }

    @Test
    @DisplayName("保存用户信息 - 更新用户")
    void testSaveOrUpdateWechatWorkUser_UpdateUser() {
        // Given
        when(userMapper.selectByWechatworkUserId(testUserInfo.getWechatworkUserId())).thenReturn(testUser);
        when(userMapper.updateById(any(WechatWorkUser.class))).thenReturn(1);

        // When
        wechatWorkUserService.saveOrUpdateWechatWorkUser(testUserInfo);

        // Then
        verify(userMapper).selectByWechatworkUserId(testUserInfo.getWechatworkUserId());
        verify(userMapper).updateById(any(WechatWorkUser.class));
        verify(userMapper, never()).insert(any(WechatWorkUser.class));
    }

    @Test
    @DisplayName("同步外部联系人 - 成功")
    void testSyncExternalContacts_Success() {
        // Given
        String wechatworkUserId = "test_user_id";
        String accessToken = "test_access_token";
        List<String> externalUserIds = Arrays.asList("external_user_1", "external_user_2");
        
        when(accessTokenService.getAccessToken()).thenReturn(accessToken);
        when(wechatWorkApiAdapter.getExternalContacts(accessToken, wechatworkUserId)).thenReturn(externalUserIds);

        // When
        int result = wechatWorkUserService.syncExternalContacts(wechatworkUserId);

        // Then
        assertEquals(2, result);
        verify(accessTokenService).getAccessToken();
        verify(wechatWorkApiAdapter).getExternalContacts(accessToken, wechatworkUserId);
    }

    @Test
    @DisplayName("同步外部联系人 - API调用失败")
    void testSyncExternalContacts_ApiFailure() {
        // Given
        String wechatworkUserId = "test_user_id";
        String accessToken = "test_access_token";
        
        when(accessTokenService.getAccessToken()).thenReturn(accessToken);
        when(wechatWorkApiAdapter.getExternalContacts(accessToken, wechatworkUserId))
                .thenThrow(new WechatWorkServiceException("WECHATWORK_API_ERROR", "API调用失败"));

        // When & Then
        WechatWorkServiceException exception = assertThrows(WechatWorkServiceException.class, 
                () -> wechatWorkUserService.syncExternalContacts(wechatworkUserId));
        
        assertEquals("WECHATWORK_EXTERNAL_CONTACTS_SYNC_FAILED", exception.getErrorCode());
        assertTrue(exception.getMessage().contains("同步外部联系人失败"));
        verify(accessTokenService).getAccessToken();
        verify(wechatWorkApiAdapter).getExternalContacts(accessToken, wechatworkUserId);
    }
} 
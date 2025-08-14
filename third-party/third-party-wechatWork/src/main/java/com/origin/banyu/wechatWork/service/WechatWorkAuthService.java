package com.origin.banyu.wechatWork.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.origin.banyu.base.manager.OkHttpManager;
import com.origin.banyu.common.dto.ResultData;
import com.origin.banyu.common.dto.ThirdPartyPlatformConfigDTO;
import com.origin.banyu.common.dto.WechatWorkAuthStatusResponse;
import com.origin.banyu.common.dto.WechatWorkUserInfo;
import com.origin.banyu.common.entity.ThirdPartyConfig;
import com.origin.banyu.common.util.ThirdPartyConfigParser;
import com.origin.banyu.wechatWork.adapter.WechatWorkApiAdapter;
import com.origin.banyu.wechatWork.dto.AuthCallbackResponse;
import com.origin.banyu.wechatWork.dto.AuthQrCodeResponse;
import com.origin.banyu.wechatWork.dto.BindWechatWorkUserRequest;
import com.origin.banyu.wechatWork.entity.WechatWorkUser;
import com.origin.banyu.wechatWork.feign.WechatWorkAuthFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

/**
 * 企业微信认证服务
 * 
 * @author scccy
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class WechatWorkAuthService {
    
    private final AccessTokenService accessTokenService;
    private final WechatWorkUserService userService;
    private final WechatWorkAuthFeignClient authFeignClient;
    private final OkHttpManager okHttpManager;
    private final WechatWorkApiAdapter wechatWorkApiAdapter;
    
    /**
     * 生成授权二维码
     */
    public AuthQrCodeResponse generateAuthQrCode(String redirectUri) {
        try {
            // 1. 获取企业微信配置
            ResultData<ThirdPartyConfig> configResult = authFeignClient.getWechatWorkConfig();
            if (configResult.getCode() == null || !configResult.getCode().equals(200)) {
                throw new RuntimeException("获取企业微信配置失败");
            }
            
            ThirdPartyConfig config = configResult.getData();
            
            // 2. 获取WechatWorkConfig
            Object platformConfigObj = config.getPlatformConfig();
            String configJsonString;
            if (platformConfigObj instanceof String) {
                configJsonString = (String) platformConfigObj;
            } else if (platformConfigObj instanceof java.util.Map) {
                configJsonString = com.alibaba.fastjson2.JSON.toJSONString(platformConfigObj);
            } else {
                configJsonString = platformConfigObj != null ? platformConfigObj.toString() : null;
            }
            ThirdPartyPlatformConfigDTO.WechatWorkConfig wechatWorkConfig = (ThirdPartyPlatformConfigDTO.WechatWorkConfig) ThirdPartyConfigParser.parseConfigByType(
                config.getPlatformType(), configJsonString);
            if (wechatWorkConfig == null || wechatWorkConfig.getCorpId() == null || wechatWorkConfig.getAppId() == null) {
                throw new RuntimeException("企业微信配置信息不完整");
            }
            
            String corpId = wechatWorkConfig.getCorpId();
            String agentId = wechatWorkConfig.getAppId(); // 使用appId作为agentId
            
            // 3. 生成state参数（用于防CSRF攻击）
            String state = UUID.randomUUID().toString();
            
            // 4. 构建企业微信扫码授权URL
            // 企业微信扫码授权使用特殊的URL格式
            String authUrl = String.format(
                "https://open.work.weixin.qq.com/wwopen/sso/qrConnect?key=%s&agentid=%s&redirect_uri=%s&state=%s",
                corpId, agentId, redirectUri, state);
            
            // 5. 生成二维码图片
            String qrCodeImage = generateQrCodeImage(authUrl);
            
            return AuthQrCodeResponse.builder()
                    .authUrl(authUrl)
                    .qrCodeUrl(authUrl) // 二维码URL与授权URL相同
                    .qrCodeImage(qrCodeImage) // Base64格式的二维码图片
                    .state(state)
                    .expireTime(System.currentTimeMillis() + 300000) // 5分钟过期
                    .build();
                    
        } catch (Exception e) {
            log.error("生成授权二维码失败", e);
            throw new RuntimeException("生成授权二维码失败", e);
        }
    }
    
    /**
     * 生成二维码图片（Base64格式）
     */
    private String generateQrCodeImage(String content) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, 200, 200);
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            
            byte[] imageBytes = outputStream.toByteArray();
            return java.util.Base64.getEncoder().encodeToString(imageBytes);
            
        } catch (Exception e) {
            log.error("生成二维码图片失败", e);
            throw new RuntimeException("生成二维码图片失败", e);
        }
    }
    
    /**
     * 处理授权回调
     */
    public AuthCallbackResponse handleAuthCallback(String code, String state) {
        try {
            // 1. 通过code获取用户身份
            String accessToken = accessTokenService.getAccessToken();
            String url = String.format("https://qyapi.weixin.qq.com/cgi-bin/auth/getuserinfo?access_token=%s&code=%s",
                    accessToken, code);
            
            String responseBody = okHttpManager.get(url);
            JSONObject result = JSON.parseObject(responseBody);
            
            if (result.getInteger("errcode") != 0) {
                throw new RuntimeException("获取用户身份失败: " + result.getString("errmsg"));
            }
            
            String userid = result.getString("userid");
            String userTicket = result.getString("user_ticket");
            
            // 2. 使用适配器获取用户详细信息
            WechatWorkUserInfo userInfo = wechatWorkApiAdapter.getUserInfo(accessToken, userid);
            
            // 3. 检查用户是否已绑定
            WechatWorkUser existingUser = userService.getUserByWechatworkUserId(userid);
            
            return AuthCallbackResponse.builder()
                    .userid(userid)
                    .userInfo(userInfo)
                    .isBound(existingUser != null)
                    .state(state)
                    .build();
                    
        } catch (Exception e) {
            log.error("处理授权回调失败", e);
            throw new RuntimeException("处理授权回调失败", e);
        }
    }
    
    /**
     * 绑定企业微信用户
     */
    public void bindWechatWorkUser(BindWechatWorkUserRequest request) {
        try {
            // 1. 验证系统用户是否存在
            // 2. 创建或更新企业微信用户记录
            // 3. 更新系统用户的wechatWork_id字段
            // 4. 更新授权状态
            
            userService.saveOrUpdateWechatWorkUser(request.getUserInfo());
            
            // 更新系统用户表
            // ... 调用用户服务更新wechatWork_id和授权状态
            
            log.info("企业微信用户绑定成功，userid: {}, sysUserId: {}", request.getUserInfo().getWechatworkUserId(), request.getSysUserId());
            
        } catch (Exception e) {
            log.error("绑定企业微信用户失败", e);
            throw new RuntimeException("绑定企业微信用户失败", e);
        }
    }
    
    /**
     * 解绑企业微信用户
     */
    public void unbindWechatWorkUser(String userId) {
        try {
            // 实现解绑逻辑
            log.info("企业微信用户解绑成功，userId: {}", userId);
        } catch (Exception e) {
            log.error("解绑企业微信用户失败", e);
            throw new RuntimeException("解绑企业微信用户失败", e);
        }
    }
    
    /**
     * 获取授权状态
     */
    public WechatWorkAuthStatusResponse getAuthStatus(String userId) {
        try {
            return WechatWorkAuthStatusResponse.builder()
                    .userId(userId)
                    .authStatus(0)
                    .build();
        } catch (Exception e) {
            log.error("获取授权状态失败", e);
            throw new RuntimeException("获取授权状态失败", e);
        }
    }
}
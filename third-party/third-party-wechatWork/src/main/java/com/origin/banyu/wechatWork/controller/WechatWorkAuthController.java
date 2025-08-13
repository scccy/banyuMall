package com.origin.banyu.wechatWork.controller;

import com.origin.banyu.common.dto.ResultData;
import com.origin.banyu.common.dto.WechatWorkAuthStatusResponse;
import com.origin.banyu.common.entity.ErrorCode;
import com.origin.banyu.wechatWork.dto.AuthCallbackResponse;
import com.origin.banyu.wechatWork.dto.AuthQrCodeResponse;
import com.origin.banyu.wechatWork.dto.BindWechatWorkUserRequest;
import com.origin.banyu.wechatWork.service.WechatWorkAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 企业微信授权控制器
 * 
 * @author scccy
 */
@RestController
@RequestMapping("/tp/wechatWork/auth")
@Slf4j
@RequiredArgsConstructor
public class WechatWorkAuthController {
    
    private final WechatWorkAuthService authService;
    
    /**
     * 获取授权二维码
     */
    @GetMapping("/qrcode")
    public ResultData<AuthQrCodeResponse> getAuthQrCode(@RequestParam("redirectUri") String redirectUri) {
        try {
            AuthQrCodeResponse qrCode = authService.generateAuthQrCode(redirectUri);
            return ResultData.success(qrCode);
        } catch (Exception e) {
            log.error("获取授权二维码失败", e);
            return ResultData.fail(ErrorCode.WECHATWORK_SERVICE_ERROR, "获取授权二维码失败");
        }
    }
    
    /**
     * 处理授权回调
     */
    @GetMapping("/callback")
    public ResultData<AuthCallbackResponse> handleAuthCallback(@RequestParam("code") String code,
                                                              @RequestParam("state") String state) {
        try {
            log.info("收到授权回调，code: {}, state: {}", code, state);
            
            AuthCallbackResponse response = authService.handleAuthCallback(code, state);
            return ResultData.success(response);
            
        } catch (Exception e) {
            log.error("处理授权回调失败", e);
            return ResultData.fail(ErrorCode.WECHATWORK_SERVICE_ERROR, "授权失败");
        }
    }
    
    /**
     * 绑定企业微信用户
     */
    @PostMapping("/bind")
    public ResultData<String> bindWechatWorkUser(@Valid @RequestBody BindWechatWorkUserRequest request) {
        try {
            authService.bindWechatWorkUser(request);
            return ResultData.success("绑定成功");
        } catch (Exception e) {
            log.error("绑定企业微信用户失败", e);
            return ResultData.fail(ErrorCode.WECHATWORK_SERVICE_ERROR, "绑定失败");
        }
    }
    
    /**
     * 解绑企业微信用户
     */
    @PostMapping("/unbind")
    public ResultData<String> unbindWechatWorkUser(@RequestParam("userId") String userId) {
        try {
            authService.unbindWechatWorkUser(userId);
            return ResultData.success("解绑成功");
        } catch (Exception e) {
            log.error("解绑企业微信用户失败", e);
            return ResultData.fail(ErrorCode.WECHATWORK_SERVICE_ERROR, "解绑失败");
        }
    }
    
    /**
     * 获取用户授权状态
     */
    @GetMapping("/status/{userId}")
    public ResultData<WechatWorkAuthStatusResponse> getAuthStatus(@PathVariable("userId") String userId) {
        try {
            WechatWorkAuthStatusResponse status = authService.getAuthStatus(userId);
            return ResultData.success(status);
        } catch (Exception e) {
            log.error("获取授权状态失败", e);
            return ResultData.fail(ErrorCode.WECHATWORK_SERVICE_ERROR, "获取授权状态失败");
        }
    }
} 
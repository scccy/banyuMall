package com.origin.banyu.wechatWork.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 授权二维码响应DTO
 * 
 * @author scccy
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthQrCodeResponse {
    
    /**
     * 授权URL
     */
    private String authUrl;
    
    /**
     * 授权二维码URL
     */
    private String qrCodeUrl;
    
    /**
     * 二维码图片（Base64格式）
     */
    private String qrCodeImage;
    
    /**
     * 状态参数（用于防CSRF攻击）
     */
    private String state;
    
    /**
     * 过期时间戳
     */
    private Long expireTime;
} 
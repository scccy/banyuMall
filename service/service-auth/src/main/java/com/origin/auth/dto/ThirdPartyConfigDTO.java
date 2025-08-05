package com.origin.auth.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 第三方平台配置DTO
 * 
 * @author scccy
 * @since 2025-08-05
 */
@Data
public class ThirdPartyConfigDTO {

    /**
     * 配置ID
     */
    private String configId;

    /**
     * 平台类型(123456789-企业微信/234567890-钉钉/345678901-飞书等)
     */
    private Integer platformType;

    /**
     * 平台名称
     */
    private String platformName;

    /**
     * 企业ID
     */
    private String corpId;

    /**
     * 企业密钥
     */
    private String corpSecret;

    /**
     * Token
     */
    private String token;

    /**
     * EncodingAESKey
     */
    private String encodingAesKey;

    /**
     * EchoStr
     */
    private String echoStr;

    /**
     * 应用ID
     */
    private String appId;

    /**
     * 应用密钥
     */
    private String appSecret;

    /**
     * 访问令牌
     */
    private String accessToken;

    /**
     * Webhook地址
     */
    private String webhookUrl;

    /**
     * 回调地址
     */
    private String callbackUrl;

    /**
     * 配置状态：0-禁用，1-启用
     */
    private Integer configStatus;

    /**
     * 备注说明
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;

    /**
     * 创建人ID
     */
    private String createdBy;

    /**
     * 更新人ID
     */
    private String updatedBy;
} 
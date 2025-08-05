package com.origin.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 第三方平台配置实体类
 * 
 * @author scccy
 * @since 2025-08-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("third_party_config")
public class ThirdPartyConfig {

    /**
     * 配置ID
     */
    @TableId(value = "config_id", type = IdType.AUTO)
    private Integer configId;

    /**
     * 平台类型(123456789-企业微信/234567890-钉钉/345678901-飞书等)
     */
    @TableField("platform_type")
    private Integer platformType;

    /**
     * 平台名称
     */
    @TableField("platform_name")
    private String platformName;

    /**
     * 企业ID
     */
    @TableField("corp_id")
    private String corpId;

    /**
     * 企业密钥
     */
    @TableField("corp_secret")
    private String corpSecret;

    /**
     * Token
     */
    @TableField("token")
    private String token;

    /**
     * EncodingAESKey
     */
    @TableField("encoding_aes_key")
    private String encodingAesKey;

    /**
     * EchoStr
     */
    @TableField("echo_str")
    private String echoStr;

    /**
     * 应用ID
     */
    @TableField("app_id")
    private String appId;

    /**
     * 应用密钥
     */
    @TableField("app_secret")
    private String appSecret;

    /**
     * 访问令牌
     */
    @TableField("access_token")
    private String accessToken;

    /**
     * Webhook地址
     */
    @TableField("webhook_url")
    private String webhookUrl;

    /**
     * 回调地址
     */
    @TableField("callback_url")
    private String callbackUrl;

    /**
     * 配置状态：0-禁用，1-启用
     */
    @TableField("config_status")
    private Integer configStatus;

    /**
     * 备注说明
     */
    @TableField("remark")
    private String remark;

    /**
     * 创建时间
     */
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;

    /**
     * 创建人ID
     */
    @TableField("created_by")
    private String createdBy;

    /**
     * 更新人ID
     */
    @TableField("updated_by")
    private String updatedBy;

    /**
     * 是否删除：0-未删除，1-已删除
     */
    @TableLogic
    @TableField("deleted")
    private Integer deleted;
} 
package com.origin.banyu.common.entity;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * 第三方平台配置实体类（精简版）
 * 对应表：third_party_config
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName(value = "third_party_config", autoResultMap = true)
public class ThirdPartyConfig extends BaseEntity{
    /** 配置ID */
    @TableId(value = "config_id", type = IdType.AUTO)
    private Integer configId;

    /** 平台类型 */
    @TableField("platform_type")
    private Integer platformType;

    /** 平台名称 */
    @TableField("platform_name")
    private String platformName;

    /** 平台配置信息（JSON格式） */
    @TableField(value = "platform_config")
    private Object platformConfig;

    /** 配置状态：0-禁用，1-启用 */
    @TableField("config_status")
    private Integer configStatus;

    /** 备注说明 */
    @TableField("remark")
    private String remark;


} 
package com.origin.banyu.wechatWork.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.origin.banyu.common.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 外部用户信息实体
 * 
 * @author scccy
 */
@Builder
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@TableName("external_users")
public class ExternalUser extends BaseEntity {
    
    @TableId(value = "external_user_id", type = IdType.INPUT)
    private String externalUserId;
    
    @TableField("name")
    private String name;
    
    @TableField("type")
    private Integer type;
    
    @TableField("avatar")
    private String avatar;
    
    @TableField("gender")
    private Integer gender;
    
    @TableField("unionid")
    private String unionid;
} 
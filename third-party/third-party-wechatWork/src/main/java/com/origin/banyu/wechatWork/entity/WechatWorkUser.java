package com.origin.banyu.wechatWork.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.origin.banyu.common.entity.BaseEntity;
import lombok.*;

/**
 * 企业微信用户信息实体
 * 
 * @author scccy
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("wechatwork_users")
public class WechatWorkUser extends BaseEntity {
    
    @TableId(value = "wechatwork_user_id", type = IdType.INPUT)
    private String wechatworkUserId;
    
    @TableField("name")
    private String name;
    
    @TableField("mobile")
    private String mobile;
    
    @TableField("department")
    private String department; // JSON格式存储
    
    @TableField("position")
    private String position;
    
    @TableField("gender")
    private Integer gender;
    
    @TableField("email")
    private String email;
    
    @TableField("avatar")
    private String avatar;
    
    @TableField("status")
    private Integer status;
    
    @TableField("enable")
    private Integer enable;
    
    @TableField("isleader")
    private Integer isleader;
} 
package com.origin.banyu.wechatWork.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 企业微信联系人实体
 * 对应wechatwork_contacts表
 * 注意：这是维度表，不需要继承基础父类
 * 
 * @author scccy
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wechatwork_contacts")
public class WechatworkContacts {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 企业微信用户ID
     */
    private String userid;

    /**
     * 成员名称
     */
    private String name;

    /**
     * 成员所属部门id列表（JSON格式）
     */
    private String department;

    /**
     * 职位信息
     */
    private String position;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 性别
     */
    private String gender;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 企业邮箱
     */
    private String bizMail;

    /**
     * 头像url
     */
    private String avatar;

    /**
     * 激活状态
     */
    private Integer status;

    /**
     * 成员启用状态
     */
    private Integer enable;

    /**
     * 别名
     */
    private String alias;

    /**
     * 是否是部门领导
     */
    private Integer isleader;

    /**
     * 是否隐藏手机号
     */
    private Integer hideMobile;

    /**
     * 座机
     */
    private String telephone;

    /**
     * 英文名
     */
    private String englishName;

    /**
     * 主部门
     */
    private Integer mainDepartment;

    /**
     * 员工个人二维码
     */
    private String qrCode;

    /**
     * 对外职务
     */
    private String externalPosition;

    /**
     * 对外属性（JSON格式）
     */
    private String externalProfile;

    /**
     * 全局唯一ID
     */
    private String openUserid;
}

package com.origin.banyu.wechatWork.entity;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
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

    /**
     * 从企业微信API响应创建WechatworkContacts对象
     * 静态构造方法，负责数据转换逻辑
     * 
     * @param apiResponse 企业微信API响应结果
     * @return WechatworkContacts对象
     */
    public static WechatworkContacts fromApiResponse(JSONObject apiResponse) {
        WechatworkContacts contact = new WechatworkContacts();
        contact.setUserid(apiResponse.getString("userid"));
        contact.setName(apiResponse.getString("name"));
        
        // 处理部门信息，转换为JSON字符串
        if (apiResponse.getJSONArray("department") != null) {
            contact.setDepartment(apiResponse.getJSONArray("department").toJSONString());
        }
        
        contact.setPosition(apiResponse.getString("position"));
        contact.setMobile(apiResponse.getString("mobile"));
        contact.setGender(apiResponse.getString("gender"));
        contact.setEmail(apiResponse.getString("email"));
        contact.setBizMail(apiResponse.getString("biz_mail"));
        contact.setAvatar(apiResponse.getString("avatar"));
        contact.setStatus(apiResponse.getInteger("status"));
        contact.setEnable(apiResponse.getInteger("enable"));
        contact.setAlias(apiResponse.getString("alias"));
        contact.setIsleader(apiResponse.getInteger("isleader"));
        contact.setHideMobile(apiResponse.getInteger("hide_mobile"));
        contact.setTelephone(apiResponse.getString("telephone"));
        contact.setEnglishName(apiResponse.getString("english_name"));
        contact.setMainDepartment(apiResponse.getInteger("main_department"));
        contact.setQrCode(apiResponse.getString("qr_code"));
        contact.setExternalPosition(apiResponse.getString("external_position"));
        
        // 处理对外属性，转换为JSON字符串
        if (apiResponse.getJSONObject("external_profile") != null) {
            contact.setExternalProfile(apiResponse.getJSONObject("external_profile").toJSONString());
        }
        
        contact.setOpenUserid(apiResponse.getString("open_userid"));
        
        return contact;
    }
}

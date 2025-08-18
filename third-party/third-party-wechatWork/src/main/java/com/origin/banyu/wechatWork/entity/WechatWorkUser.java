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
 * 企业微信用户实体类
 * 对应数据库表 wechatwork_user
 * 
 * @author scccy
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wechatwork_user")
public class WechatworkUser {

    /**
     * 企业微信用户ID（主键）
     */
    @TableId(value = "wechatwork_user_id", type = IdType.INPUT)
    private String wechatworkUserId;

    /**
     * 成员名称
     */
    private String name;

    /**
     * 成员所属部门id列表（JSON格式）
     */
    private String depIds;

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
     * 从企业微信API响应创建WechatworkUser对象
     * 静态构造方法，负责数据转换逻辑
     * 
     * @param apiResponse 企业微信API响应结果
     * @return WechatworkUser对象
     */
    public static WechatworkUser fromApiResponse(JSONObject apiResponse) {
        WechatworkUser user = new WechatworkUser();
        user.setWechatworkUserId(apiResponse.getString("userid"));
        user.setName(apiResponse.getString("name"));
        
        // 处理部门信息，转换为JSON字符串
        JSONArray deptArray = apiResponse.getJSONArray("department");
        if (deptArray != null) {
            user.setDepIds(deptArray.toJSONString());
        }
        
        user.setPosition(apiResponse.getString("position"));
        user.setMobile(apiResponse.getString("mobile"));
        user.setGender(apiResponse.getString("gender"));
        user.setEmail(apiResponse.getString("email"));
        user.setBizMail(apiResponse.getString("biz_mail"));
        user.setAvatar(apiResponse.getString("avatar"));
        user.setStatus(apiResponse.getInteger("status"));
        user.setEnable(apiResponse.getInteger("enable"));
        user.setAlias(apiResponse.getString("alias"));
        user.setIsleader(apiResponse.getInteger("isleader"));
        user.setHideMobile(apiResponse.getInteger("hide_mobile"));
        user.setTelephone(apiResponse.getString("telephone"));
        user.setEnglishName(apiResponse.getString("english_name"));
        user.setMainDepartment(apiResponse.getInteger("main_department"));
        user.setQrCode(apiResponse.getString("qr_code"));
        user.setExternalPosition(apiResponse.getString("external_position"));
        
        // 处理对外属性，转换为JSON字符串
        JSONObject externalProfile = apiResponse.getJSONObject("external_profile");
        if (externalProfile != null) {
            user.setExternalProfile(externalProfile.toJSONString());
        }
        
        user.setOpenUserid(apiResponse.getString("open_userid"));
        
        return user;
    }
}

package com.origin.banyu.wechatWork.dto;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

/**
 * 企业微信用户信息DTO
 * 根据企业微信官方文档：https://developer.work.weixin.qq.com/document/path/90337
 * 
 * @author scccy
 */
@Data
public class WechatWorkUserInfo {

    /**
     * 成员UserID
     */
    private String userid;

    /**
     * 成员名称
     */
    private String name;

    /**
     * 成员所属部门id列表
     */
    private JSONArray department;

    /**
     * 职位信息
     */
    private String position;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 性别。0表示未定义，1表示男性，2表示女性
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
     * 激活状态: 1=已激活，2=已禁用，4=未激活，5=退出企业
     */
    private Integer status;

    /**
     * 成员启用状态。1表示启用的成员，0表示被禁用的成员
     */
    private Integer enable;

    /**
     * 别名
     */
    private String alias;

    /**
     * 是否是部门领导。0-否；1-是
     */
    private Integer isleader;

    /**
     * 是否隐藏手机号。0-否；1-是
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
     * 对外属性
     */
    private JSONObject externalProfile;

    /**
     * 全局唯一ID
     */
    private String openUserid;

    /**
     * 从企业微信API响应创建WechatWorkUserInfo对象
     * 静态构造方法，负责数据转换逻辑
     * 
     * @param apiResponse 企业微信API响应结果
     * @return WechatWorkUserInfo对象
     */
    public static WechatWorkUserInfo fromApiResponse(JSONObject apiResponse) {
        WechatWorkUserInfo userInfo = new WechatWorkUserInfo();
        userInfo.setUserid(apiResponse.getString("userid"));
        userInfo.setName(apiResponse.getString("name"));
        userInfo.setDepartment(apiResponse.getJSONArray("department"));
        userInfo.setPosition(apiResponse.getString("position"));
        userInfo.setMobile(apiResponse.getString("mobile"));
        userInfo.setGender(apiResponse.getString("gender"));
        userInfo.setEmail(apiResponse.getString("email"));
        userInfo.setBizMail(apiResponse.getString("biz_mail"));
        userInfo.setAvatar(apiResponse.getString("avatar"));
        userInfo.setStatus(apiResponse.getInteger("status"));
        userInfo.setEnable(apiResponse.getInteger("enable"));
        userInfo.setAlias(apiResponse.getString("alias"));
        userInfo.setIsleader(apiResponse.getInteger("isleader"));
        userInfo.setHideMobile(apiResponse.getInteger("hide_mobile"));
        userInfo.setTelephone(apiResponse.getString("telephone"));
        userInfo.setEnglishName(apiResponse.getString("english_name"));
        userInfo.setMainDepartment(apiResponse.getInteger("main_department"));
        userInfo.setQrCode(apiResponse.getString("qr_code"));
        userInfo.setExternalPosition(apiResponse.getString("external_position"));
        userInfo.setExternalProfile(apiResponse.getJSONObject("external_profile"));
        
        return userInfo;
    }
}

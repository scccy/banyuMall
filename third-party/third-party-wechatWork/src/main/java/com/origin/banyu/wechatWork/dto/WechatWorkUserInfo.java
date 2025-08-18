package com.origin.banyu.wechatWork.dto;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.Data;
import com.origin.banyu.wechatWork.entity.WechatworkUser;

import java.util.List;

/**
 * 企业微信用户信息DTO
 * 对应企业微信API返回的用户信息
 * 
 * @author scccy
 */
@Data
public class WechatWorkUserInfo {

    /**
     * 企业微信用户ID
     */
    private String userid;

    /**
     * 成员名称
     */
    private String name;

    /**
     * 成员所属部门id列表
     */
    private List<Integer> depIds;

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
        
        // 处理部门信息
        JSONArray deptArray = apiResponse.getJSONArray("department");
        if (deptArray != null) {
            List<Integer> deptIds = new java.util.ArrayList<>();
            for (int i = 0; i < deptArray.size(); i++) {
                deptIds.add(deptArray.getInteger(i));
            }
            userInfo.setDepIds(deptIds);
        }
        
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
        userInfo.setOpenUserid(apiResponse.getString("open_userid"));
        
        return userInfo;
    }

    /**
     * 转换为WechatworkUser实体对象
     * 
     * @return WechatworkUser实体对象
     */
    public WechatworkUser toEntity() {
        WechatworkUser user = new WechatworkUser();
        user.setWechatworkUserId(this.userid);
        user.setName(this.name);
        
        // 处理部门信息，转换为JSON字符串
        if (this.depIds != null) {
            user.setDepIds(JSON.toJSONString(this.depIds));
        }
        
        user.setPosition(this.position);
        user.setMobile(this.mobile);
        user.setGender(this.gender);
        user.setEmail(this.email);
        user.setBizMail(this.bizMail);
        user.setAvatar(this.avatar);
        user.setStatus(this.status);
        user.setEnable(this.enable);
        user.setAlias(this.alias);
        user.setIsleader(this.isleader);
        user.setHideMobile(this.hideMobile);
        user.setTelephone(this.telephone);
        user.setEnglishName(this.englishName);
        user.setMainDepartment(this.mainDepartment);
        user.setQrCode(this.qrCode);
        user.setExternalPosition(this.externalPosition);
        
        // 处理对外属性，转换为JSON字符串
        if (this.externalProfile != null) {
            user.setExternalProfile(this.externalProfile.toJSONString());
        }
        
        user.setOpenUserid(this.openUserid);
        
        return user;
    }
}

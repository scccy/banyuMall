package com.origin.banyu.wechatWork.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.origin.banyu.wechatWork.entity.WechatWorkUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 企业微信用户Mapper接口
 * 
 * @author scccy
 */
@Mapper
public interface WechatWorkUserMapper extends BaseMapper<WechatWorkUser> {
    
    /**
     * 根据wechatwork_user_id查询用户
     * 
     * @param wechatworkUserId 企业微信用户ID
     * @return 用户信息
     */
    WechatWorkUser selectByWechatworkUserId(@Param("wechatworkUserId") String wechatworkUserId);
    
    /**
     * 根据wechatwork_user_id更新用户
     * 
     * @param user 用户信息
     * @return 更新行数
     */
    int updateByWechatworkUserId(WechatWorkUser user);
} 
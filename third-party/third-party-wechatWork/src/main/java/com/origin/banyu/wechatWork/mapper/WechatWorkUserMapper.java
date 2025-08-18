package com.origin.banyu.wechatWork.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.origin.banyu.wechatWork.entity.WechatworkUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 企业微信用户数据访问层接口
 * 对应数据库表 wechatwork_user
 * 
 * @author scccy
 */
@Mapper
public interface WechatworkUserMapper extends BaseMapper<WechatworkUser> {

    /**
     * 根据企业微信用户ID查询用户信息
     * 
     * @param wechatworkUserId 企业微信用户ID
     * @return 用户信息
     */
    WechatworkUser selectByWechatworkUserId(@Param("wechatworkUserId") String wechatworkUserId);

    /**
     * 根据部门ID查询用户列表
     * 
     * @param depId 部门ID
     * @return 用户列表
     */
    List<WechatworkUser> selectByDepId(@Param("depId") Integer depId);

    /**
     * 查询所有用户信息
     * 
     * @return 用户列表
     */
    List<WechatworkUser> selectAll();

    /**
     * 批量插入用户信息
     * 
     * @param users 用户信息列表
     * @return 插入的记录数
     */
    int batchInsert(@Param("users") List<WechatworkUser> users);

    /**
     * 根据企业微信用户ID删除用户信息
     * 
     * @param wechatworkUserId 企业微信用户ID
     * @return 删除的记录数
     */
    int deleteByWechatworkUserId(@Param("wechatworkUserId") String wechatworkUserId);

    /**
     * 根据部门ID删除用户信息
     * 
     * @param depId 部门ID
     * @return 删除的记录数
     */
    int deleteByDepId(@Param("depId") Integer depId);
}

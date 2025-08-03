package com.origin.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.origin.auth.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 系统用户Mapper接口（认证服务专用）
 * 基于简化的权限控制，专注于认证相关的用户查询
 * 
 * @author scccy
 * @since 2025-01-27
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
    
    /**
     * 根据用户名查询用户信息
     *
     * @param username 用户名
     * @return 用户信息
     */
    @Select("SELECT * FROM sys_user WHERE username = #{username} AND deleted = 0")
    SysUser selectByUsername(@Param("username") String username);
    
    /**
     * 根据手机号查询用户信息
     *
     * @param phone 手机号
     * @return 用户信息
     */
    @Select("SELECT * FROM sys_user WHERE phone = #{phone} AND deleted = 0")
    SysUser selectByPhone(@Param("phone") String phone);
    
    /**
     * 根据微信ID查询用户信息
     *
     * @param wechatId 微信ID
     * @return 用户信息
     */
    @Select("SELECT * FROM sys_user WHERE wechat_id = #{wechatId} AND deleted = 0")
    SysUser selectByWechatId(@Param("wechatId") String wechatId);
    
    /**
     * 根据有赞ID查询用户信息
     *
     * @param youzanId 有赞ID
     * @return 用户信息
     */
    @Select("SELECT * FROM sys_user WHERE youzan_id = #{youzanId} AND deleted = 0")
    SysUser selectByYouzanId(@Param("youzanId") String youzanId);
}
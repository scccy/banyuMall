package com.origin.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.origin.auth.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 系统用户Mapper接口
 * 
 * @author origin
 * @since 2024-07-30
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
    
    /**
     * 根据用户名查询用户信息
     *
     * @param username 用户名
     * @return 用户信息
     */
    @Select("SELECT * FROM sys_user WHERE username = #{username} AND is_deleted = 0")
    SysUser selectByUsername(@Param("username") String username);
}
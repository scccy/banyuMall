package com.origin.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.origin.user.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 系统用户Mapper接口
 * 
 * @author scccy
 * @since 2024-07-30
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
    
    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    SysUser selectByUsername(@Param("username") String username);
    
    /**
     * 根据手机号查询用户
     *
     * @param phone 手机号
     * @return 用户信息
     */
    SysUser selectByPhone(@Param("phone") String phone);
    
    /**
     * 分页查询用户列表
     *
     * @param page 分页参数
     * @param username 用户名（模糊查询）
     * @param nickname 昵称（模糊查询）
     * @param phone 手机号（精确查询）
     * @param email 邮箱（模糊查询）
     * @param userType 用户类型
     * @param status 状态
     * @param gender 性别
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 分页结果
     */
    IPage<SysUser> selectUserPage(Page<SysUser> page,
                                 @Param("username") String username,
                                 @Param("nickname") String nickname,
                                 @Param("phone") String phone,
                                 @Param("email") String email,
                                 @Param("userType") Integer userType,
                                 @Param("status") Integer status,
                                 @Param("gender") Integer gender,
                                 @Param("startTime") String startTime,
                                 @Param("endTime") String endTime);
} 
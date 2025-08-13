// NOTE: 按微服务边界重构，auth 不再直接访问用户数据库。
// 保留原 Mapper 以便回滚与对照，现用注释方式“删除”。
// package com.origin.banyu.auth.mapper;
//
// import com.baomidou.mybatisplus.core.mapper.BaseMapper;
// import com.origin.banyu.common.entity.SysUser;
// import org.apache.ibatis.annotations.Mapper;
// import org.apache.ibatis.annotations.Param;
// import org.apache.ibatis.annotations.Select;
//
// @Mapper
// public interface SysUserMapper extends BaseMapper<SysUser> {
//
//     @Select("SELECT * FROM sys_user WHERE username = #{username} AND deleted = 0")
//     SysUser selectByUsername(@Param("username") String username);
//
//     @Select("SELECT * FROM sys_user WHERE phone = #{phone} AND deleted = 0")
//     SysUser selectByPhone(@Param("phone") String phone);
//
//     @Select("SELECT * FROM sys_user WHERE wechat_id = #{wechatId} AND deleted = 0")
//     SysUser selectByWechatId(@Param("wechatId") String wechatId);
//
//     @Select("SELECT * FROM sys_user WHERE youzan_id = #{youzanId} AND deleted = 0")
//     SysUser selectByYouzanId(@Param("youzanId") String youzanId);
// }
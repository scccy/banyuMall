// NOTE: 按微服务边界重构，auth 不再直接访问用户数据库。
// 保留原实现以便回滚与对照，现用注释方式“删除”。
// package com.origin.banyu.auth.service.impl;
//
// import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
// import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
//
// import com.origin.banyu.auth.mapper.SysUserMapper;
// import com.origin.banyu.auth.service.SysUserService;
// import com.origin.banyu.common.entity.SysUser;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;
//
// import java.time.LocalDateTime;
//
// /**
//  * 系统用户服务实现类（认证服务专用）
//  * 重构后不再生效：用户数据访问改由调用 service-user 提供的 API 完成。
//  */
// @Slf4j
// @Service
// @RequiredArgsConstructor
// public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {
//
//     private final PasswordEncoder passwordEncoder;
//
//     @Override
//     public SysUser getByUsername(String username) {
//         LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
//         wrapper.eq(SysUser::getUsername, username);
//         return getOne(wrapper);
//     }
//
//     @Override
//     public SysUser getByPhone(String phone) {
//         LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
//         wrapper.eq(SysUser::getPhone, phone);
//         return getOne(wrapper);
//     }
//
//     @Override
//     public SysUser getByWechatId(String wechatId) {
//         LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
//         wrapper.eq(SysUser::getWechatId, wechatId);
//         return getOne(wrapper);
//     }
//
//     @Override
//     public SysUser getByYouzanId(String youzanId) {
//         LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
//         wrapper.eq(SysUser::getYouzanId, youzanId);
//         return getOne(wrapper);
//     }
//
//     @Override
//     @Transactional(rollbackFor = Exception.class)
//     public void updateLastLoginTime(String userId) {
//         SysUser user = getById(userId);
//         if (user != null) {
//             user.setLastLoginTime(LocalDateTime.now());
//             updateById(user);
//         }
//     }
//
//     @Override
//     public boolean validatePassword(String rawPassword, String encodedPassword) {
//         return passwordEncoder.matches(rawPassword, encodedPassword);
//     }
//
//     @Override
//     public String encodePassword(String rawPassword) {
//         return passwordEncoder.encode(rawPassword);
//     }
//
//     @Override
//     public boolean isUserStatusNormal(SysUser user) {
//         return user != null && user.getStatus() != null && user.getStatus() == 1;
//     }
// }
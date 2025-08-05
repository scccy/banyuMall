package com.origin.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.origin.user.entity.UserProfile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户扩展信息Mapper接口
 * 基于简化的权限控制，通过profile_id进行关联
 * 
 * @author scccy
 * @since 2025-07-31
 */
@Mapper
public interface UserProfileMapper extends BaseMapper<UserProfile> {
    
    /**
     * 根据扩展信息ID查询用户扩展信息
     *
     * @param profileId 扩展信息ID
     * @return 用户扩展信息
     */
    UserProfile selectByProfileId(@Param("profileId") String profileId);
} 
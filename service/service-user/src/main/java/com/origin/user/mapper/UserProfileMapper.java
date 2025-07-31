package com.origin.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.origin.user.entity.UserProfile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户扩展信息Mapper接口
 * 
 * @author scccy
 * @since 2024-07-30
 */
@Mapper
public interface UserProfileMapper extends BaseMapper<UserProfile> {
    
    /**
     * 根据用户ID查询扩展信息
     *
     * @param userId 用户ID
     * @return 用户扩展信息
     */
    UserProfile selectByUserId(@Param("userId") String userId);
} 
package com.origin.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.origin.user.entity.UserProfile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 用户扩展信息Mapper接口
 *
 * @author origin
 * @since 2025-01-27
 */
@Mapper
public interface UserProfileMapper extends BaseMapper<UserProfile> {

    /**
     * 根据用户ID查询扩展信息
     *
     * @param userId 用户ID
     * @return 用户扩展信息
     */
    @Select("SELECT * FROM user_profile WHERE user_id = #{userId} AND is_deleted = 0")
    UserProfile selectByUserId(@Param("userId") String userId);
} 
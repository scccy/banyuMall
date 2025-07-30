package com.origin.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.origin.user.entity.UserConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户配置Mapper接口
 *
 * @author origin
 * @since 2025-01-27
 */
@Mapper
public interface UserConfigMapper extends BaseMapper<UserConfig> {

    /**
     * 根据用户ID查询所有配置
     *
     * @param userId 用户ID
     * @return 配置列表
     */
    @Select("SELECT * FROM user_config WHERE user_id = #{userId} AND is_deleted = 0")
    List<UserConfig> selectByUserId(@Param("userId") String userId);

    /**
     * 根据用户ID和配置键查询配置
     *
     * @param userId 用户ID
     * @param configKey 配置键
     * @return 配置信息
     */
    @Select("SELECT * FROM user_config WHERE user_id = #{userId} AND config_key = #{configKey} AND is_deleted = 0")
    UserConfig selectByUserIdAndKey(@Param("userId") String userId, @Param("configKey") String configKey);
} 
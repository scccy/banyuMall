package com.origin.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.origin.auth.entity.SysRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 系统角色Mapper接口
 * 
 * @author origin
 * @since 2024-07-30
 */
@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {
    
    /**
     * 根据用户ID查询角色列表
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    @Select("SELECT r.* FROM sys_role r "
            + "INNER JOIN sys_user_role ur ON r.id = ur.role_id "
            + "WHERE ur.user_id = #{userId} AND r.status = 1 AND r.is_deleted = 0")
    List<SysRole> selectRolesByUserId(@Param("userId") String userId);
}
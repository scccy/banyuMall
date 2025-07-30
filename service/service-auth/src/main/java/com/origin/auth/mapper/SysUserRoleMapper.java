package com.origin.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.origin.auth.entity.SysUserRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户角色关联Mapper接口
 * 
 * @author origin
 * @since 2024-07-30
 */
@Mapper
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {
    
}
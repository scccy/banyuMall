package com.origin.banyu.wechatWork.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.origin.banyu.wechatWork.entity.ExternalUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 外部用户Mapper接口
 * 
 * @author scccy
 */
@Mapper
public interface ExternalUserMapper extends BaseMapper<ExternalUser> {
    
    /**
     * 根据external_user_id查询外部用户
     * 
     * @param externalUserId 外部用户ID
     * @return 外部用户信息
     */
    ExternalUser selectByExternalUserId(@Param("externalUserId") String externalUserId);
} 
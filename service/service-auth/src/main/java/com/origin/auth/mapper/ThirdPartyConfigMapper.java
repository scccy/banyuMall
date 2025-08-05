package com.origin.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.origin.common.entity.ThirdPartyConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 第三方平台配置Mapper接口
 * 
 * @author scccy
 * @since 2025-08-05
 */
@Mapper
public interface ThirdPartyConfigMapper extends BaseMapper<ThirdPartyConfig> {

    /**
     * 分页查询第三方平台配置
     * 
     * @param page 分页参数
     * @param platformType 平台类型
     * @param platformName 平台名称
     * @param configStatus 配置状态
     * @return 分页结果
     */
    IPage<ThirdPartyConfig> selectConfigPage(Page<ThirdPartyConfig> page,
                                            @Param("platformType") String platformType,
                                            @Param("platformName") String platformName,
                                            @Param("configStatus") Integer configStatus);

    /**
     * 根据平台类型查询配置
     * 
     * @param platformType 平台类型
     * @return 配置信息
     */
    ThirdPartyConfig selectByPlatformType(@Param("platformType") Integer platformType);
} 
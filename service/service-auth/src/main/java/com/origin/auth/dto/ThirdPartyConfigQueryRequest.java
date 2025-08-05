package com.origin.auth.dto;

import lombok.Data;

/**
 * 第三方平台配置查询请求DTO
 * 
 * @author scccy
 * @since 2025-08-05
 */
@Data
public class ThirdPartyConfigQueryRequest {

    /**
     * 平台类型
     */
    private String platformType;

    /**
     * 平台名称
     */
    private String platformName;

    /**
     * 配置状态：0-禁用，1-启用
     */
    private Integer configStatus;

    /**
     * 当前页码
     */
    private Integer current = 1;

    /**
     * 每页大小
     */
    private Integer size = 10;
} 
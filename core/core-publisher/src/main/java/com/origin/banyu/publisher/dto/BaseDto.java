package com.origin.banyu.publisher.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 基础DTO基类
 * 提供通用字段和验证注解
 * 作者: AI小张
 * 创建时间: 2025-08-09
 */
@Data
public abstract class BaseDto {
    
    /**
     * 任务名称
     */
    private String taskName;
    
    /**
     * 任务描述
     */
    private String taskDescription;
    
    /**
     * 任务积分
     */
    private BigDecimal taskReward;
    
    /**
     * 任务图标URL
     */
    private String taskIconUrl;
}

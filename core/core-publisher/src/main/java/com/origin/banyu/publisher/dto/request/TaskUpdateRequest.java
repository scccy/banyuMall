package com.origin.banyu.publisher.dto.request;

import com.origin.banyu.publisher.dto.BaseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 任务更新请求DTO
 * 作者: scccy
 * 创建时间: 2025-07-31
 * 重构时间: 2025-08-09
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TaskUpdateRequest extends BaseDto {
    
    /**
     * 任务特定配置
     * 以字符串形式保存JSON配置
     */
    private String taskConfig;
} 
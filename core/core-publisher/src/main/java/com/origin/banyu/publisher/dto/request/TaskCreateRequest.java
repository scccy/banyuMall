package com.origin.banyu.publisher.dto.request;

import com.origin.banyu.publisher.dto.BaseDto;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 任务创建请求DTO
 * 作者: scccy
 * 创建时间: 2025-07-31
 * 重构时间: 2025-08-09
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TaskCreateRequest extends BaseDto {
    
    /**
     * 任务类型ID
     */
    @NotNull(message = "任务类型不能为空")
    private Integer taskTypeId;
    
    /**
     * 任务特定配置
     * 以字符串形式保存JSON配置
     */
    private String taskConfig;
} 
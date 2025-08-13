package com.origin.banyu.publisher.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.origin.banyu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 基础发布者实体类
 * 提供通用字段和注解
 * 作者: AI小张
 * 创建时间: 2025-08-09
 */
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class BasePublisherEntity extends BaseEntity {
    
    /**
     * 任务ID（外键）
     */
    @TableField("task_id")
    private String taskId;
}

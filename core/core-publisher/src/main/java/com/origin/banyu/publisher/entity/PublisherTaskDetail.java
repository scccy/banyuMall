package com.origin.banyu.publisher.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 任务详情表实体
 * 作者: scccy
 * 创建时间: 2025-07-31
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("publisher_task_detail")
public class PublisherTaskDetail extends BasePublisherEntity {
    
    /**
     * 详情ID
     */
    @TableId(value = "detail_id", type = IdType.ASSIGN_ID)
    private String detailId;
    
    /**
     * 任务ID
     */
    @TableField("task_id")
    private String taskId;
    
    /**
     * 任务配置JSON
     */
    @TableField("task_config")
    private String taskConfig;
} 
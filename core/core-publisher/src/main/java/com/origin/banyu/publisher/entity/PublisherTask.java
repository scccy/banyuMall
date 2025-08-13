package com.origin.banyu.publisher.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.origin.banyu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 任务主表实体
 * 作者: scccy
 * 创建时间: 2025-07-31
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("publisher_task")
public class PublisherTask extends BaseEntity {
    
    /**
     * 任务名称
     */
    @TableField("task_name")
    private String taskName;
    
    /**
     * 任务类型ID：1-点赞，2-评论，3-讨论，4-分享，5-邀请，6-反馈，7-排行榜
     */
    @TableField("task_type_id")
    private Integer taskTypeId;
    
    /**
     * 任务描述
     */
    @TableField("task_description")
    private String taskDescription;
    
    /**
     * 任务积分
     */
    @TableField("task_reward")
    private BigDecimal taskReward;
    
    /**
     * 任务图标URL
     */
    @TableField("task_icon_url")
    private String taskIconUrl;
    
    /**
     * 详情配置ID
     */
    @TableField("detail_id")
    private String detailId;
    
    /**
     * 主键ID
     */
    @TableId(value = "task_id", type = IdType.ASSIGN_ID)
    private String taskId;
    
    /**
     * 状态ID
     */
    @TableField("status_id")
    private Integer statusId;

    /**
     * 完成人数统计（非数据库字段，计算得出）
     */
    @TableField(exist = false)
    private Integer completionCount;

} 
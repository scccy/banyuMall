-- =====================================================
-- 发布者模块数据库重新设计脚本
-- 基于MySQL数据开发规范优化
-- 作者: scccy
-- 创建时间: 2025-01-27
-- =====================================================

-- 使用数据库
USE banyu;

-- 任务类型说明（使用INT值直接映射）
-- 1: 点赞任务
-- 2: 评论任务  
-- 3: 讨论任务
-- 4: 分享任务
-- 5: 邀请任务
-- 6: 反馈任务
-- 7: 排行榜任务

-- 任务状态说明（使用INT值直接映射）
-- 1: 草稿
-- 2: 上架
-- 3: 下架
-- 4: 审核通过
-- 5: 审核不通过

-- 1. 任务主表
CREATE TABLE IF NOT EXISTS `publisher_task` (
    `task_id` VARCHAR(32) NOT NULL COMMENT '任务ID',
    `task_name` VARCHAR(100) NOT NULL COMMENT '任务名称',
    `task_type_id` INT NOT NULL COMMENT '任务类型ID：1-点赞，2-评论，3-讨论，4-分享，5-邀请，6-反馈，7-排行榜',
    `task_description` TEXT COMMENT '任务描述',
    `task_reward` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '任务积分',
    `task_icon_url` VARCHAR(500) DEFAULT NULL COMMENT '任务图标URL',
    `detail_id` VARCHAR(32) DEFAULT NULL COMMENT '详情配置ID',
    `status_id` INT NOT NULL DEFAULT 1 COMMENT '任务状态ID：1-草稿，2-上架，3-下架，4-审核通过，5-审核不通过',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `created_by` VARCHAR(32) DEFAULT NULL COMMENT '创建人ID',
    `updated_by` VARCHAR(32) DEFAULT NULL COMMENT '更新人ID',
    `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`task_id`),
    KEY `idx_task_type_id` (`task_type_id`),
    KEY `idx_status_id` (`status_id`),
    KEY `idx_detail_id` (`detail_id`),
    KEY `idx_created_time` (`created_time`),
    KEY `idx_type_status` (`task_type_id`, `status_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_bin COMMENT='任务主表';

-- 2. 任务详情表（使用JSON存储不同类型任务的配置）
CREATE TABLE IF NOT EXISTS `publisher_task_detail` (
    `detail_id` VARCHAR(32) NOT NULL COMMENT '详情ID',
    `task_id` VARCHAR(32) NOT NULL COMMENT '任务ID',
    `task_config` JSON NOT NULL COMMENT '任务配置JSON',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `created_by` VARCHAR(32) DEFAULT NULL COMMENT '创建人ID',
    `updated_by` VARCHAR(32) DEFAULT NULL COMMENT '更新人ID',
    `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`detail_id`),
    UNIQUE KEY `uk_task_id` (`task_id`),
    KEY `idx_task_id` (`task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_bin COMMENT='任务详情表';



-- 3. 社群分享审核表
CREATE TABLE IF NOT EXISTS `publisher_share_review` (
    `share_review_id` VARCHAR(32) NOT NULL COMMENT '分享审核ID',
    `task_id` VARCHAR(32) NOT NULL COMMENT '任务ID',
    `share_content` TEXT NOT NULL COMMENT '分享内容',
    `share_platform` VARCHAR(50) NOT NULL COMMENT '分享平台',
    `share_url` VARCHAR(500) COMMENT '分享链接',
    `screenshot_url` VARCHAR(500) COMMENT '截图URL',
    `reviewer_id` VARCHAR(32) COMMENT '审核人ID',
    `review_status_id` INT NOT NULL DEFAULT 1 COMMENT '审核状态ID',
    `review_comment` TEXT COMMENT '审核意见',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `created_by` VARCHAR(32) DEFAULT NULL COMMENT '创建人ID',
    `updated_by` VARCHAR(32) DEFAULT NULL COMMENT '更新人ID',
    `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`share_review_id`),
    KEY `idx_task_id` (`task_id`),
    KEY `idx_share_platform` (`share_platform`),
    KEY `idx_review_status_id` (`review_status_id`),
    KEY `idx_reviewer_id` (`reviewer_id`),
    KEY `idx_task_platform` (`task_id`, `share_platform`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_bin COMMENT='社群分享审核表';

-- 4. 任务完成流水表
CREATE TABLE IF NOT EXISTS `publisher_task_completion` (
    `completion_id` VARCHAR(32) NOT NULL COMMENT '完成记录ID',
    `task_id` VARCHAR(32) NOT NULL COMMENT '任务ID',
    `user_id` VARCHAR(32) NOT NULL COMMENT '完成用户ID',
    `completion_status` INT NOT NULL DEFAULT 1 COMMENT '完成状态：1-进行中，2-已完成，3-已拒绝',
    `completion_time` DATETIME COMMENT '完成时间',
    `reward_amount` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '获得奖励金额',
    `completion_evidence` JSON COMMENT '完成证据（截图、链接等）',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `created_by` VARCHAR(32) DEFAULT NULL COMMENT '创建人ID',
    `updated_by` VARCHAR(32) DEFAULT NULL COMMENT '更新人ID',
    `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`completion_id`),
    KEY `idx_task_id` (`task_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_completion_status` (`completion_status`),
    KEY `idx_completion_time` (`completion_time`),
    KEY `idx_task_user` (`task_id`, `user_id`),
    KEY `idx_user_status` (`user_id`, `completion_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_bin COMMENT='任务完成流水表';

-- =====================================================
-- 初始化数据
-- =====================================================





-- =====================================================
-- 表结构说明
-- =====================================================

/*
设计说明：
1. 遵循MySQL数据开发规范，使用正确的字符集和排序规则
2. 主键命名规范：{表名}_id
3. 任务类型使用INT值直接映射（1-7），任务状态使用INT值直接映射（1-5），提高查询效率
4. 不使用外键约束，应用层控制数据完整性
5. 任务详情使用JSON存储，灵活且易扩展
6. 从原来的10张表简化为5张表，提高维护性
7. 新增任务完成流水表，支持多用户完成任务
*/ 
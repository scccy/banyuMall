-- core-publisher 模块数据库脚本
-- 作者: scccy
-- 创建时间: 2025-08-01

-- 使用数据库
USE banyu_mall;

-- 1. 任务基础表
CREATE TABLE IF NOT EXISTS `publisher_task` (
    `id` VARCHAR(32) NOT NULL COMMENT '任务ID',
    `task_name` VARCHAR(100) NOT NULL COMMENT '任务名称',
    `task_type` VARCHAR(20) NOT NULL COMMENT '任务类型：LIKE-点赞,COMMENT-评论,DISCUSS-讨论,SHARE-分享,INVITE-邀请,FEEDBACK-反馈,RANKING-排行榜',
    `task_description` TEXT COMMENT '任务描述',
    `reward_amount` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '奖励金额',
    `publisher_id` VARCHAR(32) NOT NULL COMMENT '发布者ID',
    `status` VARCHAR(20) NOT NULL DEFAULT 'DRAFT' COMMENT '任务状态：DRAFT-草稿,SUBMITTED-已提交,REVIEWING-审核中,APPROVED-已通过,REJECTED-已拒绝,ACTIVE-进行中,COMPLETED-已完成,CANCELLED-已取消',
    `start_time` DATETIME NOT NULL COMMENT '开始时间',
    `end_time` DATETIME NOT NULL COMMENT '结束时间',
    `max_participants` INT NOT NULL DEFAULT 0 COMMENT '最大参与人数',
    `current_participants` INT NOT NULL DEFAULT 0 COMMENT '当前参与人数',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_publisher_id` (`publisher_id`),
    KEY `idx_task_type` (`task_type`),
    KEY `idx_status` (`status`),
    KEY `idx_start_time` (`start_time`),
    KEY `idx_end_time` (`end_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任务基础表';

-- 2. 点赞任务表
CREATE TABLE IF NOT EXISTS `publisher_like_task` (
    `id` VARCHAR(32) NOT NULL COMMENT 'ID',
    `task_id` VARCHAR(32) NOT NULL COMMENT '任务ID',
    `target_url` VARCHAR(500) NOT NULL COMMENT '目标URL',
    `like_count` INT NOT NULL DEFAULT 1 COMMENT '点赞数量',
    `comment_required` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否需要评论：0-不需要，1-需要',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_task_id` (`task_id`),
    KEY `idx_target_url` (`target_url`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='点赞任务表';

-- 3. 评论任务表
CREATE TABLE IF NOT EXISTS `publisher_comment_task` (
    `id` VARCHAR(32) NOT NULL COMMENT 'ID',
    `task_id` VARCHAR(32) NOT NULL COMMENT '任务ID',
    `target_url` VARCHAR(500) NOT NULL COMMENT '目标URL',
    `comment_template` TEXT COMMENT '评论模板',
    `min_comment_length` INT NOT NULL DEFAULT 10 COMMENT '最小评论长度',
    `max_comment_length` INT NOT NULL DEFAULT 500 COMMENT '最大评论长度',
    `comment_count` INT NOT NULL DEFAULT 1 COMMENT '评论数量',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_task_id` (`task_id`),
    KEY `idx_target_url` (`target_url`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论任务表';

-- 4. 讨论任务表
CREATE TABLE IF NOT EXISTS `publisher_discuss_task` (
    `id` VARCHAR(32) NOT NULL COMMENT 'ID',
    `task_id` VARCHAR(32) NOT NULL COMMENT '任务ID',
    `discuss_topic` VARCHAR(200) NOT NULL COMMENT '讨论主题',
    `discuss_content` TEXT COMMENT '讨论内容',
    `discuss_platform` VARCHAR(50) NOT NULL COMMENT '讨论平台',
    `discuss_url` VARCHAR(500) COMMENT '讨论链接',
    `min_discuss_length` INT NOT NULL DEFAULT 20 COMMENT '最小讨论长度',
    `discuss_count` INT NOT NULL DEFAULT 1 COMMENT '讨论数量',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_task_id` (`task_id`),
    KEY `idx_discuss_topic` (`discuss_topic`),
    KEY `idx_discuss_platform` (`discuss_platform`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='讨论任务表';

-- 5. 社群分享任务表
CREATE TABLE IF NOT EXISTS `publisher_share_task` (
    `id` VARCHAR(32) NOT NULL COMMENT 'ID',
    `task_id` VARCHAR(32) NOT NULL COMMENT '任务ID',
    `share_content` TEXT NOT NULL COMMENT '分享内容',
    `share_platform` VARCHAR(50) NOT NULL COMMENT '分享平台',
    `share_url` VARCHAR(500) COMMENT '分享链接',
    `screenshot_required` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否需要截图：0-不需要，1-需要',
    `share_count` INT NOT NULL DEFAULT 1 COMMENT '分享数量',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_task_id` (`task_id`),
    KEY `idx_share_platform` (`share_platform`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='社群分享任务表';

-- 6. 邀请任务表
CREATE TABLE IF NOT EXISTS `publisher_invite_task` (
    `id` VARCHAR(32) NOT NULL COMMENT 'ID',
    `task_id` VARCHAR(32) NOT NULL COMMENT '任务ID',
    `invite_platform` VARCHAR(50) NOT NULL COMMENT '邀请平台',
    `invite_link` VARCHAR(500) COMMENT '邀请链接',
    `invite_code` VARCHAR(50) COMMENT '邀请码',
    `invite_count` INT NOT NULL DEFAULT 1 COMMENT '邀请数量',
    `invite_reward` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '邀请奖励',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_task_id` (`task_id`),
    KEY `idx_invite_platform` (`invite_platform`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='邀请任务表';

-- 7. 反馈任务表
CREATE TABLE IF NOT EXISTS `publisher_feedback_task` (
    `id` VARCHAR(32) NOT NULL COMMENT 'ID',
    `task_id` VARCHAR(32) NOT NULL COMMENT '任务ID',
    `feedback_type` VARCHAR(50) NOT NULL COMMENT '反馈类型',
    `feedback_platform` VARCHAR(50) NOT NULL COMMENT '反馈平台',
    `feedback_content` TEXT COMMENT '反馈内容模板',
    `feedback_url` VARCHAR(500) COMMENT '反馈链接',
    `feedback_count` INT NOT NULL DEFAULT 1 COMMENT '反馈数量',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_task_id` (`task_id`),
    KEY `idx_feedback_type` (`feedback_type`),
    KEY `idx_feedback_platform` (`feedback_platform`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='反馈任务表';

-- 8. 排行榜任务表
CREATE TABLE IF NOT EXISTS `publisher_ranking_task` (
    `id` VARCHAR(32) NOT NULL COMMENT 'ID',
    `task_id` VARCHAR(32) NOT NULL COMMENT '任务ID',
    `ranking_platform` VARCHAR(50) NOT NULL COMMENT '排行榜平台',
    `ranking_type` VARCHAR(50) NOT NULL COMMENT '排行榜类型',
    `ranking_url` VARCHAR(500) COMMENT '排行榜链接',
    `target_ranking` INT NOT NULL DEFAULT 1 COMMENT '目标排名',
    `ranking_reward` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '排名奖励',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_task_id` (`task_id`),
    KEY `idx_ranking_platform` (`ranking_platform`),
    KEY `idx_ranking_type` (`ranking_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='排行榜任务表';

-- 9. 任务审核记录表
CREATE TABLE IF NOT EXISTS `publisher_task_review` (
    `id` VARCHAR(32) NOT NULL COMMENT 'ID',
    `task_id` VARCHAR(32) NOT NULL COMMENT '任务ID',
    `reviewer_id` VARCHAR(32) NOT NULL COMMENT '审核人ID',
    `review_status` VARCHAR(20) NOT NULL COMMENT '审核状态：PENDING-待审核,APPROVED-通过,REJECTED-拒绝',
    `review_comment` TEXT COMMENT '审核意见',
    `review_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '审核时间',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_task_id` (`task_id`),
    KEY `idx_reviewer_id` (`reviewer_id`),
    KEY `idx_review_status` (`review_status`),
    KEY `idx_review_time` (`review_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任务审核记录表';

-- 10. 社群分享审核表
CREATE TABLE IF NOT EXISTS `publisher_share_review` (
    `id` VARCHAR(32) NOT NULL COMMENT 'ID',
    `task_id` VARCHAR(32) NOT NULL COMMENT '任务ID',
    `share_content` TEXT NOT NULL COMMENT '分享内容',
    `share_platform` VARCHAR(50) NOT NULL COMMENT '分享平台',
    `share_url` VARCHAR(500) COMMENT '分享链接',
    `screenshot_url` VARCHAR(500) COMMENT '截图URL',
    `reviewer_id` VARCHAR(32) COMMENT '审核人ID',
    `review_status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '审核状态：PENDING-待审核,APPROVED-通过,REJECTED-拒绝',
    `review_comment` TEXT COMMENT '审核意见',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_task_id` (`task_id`),
    KEY `idx_share_platform` (`share_platform`),
    KEY `idx_review_status` (`review_status`),
    KEY `idx_reviewer_id` (`reviewer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='社群分享审核表';

-- 添加外键约束
ALTER TABLE `publisher_like_task` ADD CONSTRAINT `fk_like_task_id` FOREIGN KEY (`task_id`) REFERENCES `publisher_task` (`id`) ON DELETE CASCADE;
ALTER TABLE `publisher_comment_task` ADD CONSTRAINT `fk_comment_task_id` FOREIGN KEY (`task_id`) REFERENCES `publisher_task` (`id`) ON DELETE CASCADE;
ALTER TABLE `publisher_discuss_task` ADD CONSTRAINT `fk_discuss_task_id` FOREIGN KEY (`task_id`) REFERENCES `publisher_task` (`id`) ON DELETE CASCADE;
ALTER TABLE `publisher_share_task` ADD CONSTRAINT `fk_share_task_id` FOREIGN KEY (`task_id`) REFERENCES `publisher_task` (`id`) ON DELETE CASCADE;
ALTER TABLE `publisher_invite_task` ADD CONSTRAINT `fk_invite_task_id` FOREIGN KEY (`task_id`) REFERENCES `publisher_task` (`id`) ON DELETE CASCADE;
ALTER TABLE `publisher_feedback_task` ADD CONSTRAINT `fk_feedback_task_id` FOREIGN KEY (`task_id`) REFERENCES `publisher_task` (`id`) ON DELETE CASCADE;
ALTER TABLE `publisher_ranking_task` ADD CONSTRAINT `fk_ranking_task_id` FOREIGN KEY (`task_id`) REFERENCES `publisher_task` (`id`) ON DELETE CASCADE;
ALTER TABLE `publisher_task_review` ADD CONSTRAINT `fk_task_review_task_id` FOREIGN KEY (`task_id`) REFERENCES `publisher_task` (`id`) ON DELETE CASCADE;
ALTER TABLE `publisher_share_review` ADD CONSTRAINT `fk_share_review_task_id` FOREIGN KEY (`task_id`) REFERENCES `publisher_task` (`id`) ON DELETE CASCADE; 
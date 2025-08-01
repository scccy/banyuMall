-- 发布者模块数据库表结构
-- 创建时间: 2025-01-15
-- 模块: core-publisher

-- 1. 任务基础表
CREATE TABLE publisher_task (
    id VARCHAR(32) PRIMARY KEY COMMENT '任务ID',
    task_name VARCHAR(100) NOT NULL COMMENT '任务名称',
    task_description TEXT COMMENT '任务描述',
    task_points INT NOT NULL DEFAULT 0 COMMENT '任务积分',
    task_icon VARCHAR(255) COMMENT '任务图标URL',
    task_type TINYINT NOT NULL COMMENT '任务类型：1-点赞/2-评论/3-讨论/4-社群分享/5-邀请/6-反馈/7-排行榜',
    publisher_id VARCHAR(32) NOT NULL COMMENT '发布者ID',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '任务状态：1-草稿/2-已提交/3-已审核/4-已拒绝/5-已发布/6-已下架',
    review_status TINYINT DEFAULT 1 COMMENT '审核状态：1-待审核/2-已通过/3-已拒绝',
    review_comment TEXT COMMENT '审核意见',
    participant_count INT DEFAULT 0 COMMENT '参与人数',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_publisher_id (publisher_id),
    INDEX idx_task_type (task_type),
    INDEX idx_status (status),
    INDEX idx_review_status (review_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务基础表';

-- 2. 点赞任务表
CREATE TABLE publisher_like_task (
    id VARCHAR(32) PRIMARY KEY COMMENT '点赞任务ID',
    task_id VARCHAR(32) NOT NULL COMMENT '关联任务ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (task_id) REFERENCES publisher_task(id) ON DELETE CASCADE,
    INDEX idx_task_id (task_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='点赞任务表';

-- 3. 评论任务表
CREATE TABLE publisher_comment_task (
    id VARCHAR(32) PRIMARY KEY COMMENT '评论任务ID',
    task_id VARCHAR(32) NOT NULL COMMENT '关联任务ID',
    task_instruction TEXT COMMENT '任务说明',
    require_image TINYINT(1) DEFAULT 0 COMMENT '是否上传图片：0-否，1-是',
    min_image_count INT DEFAULT 0 COMMENT '最少图片数量',
    image_urls JSON COMMENT '图片地址列表',
    require_share_link TINYINT(1) DEFAULT 0 COMMENT '是否填写分享链接：0-否，1-是',
    share_result_urls JSON COMMENT '社群分享结果链接列表',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (task_id) REFERENCES publisher_task(id) ON DELETE CASCADE,
    INDEX idx_task_id (task_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论任务表';

-- 4. 讨论任务表
CREATE TABLE publisher_discuss_task (
    id VARCHAR(32) PRIMARY KEY COMMENT '讨论任务ID',
    task_id VARCHAR(32) NOT NULL COMMENT '关联任务ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (task_id) REFERENCES publisher_task(id) ON DELETE CASCADE,
    INDEX idx_task_id (task_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='讨论任务表';

-- 5. 社群分享任务表
CREATE TABLE publisher_share_task (
    id VARCHAR(32) PRIMARY KEY COMMENT '社群分享任务ID',
    task_id VARCHAR(32) NOT NULL COMMENT '关联任务ID',
    invite_type TINYINT NOT NULL COMMENT '邀请类型：1-入群/2-加微信',
    invitee_reward TINYINT(1) DEFAULT 0 COMMENT '被邀请人是否增加积分：0-否，1-是',
    invitee_points INT DEFAULT 0 COMMENT '被邀请人增加积分数量',
    activity_rules TEXT COMMENT '活动规则',
    qr_code VARCHAR(255) COMMENT '二维码URL',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (task_id) REFERENCES publisher_task(id) ON DELETE CASCADE,
    INDEX idx_task_id (task_id),
    INDEX idx_invite_type (invite_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='社群分享任务表';

-- 6. 邀请任务表
CREATE TABLE publisher_invite_task (
    id VARCHAR(32) PRIMARY KEY COMMENT '邀请任务ID',
    task_id VARCHAR(32) NOT NULL COMMENT '关联任务ID',
    invite_type TINYINT NOT NULL COMMENT '邀请类型：1-入群/2-加微信',
    invitee_reward TINYINT(1) DEFAULT 0 COMMENT '被邀请人是否增加积分：0-否，1-是',
    invitee_points INT DEFAULT 0 COMMENT '被邀请人增加积分数量',
    activity_rules TEXT COMMENT '活动规则',
    qr_code_url VARCHAR(255) COMMENT '二维码地址',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (task_id) REFERENCES publisher_task(id) ON DELETE CASCADE,
    INDEX idx_task_id (task_id),
    INDEX idx_invite_type (invite_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='邀请任务表';

-- 7. 反馈任务表
CREATE TABLE publisher_feedback_task (
    id VARCHAR(32) PRIMARY KEY COMMENT '反馈任务ID',
    task_id VARCHAR(32) NOT NULL COMMENT '关联任务ID',
    feedback_content TEXT COMMENT '反馈文字或反馈URL',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (task_id) REFERENCES publisher_task(id) ON DELETE CASCADE,
    INDEX idx_task_id (task_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='反馈任务表';

-- 8. 排行榜任务表
CREATE TABLE publisher_ranking_task (
    id VARCHAR(32) PRIMARY KEY COMMENT '排行榜任务ID',
    task_id VARCHAR(32) NOT NULL COMMENT '关联任务ID',
    ranking_top_count INT DEFAULT 10 COMMENT '排行榜展示积分排名前X位置',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (task_id) REFERENCES publisher_task(id) ON DELETE CASCADE,
    INDEX idx_task_id (task_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='排行榜任务表';

-- 9. 任务审核记录表
CREATE TABLE publisher_task_review (
    id VARCHAR(32) PRIMARY KEY COMMENT '审核记录ID',
    task_id VARCHAR(32) NOT NULL COMMENT '任务ID',
    reviewer_id VARCHAR(32) NOT NULL COMMENT '审核员ID',
    review_status TINYINT NOT NULL COMMENT '审核状态：2-已通过/3-已拒绝',
    review_comment TEXT COMMENT '审核意见',
    review_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '审核时间',
    review_type VARCHAR(20) DEFAULT 'INITIAL' COMMENT '审核类型：INITIAL/REVISION',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (task_id) REFERENCES publisher_task(id) ON DELETE CASCADE,
    INDEX idx_task_id (task_id),
    INDEX idx_reviewer_id (reviewer_id),
    INDEX idx_review_status (review_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务审核记录表';

-- 10. 社群分享审核表
CREATE TABLE publisher_share_review (
    id VARCHAR(32) PRIMARY KEY COMMENT '分享审核ID',
    share_task_id VARCHAR(32) NOT NULL COMMENT '社群分享任务ID',
    activity_id VARCHAR(32) NOT NULL COMMENT '活动ID',
    activity_name VARCHAR(100) NOT NULL COMMENT '活动名称',
    user_id VARCHAR(32) NOT NULL COMMENT '用户ID',
    wechat_nickname VARCHAR(50) COMMENT '微信昵称',
    review_status TINYINT NOT NULL DEFAULT 1 COMMENT '审核状态：1-待审核/2-已通过/3-已拒绝',
    review_comment TEXT COMMENT '审核意见',
    points_awarded INT DEFAULT 0 COMMENT '发放积分数量',
    reviewer_id VARCHAR(32) COMMENT '审核员ID',
    review_time DATETIME COMMENT '审核时间',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (share_task_id) REFERENCES publisher_share_task(id) ON DELETE CASCADE,
    INDEX idx_share_task_id (share_task_id),
    INDEX idx_activity_id (activity_id),
    INDEX idx_user_id (user_id),
    INDEX idx_review_status (review_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='社群分享审核表';

-- 11. 任务参与记录表
CREATE TABLE publisher_task_participation (
    id VARCHAR(32) PRIMARY KEY COMMENT '参与记录ID',
    task_id VARCHAR(32) NOT NULL COMMENT '任务ID',
    user_id VARCHAR(32) NOT NULL COMMENT '用户ID',
    participation_status TINYINT NOT NULL DEFAULT 1 COMMENT '参与状态：1-进行中/2-已完成/3-已失败',
    points_earned INT DEFAULT 0 COMMENT '获得积分',
    completion_time DATETIME COMMENT '完成时间',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (task_id) REFERENCES publisher_task(id) ON DELETE CASCADE,
    INDEX idx_task_id (task_id),
    INDEX idx_user_id (user_id),
    INDEX idx_participation_status (participation_status),
    UNIQUE KEY uk_task_user (task_id, user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务参与记录表'; 
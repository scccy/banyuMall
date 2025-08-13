-- 企业微信用户信息表
CREATE TABLE `wechatwork_users` (
    `wechatwork_users_id` VARCHAR(64) NOT NULL COMMENT '企业微信用户ID',
    `user_id` VARCHAR(32) DEFAULT NULL COMMENT '关联的系统用户ID',
    `name` VARCHAR(50) DEFAULT NULL COMMENT '用户姓名',
    `mobile` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `department` JSON DEFAULT NULL COMMENT '部门ID列表',
    `position` VARCHAR(50) DEFAULT NULL COMMENT '职位',
    `gender` TINYINT(1) DEFAULT 0 COMMENT '性别：0-未知，1-男，2-女',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `avatar` VARCHAR(500) DEFAULT NULL COMMENT '头像URL',
    `status` TINYINT(1) DEFAULT 1 COMMENT '状态：1-已激活，2-已禁用，4-未激活，5-退出企业',
    `enable` TINYINT(1) DEFAULT 1 COMMENT '是否启用：1-启用，0-禁用',
    `isleader` TINYINT(1) DEFAULT 0 COMMENT '是否部门领导：1-是，0-否',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `created_by` VARCHAR(32) DEFAULT NULL COMMENT '创建人ID',
    `updated_by` VARCHAR(32) DEFAULT NULL COMMENT '更新人ID',
    `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`wechatwork_users_id`),
    UNIQUE KEY `uk_userid` (`userid`),
    UNIQUE KEY `uk_sys_user_id` (`sys_user_id`),
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='企业微信用户信息表';

-- 企业微信外部用户信息表
CREATE TABLE `external_users` (
    `external_users_id` VARCHAR(32) NOT NULL COMMENT '企业微信外部用户ID',
    `name` VARCHAR(50) DEFAULT NULL COMMENT '用户姓名',
    `type` TINYINT(1) DEFAULT 1 COMMENT '用户类型：1-微信用户，2-企业微信用户',
    `avatar` VARCHAR(500) DEFAULT NULL COMMENT '头像URL',
    `gender` TINYINT(1) DEFAULT 0 COMMENT '性别：0-未知，1-男，2-女',
    `unionid` VARCHAR(64) DEFAULT NULL COMMENT '微信unionid',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `created_by` VARCHAR(32) DEFAULT NULL COMMENT '创建人ID',
    `updated_by` VARCHAR(32) DEFAULT NULL COMMENT '更新人ID',
    `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`external_users_id`),
    UNIQUE KEY `uk_external_userid` (`external_userid`),
    KEY `idx_name` (`name`),
    KEY `idx_unionid` (`unionid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='企业微信外部用户信息表';

-- 客户联系人关系表
CREATE TABLE `wechatwork_contacts` (
    `wechatwork_users_id` VARCHAR(32) NOT NULL COMMENT '发布者企业微信用户ID',
    `external_user_id` VARCHAR(64) NOT NULL COMMENT '客户外部用户ID',
    `remark` VARCHAR(100) DEFAULT NULL COMMENT '备注',
    `description` VARCHAR(500) DEFAULT NULL COMMENT '描述',
    `createtime` BIGINT DEFAULT NULL COMMENT '创建时间戳',
    `tag_id` JSON DEFAULT NULL COMMENT '标签ID列表',
    `state` VARCHAR(50) DEFAULT NULL COMMENT '自定义状态',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `created_by` VARCHAR(32) DEFAULT NULL COMMENT '创建人ID',
    `updated_by` VARCHAR(32) DEFAULT NULL COMMENT '更新人ID',
    `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`wechatwork_contacts_id`),
    UNIQUE KEY `uk_wechatwork_user_external` (`wechatwork_users_id`, `external_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='企业微信客户联系人关系表';

-- 群聊成员关系表
CREATE TABLE `wechatwork_group_members` (
    `wechatwork_group_members_id` VARCHAR(32) NOT NULL COMMENT '群聊ID',
     `wechatwork_users_id` VARCHAR(64) NOT NULL COMMENT '企业微信用户ID,群主id',
    `external_user_id` VARCHAR(64) NOT NULL COMMENT '外部用户ID',
    `type` TINYINT(1) DEFAULT 1 COMMENT '成员类型：1-企业成员，2-外部联系人',
    `join_time` BIGINT DEFAULT NULL COMMENT '入群时间戳',
    `group_nickname` VARCHAR(50) DEFAULT NULL COMMENT '群昵称',
    `name` VARCHAR(50) DEFAULT NULL COMMENT '姓名',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `created_by` VARCHAR(32) DEFAULT NULL COMMENT '创建人ID',
    `updated_by` VARCHAR(32) DEFAULT NULL COMMENT '更新人ID',
    `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`wechatwork_group_members_id`),
    UNIQUE KEY `uk_chat_external_user` (`wechatwork_group_members_id`, `external_userid`),
    KEY `idx_chat_id` (`chat_id`),
    KEY `idx_external_userid` (`external_userid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='企业微信群聊成员关系表';

-- 朋友圈动态表
CREATE TABLE `wechatwork_moments` (
    `wechatwork_users_id` VARCHAR(32) NOT NULL COMMENT '主键ID',
    `moment_id` VARCHAR(64) NOT NULL COMMENT '朋友圈动态ID',
    `content` TEXT DEFAULT NULL COMMENT '动态内容',
    `media_urls` JSON DEFAULT NULL COMMENT '媒体文件URL列表',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `created_by` VARCHAR(32) DEFAULT NULL COMMENT '创建人ID',
    `updated_by` VARCHAR(32) DEFAULT NULL COMMENT '更新人ID',
    `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`wechatwork_moments_id`),
    UNIQUE KEY `uk_moment_id` (`moment_id`),
    KEY `idx_wechatwork_users_id` (`wechatwork_users_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='企业微信朋友圈动态表';

-- 朋友圈互动表
CREATE TABLE `wechatwork_moment_interactions` (
    `moment_id` VARCHAR(64) NOT NULL COMMENT '朋友圈动态ID',
    `wechatwork_users_id` VARCHAR(32) NOT NULL COMMENT '发布者企业微信用户ID',
    `external_userid` VARCHAR(64) NOT NULL COMMENT '互动外部用户ID',
    `interaction_type` TINYINT(1) NOT NULL COMMENT '互动类型：1-点赞，2-评论',
    `comment_content` TEXT DEFAULT NULL COMMENT '评论内容',
    `interaction_time` BIGINT DEFAULT NULL COMMENT '互动时间戳',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `created_by` VARCHAR(32) DEFAULT NULL COMMENT '创建人ID',
    `updated_by` VARCHAR(32) DEFAULT NULL COMMENT '更新人ID',
    `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`wechatwork_moment_interactions_id`),
    UNIQUE KEY `uk_moment_user_interaction` (`moment_id`, `external_userid`, `interaction_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='企业微信朋友圈互动表'; 
-- 统一用户数据库表结构
-- 创建时间: 2025-01-27
-- 说明: 供service-auth和service-user共同使用的数据库表结构

-- 系统用户表（共享表）
CREATE TABLE IF NOT EXISTS `sys_user` (
    `id` varchar(32) NOT NULL COMMENT '用户ID',
    `username` varchar(50) NOT NULL COMMENT '用户名',
    `password` varchar(255) NOT NULL COMMENT '密码（加密）',
    `nickname` varchar(100) DEFAULT NULL COMMENT '昵称',
    `avatar` varchar(500) DEFAULT NULL COMMENT '头像URL',
    `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
    `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
    `gender` tinyint(1) DEFAULT 0 COMMENT '性别：0-未知，1-男，2-女',
    `birthday` date DEFAULT NULL COMMENT '生日',
    `status` tinyint(1) DEFAULT 1 COMMENT '状态：0-禁用，1-正常，2-待审核，3-已删除',
    `user_type` tinyint(1) DEFAULT 2 COMMENT '用户类型：1-最高权限，2-普通发布者',
    `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
    `create_by` varchar(32) DEFAULT NULL COMMENT '创建人ID',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` varchar(32) DEFAULT NULL COMMENT '更新人ID',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` tinyint(1) DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_email` (`email`),
    UNIQUE KEY `uk_phone` (`phone`),
    KEY `idx_status` (`status`),
    KEY `idx_user_type` (`user_type`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统用户表';

-- 系统角色表
CREATE TABLE IF NOT EXISTS `sys_role` (
    `id` varchar(32) NOT NULL COMMENT '角色ID',
    `role_name` varchar(50) NOT NULL COMMENT '角色名称',
    `role_code` varchar(50) NOT NULL COMMENT '角色编码',
    `description` varchar(200) DEFAULT NULL COMMENT '角色描述',
    `status` tinyint(1) DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    `create_by` varchar(32) DEFAULT NULL COMMENT '创建人ID',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` varchar(32) DEFAULT NULL COMMENT '更新人ID',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` tinyint(1) DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_code` (`role_code`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统角色表';

-- 系统权限表
CREATE TABLE IF NOT EXISTS `sys_permission` (
    `id` varchar(32) NOT NULL COMMENT '权限ID',
    `permission_name` varchar(50) NOT NULL COMMENT '权限名称',
    `permission_code` varchar(50) NOT NULL COMMENT '权限编码',
    `permission_type` tinyint(1) DEFAULT 1 COMMENT '权限类型：1-菜单，2-按钮，3-接口',
    `parent_id` varchar(32) DEFAULT NULL COMMENT '父权限ID',
    `path` varchar(200) DEFAULT NULL COMMENT '路径',
    `component` varchar(200) DEFAULT NULL COMMENT '组件',
    `icon` varchar(100) DEFAULT NULL COMMENT '图标',
    `sort_order` int(11) DEFAULT 0 COMMENT '排序',
    `status` tinyint(1) DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    `create_by` varchar(32) DEFAULT NULL COMMENT '创建人ID',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` varchar(32) DEFAULT NULL COMMENT '更新人ID',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` tinyint(1) DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_permission_code` (`permission_code`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_status` (`status`),
    KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统权限表';

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS `sys_user_role` (
    `id` varchar(32) NOT NULL COMMENT '关联ID',
    `user_id` varchar(32) NOT NULL COMMENT '用户ID',
    `role_id` varchar(32) NOT NULL COMMENT '角色ID',
    `create_by` varchar(32) DEFAULT NULL COMMENT '创建人ID',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` varchar(32) DEFAULT NULL COMMENT '更新人ID',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` tinyint(1) DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_role` (`user_id`, `role_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- 角色权限关联表
CREATE TABLE IF NOT EXISTS `sys_role_permission` (
    `id` varchar(32) NOT NULL COMMENT '关联ID',
    `role_id` varchar(32) NOT NULL COMMENT '角色ID',
    `permission_id` varchar(32) NOT NULL COMMENT '权限ID',
    `create_by` varchar(32) DEFAULT NULL COMMENT '创建人ID',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` varchar(32) DEFAULT NULL COMMENT '更新人ID',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` tinyint(1) DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_permission` (`role_id`, `permission_id`),
    KEY `idx_role_id` (`role_id`),
    KEY `idx_permission_id` (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联表';

-- 用户扩展信息表（service-user管理）
CREATE TABLE IF NOT EXISTS `user_profile` (
    `id` varchar(32) NOT NULL COMMENT '扩展信息ID',
    `user_id` varchar(32) NOT NULL COMMENT '用户ID',
    `real_name` varchar(50) DEFAULT NULL COMMENT '真实姓名',
    `id_card` varchar(18) DEFAULT NULL COMMENT '身份证号',
    `address` varchar(500) DEFAULT NULL COMMENT '地址',
    `contact_phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
    `industry` varchar(50) DEFAULT NULL COMMENT '所属行业',
    `description` text DEFAULT NULL COMMENT '个人简介',
    `create_by` varchar(32) DEFAULT NULL COMMENT '创建人ID',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` varchar(32) DEFAULT NULL COMMENT '更新人ID',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` tinyint(1) DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`),
    KEY `idx_real_name` (`real_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户扩展信息表';

-- 用户配置表（service-user管理）
CREATE TABLE IF NOT EXISTS `user_config` (
    `id` varchar(32) NOT NULL COMMENT '配置ID',
    `user_id` varchar(32) NOT NULL COMMENT '用户ID',
    `config_key` varchar(100) NOT NULL COMMENT '配置键',
    `config_value` text DEFAULT NULL COMMENT '配置值',
    `config_type` varchar(20) DEFAULT 'string' COMMENT '配置类型：string、number、boolean、json',
    `description` varchar(200) DEFAULT NULL COMMENT '配置描述',
    `create_by` varchar(32) DEFAULT NULL COMMENT '创建人ID',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` varchar(32) DEFAULT NULL COMMENT '更新人ID',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` tinyint(1) DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_config` (`user_id`, `config_key`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户配置表'; 
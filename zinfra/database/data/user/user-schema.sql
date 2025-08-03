-- =====================================================
-- 用户模块数据库设计脚本
-- 基于MySQL数据开发规范优化
-- 作者: scccy
-- 创建时间: 2025-01-27
-- =====================================================

-- 使用数据库
USE banyu;

-- 用户类型说明（使用INT值直接映射）
-- 1: 系统管理员（最高权限）
-- 2: 发布者（可以发布任务）
-- 3: 接受者（可以接受任务）

-- 用户状态说明（使用INT值直接映射）
-- 1: 正常
-- 2: 禁用
-- 3: 待审核
-- 4: 已删除

-- 性别说明（使用INT值直接映射）
-- 0: 未知
-- 1: 男
-- 2: 女

-- 1. 系统用户表（核心表）
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
    `user_id` VARCHAR(32) NOT NULL COMMENT '用户ID',
    `phone` VARCHAR(20) NOT NULL COMMENT '手机号',
    `wechat_id` VARCHAR(255) DEFAULT NULL COMMENT '微信用户ID',
    `youzan_id` VARCHAR(255) DEFAULT NULL COMMENT '有赞用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码(BCrypt加密)',
    `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
    `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `gender` INT NOT NULL DEFAULT 0 COMMENT '性别：0-未知，1-男，2-女',
    `birthday` DATE DEFAULT NULL COMMENT '生日',
    `status` INT NOT NULL DEFAULT 1 COMMENT '状态：1-正常，2-禁用，3-待审核，4-已删除',
    `user_type` INT NOT NULL DEFAULT 2 COMMENT '用户类型：1-系统管理员，2-发布者，3-接受者',
    `last_login_time` DATETIME DEFAULT NULL COMMENT '最后登录时间',
    `profile_id` VARCHAR(32) DEFAULT NULL COMMENT '扩展信息ID',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `created_by` VARCHAR(32) DEFAULT NULL COMMENT '创建人ID',
    `updated_by` VARCHAR(32) DEFAULT NULL COMMENT '更新人ID',
    `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`user_id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_phone` (`phone`),
    UNIQUE KEY `uk_wechat_id` (`wechat_id`),
    UNIQUE KEY `uk_youzan_id` (`youzan_id`),
    KEY `idx_status` (`status`),
    KEY `idx_user_type` (`user_type`),
    KEY `idx_email` (`email`),
    KEY `idx_profile_id` (`profile_id`),
    KEY `idx_created_time` (`created_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_bin COMMENT='系统用户表';

-- 2. 用户扩展信息表
DROP TABLE IF EXISTS `user_profile`;
CREATE TABLE `user_profile` (
    `profile_id` VARCHAR(32) NOT NULL COMMENT '扩展信息ID',
    `real_name` VARCHAR(50) DEFAULT NULL COMMENT '真实姓名',
    `company_name` VARCHAR(100) DEFAULT NULL COMMENT '公司名称',
    `company_address` VARCHAR(255) DEFAULT NULL COMMENT '公司地址',
    `contact_person` VARCHAR(50) DEFAULT NULL COMMENT '联系人',
    `contact_phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
    `industry` VARCHAR(50) DEFAULT NULL COMMENT '所属行业',
    `description` TEXT COMMENT '个人简介',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `created_by` VARCHAR(32) DEFAULT NULL COMMENT '创建人ID',
    `updated_by` VARCHAR(32) DEFAULT NULL COMMENT '更新人ID',
    `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`profile_id`),
    KEY `idx_company_name` (`company_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_bin COMMENT='用户扩展信息表';

-- =====================================================
-- 初始化数据
-- =====================================================

-- 插入用户扩展信息
INSERT INTO `user_profile` (
    `profile_id`, `real_name`, `company_name`, `description`
) VALUES 
('profile_admin_001', '系统管理员', '系统管理', '系统最高权限管理员'),
('profile_publisher_001', '张三', '测试公司', '测试发布者账号'),
('profile_receiver_001', '李四', '测试公司', '测试接受者账号');

-- 插入默认管理员用户
INSERT INTO `sys_user` (
    `user_id`, `phone`, `username`, `password`, `nickname`, 
    `user_type`, `status`, `profile_id`, `created_by`
) VALUES (
    'admin_001', '13800138000', 'admin', 
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', -- 密码：123456
    '系统管理员', 1, 1, 'profile_admin_001', 'system'
);

-- 插入默认发布者用户
INSERT INTO `sys_user` (
    `user_id`, `phone`, `username`, `password`, `nickname`, 
    `user_type`, `status`, `profile_id`, `created_by`
) VALUES (
    'publisher_001', '13800138001', 'publisher', 
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', -- 密码：123456
    '测试发布者', 2, 1, 'profile_publisher_001', 'system'
);

-- 插入默认接受者用户
INSERT INTO `sys_user` (
    `user_id`, `phone`, `username`, `password`, `nickname`, 
    `user_type`, `status`, `profile_id`, `created_by`
) VALUES (
    'receiver_001', '13800138002', 'receiver', 
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', -- 密码：123456
    '测试接受者', 3, 1, 'profile_receiver_001', 'system'
);

-- =====================================================
-- 索引优化说明
-- =====================================================

/*
关联逻辑说明：
1. sys_user表包含profile_id字段，关联user_profile表
2. 左表（主表）存在右表ID，符合正确的关联逻辑
3. 去掉了不必要的用户配置表和登录日志表

索引设计说明：
1. 主键索引：所有表都使用主键索引
2. 唯一索引：用户名、手机号、微信ID、有赞ID等唯一字段
3. 普通索引：状态、用户类型、邮箱、profile_id等常用查询字段

查询优化：
- 用户查询：通过profile_id关联查询用户详细信息
- 用户管理：通过状态、用户类型进行筛选
- 扩展信息：通过profile_id快速查询用户扩展信息
*/ 
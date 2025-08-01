/*
 优化后的用户模块数据库脚本
 基于MySQL数据开发规范优化
 作者: scccy
 创建时间: 2025-08-01
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission` (
  `id` VARCHAR(32) NOT NULL COMMENT '权限ID',
  `name` VARCHAR(50) NOT NULL COMMENT '权限名称',
  `code` VARCHAR(50) NOT NULL COMMENT '权限编码',
  `type` TINYINT(1) NOT NULL COMMENT '权限类型：1-菜单，2-按钮，3-接口',
  `parent_id` VARCHAR(32) DEFAULT NULL COMMENT '父权限ID',
  `path` VARCHAR(255) DEFAULT NULL COMMENT '路径',
  `component` VARCHAR(255) DEFAULT NULL COMMENT '组件',
  `icon` VARCHAR(50) DEFAULT NULL COMMENT '图标',
  `sort` INT DEFAULT '0' COMMENT '排序',
  `status` TINYINT(1) DEFAULT '1' COMMENT '状态：0-禁用，1-正常',
  `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `created_by` VARCHAR(32) DEFAULT NULL COMMENT '创建人ID',
  `updated_by` VARCHAR(32) DEFAULT NULL COMMENT '更新人ID',
  `deleted` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_type` (`type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统权限表';

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `id` VARCHAR(32) NOT NULL COMMENT '角色ID',
  `name` VARCHAR(50) NOT NULL COMMENT '角色名称',
  `code` VARCHAR(50) NOT NULL COMMENT '角色编码',
  `description` VARCHAR(255) DEFAULT NULL COMMENT '角色描述',
  `status` TINYINT(1) DEFAULT '1' COMMENT '状态：0-禁用，1-正常',
  `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `created_by` VARCHAR(32) DEFAULT NULL COMMENT '创建人ID',
  `updated_by` VARCHAR(32) DEFAULT NULL COMMENT '更新人ID',
  `deleted` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统角色表';

-- ----------------------------
-- Table structure for sys_role_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_permission`;
CREATE TABLE `sys_role_permission` (
  `id` VARCHAR(32) NOT NULL COMMENT '关联ID',
  `role_id` VARCHAR(32) NOT NULL COMMENT '角色ID',
  `permission_id` VARCHAR(32) NOT NULL COMMENT '权限ID',
  `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `created_by` VARCHAR(32) DEFAULT NULL COMMENT '创建人ID',
  `updated_by` VARCHAR(32) DEFAULT NULL COMMENT '更新人ID',
  `deleted` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_permission` (`role_id`,`permission_id`),
  KEY `idx_role_id` (`role_id`),
  KEY `idx_permission_id` (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联表';

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` VARCHAR(32) NOT NULL COMMENT '用户ID',
  `phone` VARCHAR(20) NOT NULL COMMENT '手机号',
  `wechat_id` VARCHAR(255) NOT NULL COMMENT '微信用户id',
  `youzan_id` VARCHAR(255) NOT NULL COMMENT '有赞用户id',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `password` VARCHAR(100) NOT NULL COMMENT '密码(BCrypt加密)',
  `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
  `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
  `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  `gender` TINYINT(1) DEFAULT '0' COMMENT '性别：0-未知，1-男，2-女',
  `birthday` DATE DEFAULT NULL COMMENT '生日',
  `status` TINYINT(1) DEFAULT '1' COMMENT '状态：0-禁用，1-正常，2-待审核，3-已删除',
  `user_type` TINYINT(1) DEFAULT '2' COMMENT '用户类型：1-最高权限，2-普通发布者',
  `last_login_time` DATETIME DEFAULT NULL COMMENT '最后登录时间',
  `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `created_by` VARCHAR(32) DEFAULT NULL COMMENT '创建人ID',
  `updated_by` VARCHAR(32) DEFAULT NULL COMMENT '更新人ID',
  `deleted` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_phone_wechat_youzan` (`phone`,`wechat_id`,`youzan_id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_status` (`status`),
  KEY `idx_user_type` (`user_type`),
  KEY `idx_email` (`email`),
  KEY `idx_phone` (`phone`),
  KEY `idx_created_time` (`created_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统用户表（共享表）';

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
  `id` VARCHAR(32) NOT NULL COMMENT '关联ID',
  `user_id` VARCHAR(32) NOT NULL COMMENT '用户ID',
  `role_id` VARCHAR(32) NOT NULL COMMENT '角色ID',
  `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `created_by` VARCHAR(32) DEFAULT NULL COMMENT '创建人ID',
  `updated_by` VARCHAR(32) DEFAULT NULL COMMENT '更新人ID',
  `deleted` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_id`,`role_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- ----------------------------
-- Table structure for user_config
-- ----------------------------
DROP TABLE IF EXISTS `user_config`;
CREATE TABLE `user_config` (
  `id` VARCHAR(32) NOT NULL COMMENT 'ID',
  `user_id` VARCHAR(32) NOT NULL COMMENT '用户ID',
  `config_key` VARCHAR(50) NOT NULL COMMENT '配置键',
  `config_value` TEXT COMMENT '配置值',
  `config_type` VARCHAR(20) DEFAULT 'string' COMMENT '配置类型：string,number,boolean,json',
  `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `created_by` VARCHAR(32) DEFAULT NULL COMMENT '创建人ID',
  `updated_by` VARCHAR(32) DEFAULT NULL COMMENT '更新人ID',
  `deleted` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_key` (`user_id`,`config_key`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户配置表';

-- ----------------------------
-- Table structure for user_profile
-- ----------------------------
DROP TABLE IF EXISTS `user_profile`;
CREATE TABLE `user_profile` (
  `id` VARCHAR(32) NOT NULL COMMENT 'ID',
  `user_id` VARCHAR(32) NOT NULL COMMENT '用户ID',
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
  `deleted` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`),
  KEY `idx_company_name` (`company_name`),
  KEY `idx_created_time` (`created_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户扩展信息表';

SET FOREIGN_KEY_CHECKS = 1; 
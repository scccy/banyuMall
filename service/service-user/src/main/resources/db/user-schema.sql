-- 用户服务数据库表结构（扩展表，与service-auth共享sys_user表）
-- 创建时间: 2025-01-27

-- 注意：用户基础信息在sys_user表中，由service-auth和service-user共享
-- 这里只创建用户扩展信息表

-- 1. 用户扩展信息表
CREATE TABLE IF NOT EXISTS `user_profile` (
  `id` varchar(32) NOT NULL COMMENT 'ID',
  `user_id` varchar(32) NOT NULL COMMENT '用户ID',
  `real_name` varchar(50) DEFAULT NULL COMMENT '真实姓名',
  `company_name` varchar(100) DEFAULT NULL COMMENT '公司名称',
  `company_address` varchar(255) DEFAULT NULL COMMENT '公司地址',
  `contact_person` varchar(50) DEFAULT NULL COMMENT '联系人',
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
  KEY `idx_company_name` (`company_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户扩展信息表';

-- 2. 用户配置表
CREATE TABLE IF NOT EXISTS `user_config` (
  `id` varchar(32) NOT NULL COMMENT 'ID',
  `user_id` varchar(32) NOT NULL COMMENT '用户ID',
  `config_key` varchar(50) NOT NULL COMMENT '配置键',
  `config_value` text DEFAULT NULL COMMENT '配置值',
  `config_type` varchar(20) DEFAULT 'string' COMMENT '配置类型：string,number,boolean,json',
  `create_by` varchar(32) DEFAULT NULL COMMENT '创建人ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(32) DEFAULT NULL COMMENT '更新人ID',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(1) DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_key` (`user_id`, `config_key`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户配置表';

-- 初始化数据
-- 插入最高权限用户
INSERT INTO `user_info` (`id`, `username`, `nickname`, `email`, `phone`, `status`, `user_type`) 
VALUES ('1', 'admin', '系统管理员', 'admin@banyu.com', '13800138000', 1, 1);

-- 插入用户扩展信息
INSERT INTO `user_profile` (`id`, `user_id`, `real_name`, `company_name`, `contact_person`, `contact_phone`, `industry`, `description`) 
VALUES ('1', '1', '系统管理员', '半语科技', '管理员', '13800138000', '互联网', '系统最高权限管理员');

-- 插入用户配置
INSERT INTO `user_config` (`id`, `user_id`, `config_key`, `config_value`, `config_type`) VALUES 
('1', '1', 'theme', 'light', 'string'),
('2', '1', 'language', 'zh-CN', 'string'),
('3', '1', 'notifications', 'true', 'boolean'); 
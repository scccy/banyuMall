-- 创建用户表（共享表，service-auth和service-user共同使用）
CREATE TABLE IF NOT EXISTS `sys_user` (
  `id` varchar(32) NOT NULL COMMENT '用户ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码（service-auth管理）',
  `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `gender` tinyint(1) DEFAULT 0 COMMENT '性别：0-未知，1-男，2-女',
  `birthday` date DEFAULT NULL COMMENT '生日',
  `status` tinyint(1) DEFAULT 1 COMMENT '状态：0-禁用，1-正常，2-待审核，3-已删除',
  `user_type` tinyint(1) DEFAULT 2 COMMENT '用户类型：1-最高权限，2-普通发布者',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间（service-auth管理）',
  `create_by` varchar(32) DEFAULT NULL COMMENT '创建人ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(32) DEFAULT NULL COMMENT '更新人ID',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(1) DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_status` (`status`),
  KEY `idx_user_type` (`user_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表（共享表）';

-- 创建角色表
CREATE TABLE IF NOT EXISTS `sys_role` (
  `id` varchar(32) NOT NULL COMMENT '角色ID',
  `name` varchar(50) NOT NULL COMMENT '角色名称',
  `code` varchar(50) NOT NULL COMMENT '角色编码',
  `description` varchar(255) DEFAULT NULL COMMENT '角色描述',
  `status` tinyint(1) DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(1) DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统角色表';

-- 创建用户角色关联表
CREATE TABLE IF NOT EXISTS `sys_user_role` (
  `id` varchar(32) NOT NULL COMMENT 'ID',
  `user_id` varchar(32) NOT NULL COMMENT '用户ID',
  `role_id` varchar(32) NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 创建权限表
CREATE TABLE IF NOT EXISTS `sys_permission` (
  `id` varchar(32) NOT NULL COMMENT '权限ID',
  `name` varchar(50) NOT NULL COMMENT '权限名称',
  `code` varchar(50) NOT NULL COMMENT '权限编码',
  `type` tinyint(1) NOT NULL COMMENT '权限类型：1-菜单，2-按钮，3-接口',
  `parent_id` varchar(32) DEFAULT NULL COMMENT '父权限ID',
  `path` varchar(255) DEFAULT NULL COMMENT '路径',
  `component` varchar(255) DEFAULT NULL COMMENT '组件',
  `icon` varchar(50) DEFAULT NULL COMMENT '图标',
  `sort` int DEFAULT 0 COMMENT '排序',
  `status` tinyint(1) DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(1) DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统权限表';

-- 创建角色权限关联表
CREATE TABLE IF NOT EXISTS `sys_role_permission` (
  `id` varchar(32) NOT NULL COMMENT 'ID',
  `role_id` varchar(32) NOT NULL COMMENT '角色ID',
  `permission_id` varchar(32) NOT NULL COMMENT '权限ID',
  PRIMARY KEY (`id`),
  KEY `idx_role_id` (`role_id`),
  KEY `idx_permission_id` (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- 插入初始管理员用户（密码：123456，使用BCrypt加密）
INSERT INTO `sys_user` (`id`, `username`, `password`, `nickname`, `status`) 
VALUES ('1', 'admin', '$2a$10$ySG2lkvjFHY5O0./CPIE1OI8VJsuKYEzOYzqIa7AJR6sEgSzUFOAm', '系统管理员', 1);

-- 插入初始角色
INSERT INTO `sys_role` (`id`, `name`, `code`, `description`, `status`) VALUES 
('1', '超级管理员', 'ROLE_ADMIN', '系统超级管理员，拥有所有权限', 1),
('2', '普通用户', 'ROLE_USER', '普通用户，拥有基本权限', 1);

-- 关联用户和角色
INSERT INTO `sys_user_role` (`id`, `user_id`, `role_id`) VALUES 
('1', '1', '1');

-- 插入基础权限
INSERT INTO `sys_permission` (`id`, `name`, `code`, `type`, `parent_id`, `path`, `component`, `icon`, `sort`, `status`) VALUES 
('1', '系统管理', 'system:manage', 1, NULL, '/system', 'Layout', 'setting', 1, 1),
('2', '用户管理', 'system:user', 1, '1', '/system/user', 'system/user/index', 'user', 1, 1),
('3', '角色管理', 'system:role', 1, '1', '/system/role', 'system/role/index', 'peoples', 2, 1),
('4', '权限管理', 'system:permission', 1, '1', '/system/permission', 'system/permission/index', 'tree-table', 3, 1),
('5', '查看用户', 'system:user:list', 2, '2', NULL, NULL, NULL, 1, 1),
('6', '添加用户', 'system:user:add', 2, '2', NULL, NULL, NULL, 2, 1),
('7', '编辑用户', 'system:user:edit', 2, '2', NULL, NULL, NULL, 3, 1),
('8', '删除用户', 'system:user:delete', 2, '2', NULL, NULL, NULL, 4, 1);

-- 关联角色和权限
INSERT INTO `sys_role_permission` (`id`, `role_id`, `permission_id`) VALUES 
('1', '1', '1'),
('2', '1', '2'),
('3', '1', '3'),
('4', '1', '4'),
('5', '1', '5'),
('6', '1', '6'),
('7', '1', '7'),
('8', '1', '8'),
('9', '2', '5');
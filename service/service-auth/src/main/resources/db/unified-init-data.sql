-- 半语积分商城 - 统一用户权限系统初始化数据
-- 创建时间: 2025-07-30
-- 说明: 统一管理所有用户和权限相关的初始化数据

-- ========================================
-- 1. 初始化系统用户
-- ========================================
-- 插入系统管理员用户（密码：123456，使用BCrypt加密）
INSERT INTO `sys_user` (`id`, `username`, `password`, `nickname`, `email`, `phone`, `status`, `user_type`) 
VALUES ('1', 'admin', '$2a$10$ySG2lkvjFHY5O0./CPIE1OI8VJsuKYEzOYzqIa7AJR6sEgSzUFOAm', '系统管理员', 'admin@banyu.com', '13800138000', 1, 1)
ON DUPLICATE KEY UPDATE 
  `nickname` = VALUES(`nickname`),
  `email` = VALUES(`email`),
  `phone` = VALUES(`phone`),
  `status` = VALUES(`status`),
  `user_type` = VALUES(`user_type`);

-- 插入测试用户
INSERT INTO `sys_user` (`id`, `username`, `password`, `nickname`, `email`, `phone`, `status`, `user_type`) 
VALUES ('2', 'test', '$2a$10$ySG2lkvjFHY5O0./CPIE1OI8VJsuKYEzOYzqIa7AJR6sEgSzUFOAm', '测试用户', 'test@banyu.com', '13800138001', 1, 2)
ON DUPLICATE KEY UPDATE 
  `nickname` = VALUES(`nickname`),
  `email` = VALUES(`email`),
  `phone` = VALUES(`phone`),
  `status` = VALUES(`status`),
  `user_type` = VALUES(`user_type`);

-- ========================================
-- 2. 初始化系统角色
-- ========================================
INSERT INTO `sys_role` (`id`, `name`, `code`, `description`, `status`) VALUES 
('1', '超级管理员', 'ROLE_ADMIN', '系统超级管理员，拥有所有权限', 1),
('2', '普通用户', 'ROLE_USER', '普通用户，拥有基本权限', 1),
('3', '任务发布者', 'ROLE_PUBLISHER', '任务发布者，可以发布和管理任务', 1)
ON DUPLICATE KEY UPDATE 
  `name` = VALUES(`name`),
  `description` = VALUES(`description`),
  `status` = VALUES(`status`);

-- ========================================
-- 3. 初始化用户角色关联
-- ========================================
INSERT INTO `sys_user_role` (`id`, `user_id`, `role_id`) VALUES 
('1', '1', '1'),
('2', '2', '2')
ON DUPLICATE KEY UPDATE 
  `user_id` = VALUES(`user_id`),
  `role_id` = VALUES(`role_id`);

-- ========================================
-- 4. 初始化系统权限
-- ========================================
INSERT INTO `sys_permission` (`id`, `name`, `code`, `type`, `parent_id`, `path`, `component`, `icon`, `sort`, `status`) VALUES 
-- 系统管理模块
('1', '系统管理', 'system:manage', 1, NULL, '/system', 'Layout', 'setting', 1, 1),
('2', '用户管理', 'system:user', 1, '1', '/system/user', 'system/user/index', 'user', 1, 1),
('3', '角色管理', 'system:role', 1, '1', '/system/role', 'system/role/index', 'peoples', 2, 1),
('4', '权限管理', 'system:permission', 1, '1', '/system/permission', 'system/permission/index', 'tree-table', 3, 1),

-- 用户管理权限
('5', '查看用户', 'system:user:list', 2, '2', NULL, NULL, NULL, 1, 1),
('6', '添加用户', 'system:user:add', 2, '2', NULL, NULL, NULL, 2, 1),
('7', '编辑用户', 'system:user:edit', 2, '2', NULL, NULL, NULL, 3, 1),
('8', '删除用户', 'system:user:delete', 2, '2', NULL, NULL, NULL, 4, 1),

-- 角色管理权限
('9', '查看角色', 'system:role:list', 2, '3', NULL, NULL, NULL, 1, 1),
('10', '添加角色', 'system:role:add', 2, '3', NULL, NULL, NULL, 2, 1),
('11', '编辑角色', 'system:role:edit', 2, '3', NULL, NULL, NULL, 3, 1),
('12', '删除角色', 'system:role:delete', 2, '3', NULL, NULL, NULL, 4, 1),

-- 权限管理权限
('13', '查看权限', 'system:permission:list', 2, '4', NULL, NULL, NULL, 1, 1),
('14', '添加权限', 'system:permission:add', 2, '4', NULL, NULL, NULL, 2, 1),
('15', '编辑权限', 'system:permission:edit', 2, '4', NULL, NULL, NULL, 3, 1),
('16', '删除权限', 'system:permission:delete', 2, '4', NULL, NULL, NULL, 4, 1),

-- 任务管理模块
('17', '任务管理', 'task:manage', 1, NULL, '/task', 'Layout', 'task', 2, 1),
('18', '任务列表', 'task:list', 1, '17', '/task/list', 'task/list/index', 'list', 1, 1),
('19', '任务发布', 'task:publish', 1, '17', '/task/publish', 'task/publish/index', 'plus', 2, 1),

-- 任务管理权限
('20', '查看任务', 'task:list:view', 2, '18', NULL, NULL, NULL, 1, 1),
('21', '发布任务', 'task:publish:create', 2, '19', NULL, NULL, NULL, 1, 1),
('22', '编辑任务', 'task:publish:edit', 2, '19', NULL, NULL, NULL, 2, 1),
('23', '删除任务', 'task:publish:delete', 2, '19', NULL, NULL, NULL, 3, 1)
ON DUPLICATE KEY UPDATE 
  `name` = VALUES(`name`),
  `type` = VALUES(`type`),
  `parent_id` = VALUES(`parent_id`),
  `path` = VALUES(`path`),
  `component` = VALUES(`component`),
  `icon` = VALUES(`icon`),
  `sort` = VALUES(`sort`),
  `status` = VALUES(`status`);

-- ========================================
-- 5. 初始化角色权限关联
-- ========================================
INSERT INTO `sys_role_permission` (`id`, `role_id`, `permission_id`) VALUES 
-- 超级管理员拥有所有权限
('1', '1', '1'), ('2', '1', '2'), ('3', '1', '3'), ('4', '1', '4'),
('5', '1', '5'), ('6', '1', '6'), ('7', '1', '7'), ('8', '1', '8'),
('9', '1', '9'), ('10', '1', '10'), ('11', '1', '11'), ('12', '1', '12'),
('13', '1', '13'), ('14', '1', '14'), ('15', '1', '15'), ('16', '1', '16'),
('17', '1', '17'), ('18', '1', '18'), ('19', '1', '19'),
('20', '1', '20'), ('21', '1', '21'), ('22', '1', '22'), ('23', '1', '23'),

-- 普通用户拥有基本查看权限
('24', '2', '20'),

-- 任务发布者拥有任务相关权限
('25', '3', '17'), ('26', '3', '18'), ('27', '3', '19'),
('28', '3', '20'), ('29', '3', '21'), ('30', '3', '22'), ('31', '3', '23')
ON DUPLICATE KEY UPDATE 
  `role_id` = VALUES(`role_id`),
  `permission_id` = VALUES(`permission_id`);

-- ========================================
-- 6. 初始化用户扩展信息
-- ========================================
INSERT INTO `user_profile` (`id`, `user_id`, `real_name`, `company_name`, `contact_person`, `contact_phone`, `industry`, `description`) 
VALUES ('1', '1', '系统管理员', '半语科技', '管理员', '13800138000', '互联网', '系统最高权限管理员')
ON DUPLICATE KEY UPDATE 
  `real_name` = VALUES(`real_name`),
  `company_name` = VALUES(`company_name`),
  `contact_person` = VALUES(`contact_person`),
  `contact_phone` = VALUES(`contact_phone`),
  `industry` = VALUES(`industry`),
  `description` = VALUES(`description`);

-- ========================================
-- 7. 初始化用户配置
-- ========================================
INSERT INTO `user_config` (`id`, `user_id`, `config_key`, `config_value`, `config_type`) VALUES 
('1', '1', 'theme', 'light', 'string'),
('2', '1', 'language', 'zh-CN', 'string'),
('3', '1', 'notifications', 'true', 'boolean'),
('4', '1', 'page_size', '20', 'number')
ON DUPLICATE KEY UPDATE 
  `config_value` = VALUES(`config_value`),
  `config_type` = VALUES(`config_type`); 
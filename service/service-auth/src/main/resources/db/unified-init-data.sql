-- 统一初始数据脚本
-- 创建时间: 2025-01-27
-- 说明: 供service-auth和service-user共同使用的初始数据

-- 插入系统角色数据
INSERT INTO `sys_role` (`id`, `role_name`, `role_code`, `description`, `status`, `create_by`, `create_time`) VALUES
('1', '超级管理员', 'SUPER_ADMIN', '系统超级管理员，拥有所有权限', 1, 'system', NOW()),
('2', '普通用户', 'USER', '普通用户，基础权限', 1, 'system', NOW()),
('3', '发布者', 'PUBLISHER', '内容发布者，可以发布内容', 1, 'system', NOW());

-- 插入系统权限数据
INSERT INTO `sys_permission` (`id`, `permission_name`, `permission_code`, `permission_type`, `parent_id`, `path`, `component`, `icon`, `sort_order`, `status`, `create_by`, `create_time`) VALUES
-- 用户管理权限
('1', '用户管理', 'user:manage', 1, NULL, '/user', 'UserManage', 'user', 1, 1, 'system', NOW()),
('2', '用户查询', 'user:query', 3, '1', NULL, NULL, NULL, 1, 1, 'system', NOW()),
('3', '用户创建', 'user:create', 3, '1', NULL, NULL, NULL, 2, 1, 'system', NOW()),
('4', '用户修改', 'user:update', 3, '1', NULL, NULL, NULL, 3, 1, 'system', NOW()),
('5', '用户删除', 'user:delete', 3, '1', NULL, NULL, NULL, 4, 1, 'system', NOW()),

-- 角色管理权限
('6', '角色管理', 'role:manage', 1, NULL, '/role', 'RoleManage', 'team', 2, 1, 'system', NOW()),
('7', '角色查询', 'role:query', 3, '6', NULL, NULL, NULL, 1, 1, 'system', NOW()),
('8', '角色创建', 'role:create', 3, '6', NULL, NULL, NULL, 2, 1, 'system', NOW()),
('9', '角色修改', 'role:update', 3, '6', NULL, NULL, NULL, 3, 1, 'system', NOW()),
('10', '角色删除', 'role:delete', 3, '6', NULL, NULL, NULL, 4, 1, 'system', NOW()),

-- 权限管理权限
('11', '权限管理', 'permission:manage', 1, NULL, '/permission', 'PermissionManage', 'safety', 3, 1, 'system', NOW()),
('12', '权限查询', 'permission:query', 3, '11', NULL, NULL, NULL, 1, 1, 'system', NOW()),
('13', '权限创建', 'permission:create', 3, '11', NULL, NULL, NULL, 2, 1, 'system', NOW()),
('14', '权限修改', 'permission:update', 3, '11', NULL, NULL, NULL, 3, 1, 'system', NOW()),
('15', '权限删除', 'permission:delete', 3, '11', NULL, NULL, NULL, 4, 1, 'system', NOW()),

-- 系统管理权限
('16', '系统管理', 'system:manage', 1, NULL, '/system', 'SystemManage', 'setting', 4, 1, 'system', NOW()),
('17', '系统配置', 'system:config', 3, '16', NULL, NULL, NULL, 1, 1, 'system', NOW()),
('18', '系统监控', 'system:monitor', 3, '16', NULL, NULL, NULL, 2, 1, 'system', NOW()),

-- 内容管理权限
('19', '内容管理', 'content:manage', 1, NULL, '/content', 'ContentManage', 'file-text', 5, 1, 'system', NOW()),
('20', '内容查询', 'content:query', 3, '19', NULL, NULL, NULL, 1, 1, 'system', NOW()),
('21', '内容创建', 'content:create', 3, '19', NULL, NULL, NULL, 2, 1, 'system', NOW()),
('22', '内容修改', 'content:update', 3, '19', NULL, NULL, NULL, 3, 1, 'system', NOW()),
('23', '内容删除', 'content:delete', 3, '19', NULL, NULL, NULL, 4, 1, 'system', NOW());

-- 插入超级管理员用户（密码：admin123）
INSERT INTO `sys_user` (`id`, `username`, `password`, `nickname`, `email`, `phone`, `gender`, `status`, `user_type`, `create_by`, `create_time`) VALUES
('1', 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '超级管理员', 'admin@example.com', '13800138000', 1, 1, 1, 'system', NOW());

-- 插入普通用户（密码：user123）
INSERT INTO `sys_user` (`id`, `username`, `password`, `nickname`, `email`, `phone`, `gender`, `status`, `user_type`, `create_by`, `create_time`) VALUES
('2', 'user', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', '普通用户', 'user@example.com', '13800138001', 0, 1, 2, 'system', NOW()),
('3', 'publisher', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', '发布者', 'publisher@example.com', '13800138002', 0, 1, 2, 'system', NOW());

-- 插入用户角色关联数据
INSERT INTO `sys_user_role` (`id`, `user_id`, `role_id`, `create_by`, `create_time`) VALUES
('1', '1', '1', 'system', NOW()),  -- admin -> 超级管理员
('2', '2', '2', 'system', NOW()),  -- user -> 普通用户
('3', '3', '3', 'system', NOW());  -- publisher -> 发布者

-- 插入角色权限关联数据
-- 超级管理员拥有所有权限
INSERT INTO `sys_role_permission` (`id`, `role_id`, `permission_id`, `create_by`, `create_time`) VALUES
-- 超级管理员权限
('1', '1', '1', 'system', NOW()), ('2', '1', '2', 'system', NOW()), ('3', '1', '3', 'system', NOW()), ('4', '1', '4', 'system', NOW()), ('5', '1', '5', 'system', NOW()),
('6', '1', '6', 'system', NOW()), ('7', '1', '7', 'system', NOW()), ('8', '1', '8', 'system', NOW()), ('9', '1', '9', 'system', NOW()), ('10', '1', '10', 'system', NOW()),
('11', '1', '11', 'system', NOW()), ('12', '1', '12', 'system', NOW()), ('13', '1', '13', 'system', NOW()), ('14', '1', '14', 'system', NOW()), ('15', '1', '15', 'system', NOW()),
('16', '1', '16', 'system', NOW()), ('17', '1', '17', 'system', NOW()), ('18', '1', '18', 'system', NOW()), ('19', '1', '19', 'system', NOW()), ('20', '1', '20', 'system', NOW()),
('21', '1', '21', 'system', NOW()), ('22', '1', '22', 'system', NOW()), ('23', '1', '23', 'system', NOW()),

-- 普通用户权限
('24', '2', '20', 'system', NOW()),  -- 内容查询

-- 发布者权限
('25', '3', '20', 'system', NOW()),  -- 内容查询
('26', '3', '21', 'system', NOW()),  -- 内容创建
('27', '3', '22', 'system', NOW());  -- 内容修改

-- 插入用户扩展信息
INSERT INTO `user_profile` (`id`, `user_id`, `real_name`, `address`, `contact_phone`, `industry`, `description`, `create_by`, `create_time`) VALUES
('1', '1', '系统管理员', '北京市朝阳区', '13800138000', 'IT行业', '系统超级管理员', 'system', NOW()),
('2', '2', '张三', '上海市浦东新区', '13800138001', '教育行业', '普通用户', 'system', NOW()),
('3', '3', '李四', '广州市天河区', '13800138002', '媒体行业', '内容发布者', 'system', NOW());

-- 插入用户配置数据
INSERT INTO `user_config` (`id`, `user_id`, `config_key`, `config_value`, `config_type`, `description`, `create_by`, `create_time`) VALUES
('1', '1', 'theme', 'dark', 'string', '界面主题', 'system', NOW()),
('2', '1', 'language', 'zh-CN', 'string', '界面语言', 'system', NOW()),
('3', '2', 'theme', 'light', 'string', '界面主题', 'system', NOW()),
('4', '2', 'language', 'zh-CN', 'string', '界面语言', 'system', NOW()),
('5', '3', 'theme', 'light', 'string', '界面主题', 'system', NOW()),
('6', '3', 'language', 'zh-CN', 'string', '界面语言', 'system', NOW()); 
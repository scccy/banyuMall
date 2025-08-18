-- 企业微信相关表结构
-- 根据企业微信官方文档：https://developer.work.weixin.qq.com/document/path/90337
-- 创建时间：2025-01-18

-- 1. 企业微信部门表（维度表）
DROP TABLE IF EXISTS `wechatwork_department`;
CREATE TABLE `wechatwork_department` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `dep_id` int(11) NOT NULL COMMENT '企业微信部门ID',
  `dep_name` varchar(255) NOT NULL COMMENT '部门名称',
  `parentid` int(11) DEFAULT NULL COMMENT '父部门ID',
  `order` varchar(255) DEFAULT NULL COMMENT '排序',
  `department_leader` text DEFAULT NULL COMMENT '部门负责人列表（JSON格式）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_dep_id` (`dep_id`),
  KEY `idx_parentid` (`parentid`),
  KEY `idx_dep_name` (`dep_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='企业微信部门维度表';

-- 2. 企业微信联系人表（维度表）
DROP TABLE IF EXISTS `wechatwork_contacts`;
CREATE TABLE `wechatwork_contacts` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `userid` varchar(255) NOT NULL COMMENT '企业微信用户ID',
  `name` varchar(255) NOT NULL COMMENT '成员名称',
  `department` text DEFAULT NULL COMMENT '成员所属部门id列表（JSON格式）',
  `position` varchar(255) DEFAULT NULL COMMENT '职位信息',
  `mobile` varchar(255) DEFAULT NULL COMMENT '手机号',
  `gender` varchar(255) DEFAULT NULL COMMENT '性别',
  `email` varchar(255) DEFAULT NULL COMMENT '邮箱',
  `biz_mail` varchar(255) DEFAULT NULL COMMENT '企业邮箱',
  `avatar` text DEFAULT NULL COMMENT '头像url',
  `status` int(11) DEFAULT NULL COMMENT '激活状态',
  `enable` int(11) DEFAULT NULL COMMENT '成员启用状态',
  `alias` varchar(255) DEFAULT NULL COMMENT '别名',
  `isleader` int(11) DEFAULT NULL COMMENT '是否是部门领导',
  `hide_mobile` int(11) DEFAULT NULL COMMENT '是否隐藏手机号',
  `telephone` varchar(255) DEFAULT NULL COMMENT '座机',
  `english_name` varchar(255) DEFAULT NULL COMMENT '英文名',
  `main_department` int(11) DEFAULT NULL COMMENT '主部门',
  `qr_code` text DEFAULT NULL COMMENT '员工个人二维码',
  `external_position` varchar(255) DEFAULT NULL COMMENT '对外职务',
  `external_profile` text DEFAULT NULL COMMENT '对外属性（JSON格式）',
  `open_userid` varchar(255) DEFAULT NULL COMMENT '全局唯一ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_userid` (`userid`),
  KEY `idx_name` (`name`),
  KEY `idx_main_department` (`main_department`),
  KEY `idx_status` (`status`),
  KEY `idx_enable` (`enable`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='企业微信联系人维度表';

-- 3. 企业微信访问令牌表（配置表）
DROP TABLE IF EXISTS `wechatwork_access_token`;
CREATE TABLE `wechatwork_access_token` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `corp_id` varchar(255) NOT NULL COMMENT '企业ID',
  `corp_secret` varchar(255) NOT NULL COMMENT '应用凭证密钥',
  `access_token` text NOT NULL COMMENT '访问令牌',
  `expires_in` int(11) NOT NULL COMMENT '过期时间（秒）',
  `created_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_corp_secret` (`corp_id`, `corp_secret`),
  KEY `idx_created_time` (`created_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='企业微信访问令牌配置表';

-- 4. 企业微信JS-SDK票据表（配置表）
DROP TABLE IF EXISTS `wechatwork_jsapi_ticket`;
CREATE TABLE `wechatwork_jsapi_ticket` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `corp_id` varchar(255) NOT NULL COMMENT '企业ID',
  `jsapi_ticket` text NOT NULL COMMENT 'JS-SDK使用权限签名',
  `expires_in` int(11) NOT NULL COMMENT '过期时间（秒）',
  `created_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_corp_id` (`corp_id`),
  KEY `idx_created_time` (`created_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='企业微信JS-SDK票据配置表';

-- 5. 企业微信同步日志表（日志表）
DROP TABLE IF EXISTS `wechatwork_sync_log`;
CREATE TABLE `wechatwork_sync_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `sync_type` varchar(50) NOT NULL COMMENT '同步类型（department/contacts）',
  `department_id` int(11) DEFAULT NULL COMMENT '部门ID（同步联系人时使用）',
  `total_count` int(11) NOT NULL COMMENT '总数量',
  `success_count` int(11) NOT NULL COMMENT '成功数量',
  `error_count` int(11) NOT NULL COMMENT '错误数量',
  `error_message` text DEFAULT NULL COMMENT '错误信息',
  `sync_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '同步时间',
  `duration` int(11) DEFAULT NULL COMMENT '同步耗时（毫秒）',
  PRIMARY KEY (`id`),
  KEY `idx_sync_type` (`sync_type`),
  KEY `idx_department_id` (`department_id`),
  KEY `idx_sync_time` (`sync_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='企业微信同步日志表';

-- 插入示例数据（可选）
-- INSERT INTO `wechatwork_department` (`dep_id`, `dep_name`, `parentid`, `order`) VALUES (1, '根部门', 0, '100000000');
-- INSERT INTO `wechatwork_department` (`dep_id`, `dep_name`, `parentid`, `order`) VALUES (2, '技术部', 1, '100000001');
-- INSERT INTO `wechatwork_department` (`dep_id`, `dep_name`, `parentid`, `order`) VALUES (3, '产品部', 1, '100000002');

-- 表结构说明：
-- 1. wechatwork_department: 部门维度表，存储企业微信部门信息
-- 2. wechatwork_contacts: 联系人维度表，存储企业微信用户信息
-- 3. wechatwork_access_token: 访问令牌配置表，存储API调用凭证
-- 4. wechatwork_jsapi_ticket: JS-SDK票据表，存储前端调用凭证
-- 5. wechatwork_sync_log: 同步日志表，记录数据同步历史

-- 注意事项：
-- 1. 维度表不包含审计字段（创建时间、更新时间等）
-- 2. 配置表和日志表包含必要的审计字段
-- 3. 所有表使用utf8mb4字符集，支持emoji等特殊字符
-- 4. 添加了必要的索引，提高查询性能
-- 5. 字段类型和长度根据企业微信API返回数据设计

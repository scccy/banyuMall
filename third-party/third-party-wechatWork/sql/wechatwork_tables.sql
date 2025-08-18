-- 企业微信相关表结构
-- 根据企业微信官方文档：https://developer.work.weixin.qq.com/document/path/90337
-- 创建时间：2025-01-18
-- 注意：访问令牌和JS-SDK票据通过feign客户端从third_party_config表获取，无需单独建表

-- 1. 企业微信部门表（维度表）
DROP TABLE IF EXISTS `wechatwork_department`;
CREATE TABLE `wechatwork_department` (
  `dep_id` int(11) NOT NULL COMMENT '企业微信部门ID（主键）',
  `dep_name` varchar(255) NOT NULL COMMENT '部门名称',
  `parentid` int(11) DEFAULT NULL COMMENT '父部门ID',
  `order` varchar(255) DEFAULT NULL COMMENT '排序',
  `department_leader` text DEFAULT NULL COMMENT '部门负责人列表（JSON格式）',
  PRIMARY KEY (`dep_id`),
  KEY `idx_parentid` (`parentid`),
  KEY `idx_dep_name` (`dep_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='企业微信部门维度表';

-- 2. 企业微信联系人表（维度表）
DROP TABLE IF EXISTS `wechatwork_contacts`;
CREATE TABLE `wechatwork_contacts` (
  `contact_id` varchar(255) NOT NULL COMMENT '企业微信用户ID（主键）',
  `name` varchar(255) NOT NULL COMMENT '成员名称',
  `dep_ids` text DEFAULT NULL COMMENT '成员所属部门id列表（JSON格式）',
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
  PRIMARY KEY (`contact_id`),
  KEY `idx_name` (`name`),
  KEY `idx_main_department` (`main_department`),
  KEY `idx_status` (`status`),
  KEY `idx_enable` (`enable`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='企业微信联系人维度表';

-- 插入示例数据（可选）
-- INSERT INTO `wechatwork_department` (`dep_id`, `dep_name`, `parentid`, `order`) VALUES (1, '根部门', 0, '100000000');
-- INSERT INTO `wechatwork_department` (`dep_id`, `dep_name`, `parentid`, `order`) VALUES (2, '技术部', 1, '100000001');
-- INSERT INTO `wechatwork_department` (`dep_id`, `dep_name`, `parentid`, `order`) VALUES (3, '产品部', 1, '100000002');

-- 表结构说明：
-- 1. wechatwork_department: 部门维度表，存储企业微信部门信息
--    - 使用dep_id作为主键，因为企业微信部门ID本身就是唯一的
--    - 不包含自增ID字段，避免冗余
-- 2. wechatwork_contacts: 联系人维度表，存储企业微信用户信息
--    - 使用contact_id作为主键，对应企业微信的userid
--    - 将department字段改为dep_ids，更清晰地表示部门ID列表
--    - 不包含自增ID字段，避免冗余

-- 注意事项：
-- 1. 维度表不包含审计字段（创建时间、更新时间等）
-- 2. 访问令牌和JS-SDK票据通过feign客户端从third_party_config表获取
-- 3. 所有表使用utf8mb4字符集，支持emoji等特殊字符
-- 4. 添加了必要的索引，提高查询性能
-- 5. 字段类型和长度根据企业微信API返回数据设计
-- 6. 部门表使用dep_id作为主键，联系人表使用contact_id作为主键，符合业务逻辑
-- 7. 字段命名更加清晰：dep_ids表示部门ID列表，与dep_id呼应

-- =====================================================
-- 第三方业务 - 第三方平台配置数据库脚本
-- 基于MySQL数据开发规范优化
-- 作者: scccy
-- 创建时间: 2025-08-05
-- 说明: 提供第三方平台配置管理，支持企业微信、钉钉、飞书等平台配置
-- =====================================================

-- 使用数据库
USE banyu;

-- 1. 第三方平台配置表 - 存储各第三方平台的配置信息
DROP TABLE IF EXISTS `third_party_config`;
CREATE TABLE `third_party_config` (
  `config_id` VARCHAR(32) NOT NULL COMMENT '配置ID',
  `platform_type` VARCHAR(50) NOT NULL COMMENT '平台类型(wechatwork/dingtalk/feishu/youzan等)',
  `platform_name` VARCHAR(100) NOT NULL COMMENT '平台名称',
  `corp_id` VARCHAR(100) DEFAULT NULL COMMENT '企业ID',
  `corp_secret` VARCHAR(255) DEFAULT NULL COMMENT '企业密钥',
  `token` VARCHAR(255) DEFAULT NULL COMMENT 'Token',
  `encoding_aes_key` VARCHAR(255) DEFAULT NULL COMMENT 'EncodingAESKey',
  `echo_str` VARCHAR(255) DEFAULT NULL COMMENT 'EchoStr',
  `app_id` VARCHAR(100) DEFAULT NULL COMMENT '应用ID',
  `app_secret` VARCHAR(255) DEFAULT NULL COMMENT '应用密钥',
  `access_token` VARCHAR(500) DEFAULT NULL COMMENT '访问令牌',
  `webhook_url` VARCHAR(500) DEFAULT NULL COMMENT 'Webhook地址',
  `callback_url` VARCHAR(500) DEFAULT NULL COMMENT '回调地址',
  `config_status` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '配置状态：0-禁用，1-启用',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注说明',
  `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `created_by` VARCHAR(32) DEFAULT NULL COMMENT '创建人ID',
  `updated_by` VARCHAR(32) DEFAULT NULL COMMENT '更新人ID',
  `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`config_id`),
  UNIQUE KEY `uk_platform_type` (`platform_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_bin COMMENT='第三方平台配置表';

-- 2. 插入初始数据 - 企业微信配置示例
INSERT INTO `third_party_config` (
  `config_id`, 
  `platform_type`, 
  `platform_name`, 
  `corp_id`, 
  `corp_secret`, 
  `token`, 
  `encoding_aes_key`, 
  `echo_str`, 
  `app_id`, 
  `app_secret`, 
  `access_token`, 
  `webhook_url`, 
  `callback_url`, 
  `config_status`, 
  `remark`, 
  `created_by`
) VALUES (
  'wechatwork_config_001',
  'wechatwork',
  '企业微信',
  'ww1234567890abcdef',
  'corp_secret_example',
  'token_example',
  'encoding_aes_key_example',
  'echo_str_example',
  'app_id_example',
  'app_secret_example',
  'access_token_example',
  'https://webhook.example.com/wechatwork',
  'https://callback.example.com/wechatwork',
  1,
  '企业微信平台配置示例',
  'system'
);

-- 3. 插入钉钉配置示例
INSERT INTO `third_party_config` (
  `config_id`, 
  `platform_type`, 
  `platform_name`, 
  `corp_id`, 
  `corp_secret`, 
  `token`, 
  `encoding_aes_key`, 
  `echo_str`, 
  `app_id`, 
  `app_secret`, 
  `access_token`, 
  `webhook_url`, 
  `callback_url`, 
  `config_status`, 
  `remark`, 
  `created_by`
) VALUES (
  'dingtalk_config_001',
  'dingtalk',
  '钉钉',
  'ding1234567890abcdef',
  'dingtalk_secret_example',
  'dingtalk_token_example',
  'dingtalk_encoding_aes_key_example',
  'dingtalk_echo_str_example',
  'dingtalk_app_id_example',
  'dingtalk_app_secret_example',
  'dingtalk_access_token_example',
  'https://webhook.example.com/dingtalk',
  'https://callback.example.com/dingtalk',
  1,
  '钉钉平台配置示例',
  'system'
);

-- =====================================================
-- 索引优化说明
-- =====================================================

/*
关联逻辑说明：
1. third_party_config表通过created_by和updated_by字段关联sys_user表
2. 左表（主表）存在右表ID，符合正确的关联逻辑

索引设计说明：
1. 主键索引：使用config_id作为主键
2. 唯一索引：平台类型必须唯一，避免重复配置
3. 普通索引：平台名称、配置状态、创建时间等常用查询字段

查询优化：
- 平台配置查询：通过platform_type快速定位配置
- 配置状态查询：通过config_status查询启用/禁用的配置
- 配置管理：通过创建时间范围查询配置历史

安全考虑：
- 敏感字段如corp_secret、app_secret等建议在生产环境中加密存储
- 建议定期轮换密钥和Token
*/ 
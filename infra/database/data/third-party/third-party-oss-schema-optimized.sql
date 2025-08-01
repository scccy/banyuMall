-- =====================================================
-- 优化后的第三方业务 - 阿里云OSS文件存储服务数据库脚本
-- 基于MySQL数据开发规范优化
-- 创建时间: 2025-08-01
-- 说明: 提供文件存储Feign客户端，记录存储行为和文件哈希值
-- =====================================================

-- 1. 文件存储记录表 - 记录文件存储行为
CREATE TABLE `oss_file_storage` (
  `id` VARCHAR(32) NOT NULL COMMENT '主键ID',
  `original_name` VARCHAR(255) NOT NULL COMMENT '原始文件名',
  `file_size` BIGINT NOT NULL COMMENT '文件大小(字节)',
  `mime_type` VARCHAR(100) NOT NULL COMMENT 'MIME类型',
  `object_key` VARCHAR(500) NOT NULL COMMENT 'OSS对象键',
  `access_url` VARCHAR(500) NOT NULL COMMENT '文件访问URL',
  `bucket_name` VARCHAR(100) NOT NULL COMMENT 'OSS存储桶名称',
  `file_path` VARCHAR(500) NOT NULL COMMENT '文件路径(如:core-publisher/task-image/2025/08/01/)',
  `source_service` VARCHAR(50) NOT NULL COMMENT '来源服务(如:core-publisher)',
  `business_type` VARCHAR(50) NOT NULL COMMENT '业务类型(如:task-image)',
  `upload_user_id` VARCHAR(32) DEFAULT NULL COMMENT '上传用户ID',
  `upload_user_name` VARCHAR(100) DEFAULT NULL COMMENT '上传用户名',
  `upload_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
  `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `created_by` VARCHAR(32) DEFAULT NULL COMMENT '创建人ID',
  `updated_by` VARCHAR(32) DEFAULT NULL COMMENT '更新人ID',
  `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_object_key` (`object_key`),
  KEY `idx_source_service` (`source_service`),
  KEY `idx_business_type` (`business_type`),
  KEY `idx_upload_user_id` (`upload_user_id`),
  KEY `idx_upload_time` (`upload_time`),
  KEY `idx_file_path` (`file_path`),
  KEY `idx_source_business` (`source_service`, `business_type`),
  KEY `idx_created_time` (`created_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='OSS文件存储记录表';

-- 2. 文件访问日志表 - 记录文件访问情况，支持审计
CREATE TABLE `oss_file_access_log` (
  `id` VARCHAR(32) NOT NULL COMMENT '主键ID',
  `file_id` VARCHAR(32) NOT NULL COMMENT '文件ID',
  `access_type` VARCHAR(20) NOT NULL COMMENT '访问类型(VIEW/DOWNLOAD)',
  `access_user_id` VARCHAR(32) DEFAULT NULL COMMENT '访问用户ID',
  `access_user_name` VARCHAR(100) DEFAULT NULL COMMENT '访问用户名',
  `access_ip` VARCHAR(50) DEFAULT NULL COMMENT '访问IP地址',
  `user_agent` VARCHAR(500) DEFAULT NULL COMMENT '用户代理',
  `referer` VARCHAR(500) DEFAULT NULL COMMENT '来源页面',
  `access_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '访问时间',
  `response_time` INT DEFAULT NULL COMMENT '响应时间(毫秒)',
  `status_code` INT DEFAULT NULL COMMENT 'HTTP状态码',
  `error_message` VARCHAR(500) DEFAULT NULL COMMENT '错误信息',
  `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `created_by` VARCHAR(32) DEFAULT NULL COMMENT '创建人ID',
  `updated_by` VARCHAR(32) DEFAULT NULL COMMENT '更新人ID',
  `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_file_id` (`file_id`),
  KEY `idx_access_type` (`access_type`),
  KEY `idx_access_user_id` (`access_user_id`),
  KEY `idx_access_time` (`access_time`),
  KEY `idx_file_access_time` (`file_id`, `access_time`),
  KEY `idx_access_user_time` (`access_user_id`, `access_time`),
  KEY `idx_created_time` (`created_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='OSS文件访问日志表'; 
-- =====================================================
-- 第三方业务 - 阿里云OSS文件存储服务数据库脚本
-- 创建时间: 2025-08-01
-- 说明: 提供文件存储Feign客户端，记录存储行为和文件哈希值
-- =====================================================

-- 1. 文件存储记录表 - 记录文件存储行为
CREATE TABLE `oss_file_storage` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `original_name` varchar(255) NOT NULL COMMENT '原始文件名',
  `file_size` bigint NOT NULL COMMENT '文件大小(字节)',
  `mime_type` varchar(100) NOT NULL COMMENT 'MIME类型',
  `object_key` varchar(500) NOT NULL COMMENT 'OSS对象键',
  `access_url` varchar(500) NOT NULL COMMENT '文件访问URL',
  `bucket_name` varchar(100) NOT NULL COMMENT 'OSS存储桶名称',
  `file_path` varchar(500) NOT NULL COMMENT '文件路径(如:core-publisher/task-image/2025/08/01/)',
  `source_service` varchar(50) NOT NULL COMMENT '来源服务(如:core-publisher)',
  `business_type` varchar(50) NOT NULL COMMENT '业务类型(如:task-image)',
  `upload_user_id` bigint DEFAULT NULL COMMENT '上传用户ID',
  `upload_user_name` varchar(100) DEFAULT NULL COMMENT '上传用户名',
  `upload_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除标识',
  PRIMARY KEY (`id`),
  KEY `idx_source_service` (`source_service`),
  KEY `idx_business_type` (`business_type`),
  KEY `idx_upload_user_id` (`upload_user_id`),
  KEY `idx_upload_time` (`upload_time`),
  KEY `idx_object_key` (`object_key`),
  KEY `idx_file_path` (`file_path`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='OSS文件存储记录表';

-- 2. 文件访问日志表 - 记录文件访问情况，支持审计
CREATE TABLE `oss_file_access_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `file_id` bigint NOT NULL COMMENT '文件ID',
  `access_type` varchar(20) NOT NULL COMMENT '访问类型(VIEW/DOWNLOAD)',
  `access_user_id` bigint DEFAULT NULL COMMENT '访问用户ID',
  `access_user_name` varchar(100) DEFAULT NULL COMMENT '访问用户名',
  `access_ip` varchar(50) DEFAULT NULL COMMENT '访问IP地址',
  `user_agent` varchar(500) DEFAULT NULL COMMENT '用户代理',
  `referer` varchar(500) DEFAULT NULL COMMENT '来源页面',
  `access_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '访问时间',
  `response_time` int DEFAULT NULL COMMENT '响应时间(毫秒)',
  `status_code` int DEFAULT NULL COMMENT 'HTTP状态码',
  `error_message` varchar(500) DEFAULT NULL COMMENT '错误信息',
  PRIMARY KEY (`id`),
  KEY `idx_file_id` (`file_id`),
  KEY `idx_access_type` (`access_type`),
  KEY `idx_access_user_id` (`access_user_id`),
  KEY `idx_access_time` (`access_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='OSS文件访问日志表'; 
-- =====================================================
-- 第三方业务 - 阿里云OSS文件存储服务数据库脚本
-- 基于MySQL数据开发规范优化
-- 作者: scccy
-- 创建时间: 2025-01-27
-- 说明: 提供文件存储服务，记录文件上传信息
-- =====================================================

-- 使用数据库
USE banyu_mall;

-- 1. 文件存储记录表 - 记录文件存储行为
DROP TABLE IF EXISTS `oss_file_storage`;
CREATE TABLE `oss_file_storage` (
  `storage_id` VARCHAR(32) NOT NULL COMMENT '存储记录ID',
  `original_name` VARCHAR(255) NOT NULL COMMENT '原始文件名',
  `file_size` BIGINT NOT NULL COMMENT '文件大小(字节)',
  `mime_type` VARCHAR(100) NOT NULL COMMENT 'MIME类型',
  `object_key` VARCHAR(500) NOT NULL COMMENT 'OSS对象键',
  `access_url` VARCHAR(500) NOT NULL COMMENT '文件访问URL',
  `bucket_name` VARCHAR(100) NOT NULL COMMENT 'OSS存储桶名称',
  `file_path` VARCHAR(500) NOT NULL COMMENT '文件路径(如:core-publisher/task-image/2025/08/01/)',
  `source_service` VARCHAR(50) NOT NULL COMMENT '来源服务(如:core-publisher)',
  `business_type` VARCHAR(50) NOT NULL COMMENT '业务类型(如:task-image)',
  `user_id` VARCHAR(32) DEFAULT NULL COMMENT '上传用户ID',
  `upload_user_name` VARCHAR(100) DEFAULT NULL COMMENT '上传用户名',
  `upload_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
  `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `created_by` VARCHAR(32) DEFAULT NULL COMMENT '创建人ID',
  `updated_by` VARCHAR(32) DEFAULT NULL COMMENT '更新人ID',
  `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`storage_id`),
  UNIQUE KEY `uk_object_key` (`object_key`),
  KEY `idx_source_service` (`source_service`),
  KEY `idx_business_type` (`business_type`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_upload_time` (`upload_time`),
  KEY `idx_file_path` (`file_path`),
  KEY `idx_source_business` (`source_service`, `business_type`),
  KEY `idx_created_time` (`created_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_bin COMMENT='OSS文件存储记录表';

-- =====================================================
-- 索引优化说明
-- =====================================================

/*
关联逻辑说明：
1. oss_file_storage表包含user_id字段，关联sys_user表
2. 左表（主表）存在右表ID，符合正确的关联逻辑
3. 去掉了不必要的文件访问日志表

索引设计说明：
1. 主键索引：使用storage_id作为主键
2. 唯一索引：OSS对象键必须唯一
3. 普通索引：来源服务、业务类型、用户ID等常用查询字段
4. 复合索引：来源服务+业务类型等组合查询

查询优化：
- 文件查询：通过来源服务和业务类型快速定位文件
- 用户文件：通过user_id查询用户上传的文件
- 文件管理：通过文件路径和时间范围查询
*/ 
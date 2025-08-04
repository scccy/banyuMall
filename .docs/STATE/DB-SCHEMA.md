# 数据库设计基线

## 📋 数据库设计概览

**项目名称**: BanyuMall 微服务项目  
**数据库类型**: MySQL 8.0+  
**字符集**: utf8mb4  
**排序规则**: utf8mb4_0900_bin  
**存储引擎**: InnoDB  
**创建时间**: 2025-08-04  
**规范来源**: `zinfra/database/MySQL数据开发规范.md`

## 🎯 设计规范遵循

本数据库设计严格遵循 `.docs/RULES/DB-RULES.md` 中的数据库开发规则：

- ✅ **命名规范**: 小写字母，下划线分隔
- ✅ **主键设计**: `表名_id` 格式，`VARCHAR(32)` 类型
- ✅ **关联逻辑**: 左表存在右表ID原则
- ✅ **索引设计**: `idx_` 和 `uk_` 前缀规范
- ✅ **表结构**: 统一字符集、存储引擎、标准字段
- ✅ **禁止事项**: 不使用外键、触发器、存储过程等
- ✅ **推荐事项**: 使用逻辑删除、软时间戳、审计字段

## 🗂️ 数据库模块结构

### 1. 用户模块 (`user-schema.sql`) - 2个表
**文件路径**: `zinfra/database/data/user/user-schema.sql`

#### 核心表结构

##### `sys_user` - 系统用户表（核心表）
```sql
-- 主要字段
user_id VARCHAR(32)        -- 用户ID（主键）
phone VARCHAR(20)          -- 手机号（唯一）
wechat_id VARCHAR(255)     -- 微信用户ID（唯一）
youzan_id VARCHAR(255)     -- 有赞用户ID（唯一）
username VARCHAR(50)       -- 用户名（唯一）
password VARCHAR(100)      -- 密码(BCrypt加密)
user_type INT              -- 用户类型：1-管理员，2-发布者，3-接受者
status INT                 -- 状态：1-正常，2-禁用，3-待审核，4-已删除
```

**用户类型说明**：
- `1` - 系统管理员（最高权限）
- `2` - 发布者（可以发布任务）
- `3` - 接受者（可以接受任务）

**用户状态说明**：
- `1` - 正常
- `2` - 禁用
- `3` - 待审核
- `4` - 已删除

##### `user_profile` - 用户扩展信息表
```sql
-- 主要字段
profile_id VARCHAR(32)     -- 扩展信息ID（主键）
real_name VARCHAR(50)      -- 真实姓名
company_name VARCHAR(100)  -- 公司名称
contact_phone VARCHAR(20)  -- 联系电话
description TEXT           -- 个人简介
```

### 2. 发布者模块 (`publisher-schema.sql`) - 4个表
**文件路径**: `zinfra/database/data/publisher/publisher-schema.sql`

#### 核心表结构

##### `publisher_task` - 任务主表
```sql
-- 主要字段
task_id VARCHAR(32)        -- 任务ID（主键）
task_name VARCHAR(100)     -- 任务名称
task_type_id INT           -- 任务类型：1-点赞，2-评论，3-讨论，4-分享，5-邀请，6-反馈，7-排行榜
task_reward DECIMAL(10,2)  -- 任务积分
status_id INT              -- 任务状态：1-草稿，2-上架，3-下架，4-审核通过，5-审核不通过
```

##### `publisher_task_detail` - 任务详情表
```sql
-- 主要字段
detail_id VARCHAR(32)      -- 详情ID（主键）
task_id VARCHAR(32)        -- 任务ID（唯一）
task_config JSON           -- 任务配置JSON
```

##### `publisher_task_completion` - 任务完成流水表
```sql
-- 主要字段
completion_id VARCHAR(32)  -- 完成记录ID（主键）
task_id VARCHAR(32)        -- 任务ID
user_id VARCHAR(32)        -- 完成用户ID
completion_status INT      -- 完成状态：1-进行中，2-已完成，3-已拒绝
reward_amount DECIMAL(10,2) -- 获得奖励金额
completion_evidence JSON   -- 完成证据（截图、链接等）
```

##### `publisher_share_review` - 社群分享审核表
```sql
-- 主要字段
share_review_id VARCHAR(32) -- 分享审核ID（主键）
task_id VARCHAR(32)        -- 任务ID
share_content TEXT         -- 分享内容
share_platform VARCHAR(50) -- 分享平台
review_status_id INT       -- 审核状态ID
```

### 3. 第三方OSS模块 (`oss-schema.sql`) - 2个表
**文件路径**: `zinfra/database/data/third-party/oss-schema.sql`

#### 核心表结构

##### `oss_file_storage` - 文件存储表
```sql
-- 主要字段
file_id VARCHAR(32)        -- 文件ID（主键）
file_name VARCHAR(255)     -- 文件名
file_path VARCHAR(500)     -- 文件路径
file_size BIGINT          -- 文件大小（字节）
file_type VARCHAR(100)     -- 文件类型
oss_url VARCHAR(500)       -- OSS访问URL
upload_time DATETIME       -- 上传时间
upload_user_id VARCHAR(32) -- 上传用户ID
```

##### `oss_file_access_log` - 文件访问日志表
```sql
-- 主要字段
log_id VARCHAR(32)         -- 日志ID（主键）
file_id VARCHAR(32)        -- 文件ID
access_user_id VARCHAR(32) -- 访问用户ID
access_time DATETIME       -- 访问时间
access_type INT            -- 访问类型：1-下载，2-预览，3-删除
ip_address VARCHAR(45)     -- 访问IP地址
user_agent TEXT            -- 用户代理
```

## 🔗 表关联关系

### 用户模块关联
```
sys_user (1) ←→ (1) user_profile
    ↓
publisher_task_completion (1) ←→ (N) sys_user
    ↓
publisher_task (1) ←→ (N) publisher_task_completion
```

### 发布者模块关联
```
publisher_task (1) ←→ (1) publisher_task_detail
publisher_task (1) ←→ (N) publisher_task_completion
publisher_task (1) ←→ (N) publisher_share_review
```

### OSS模块关联
```
oss_file_storage (1) ←→ (N) oss_file_access_log
oss_file_storage ←→ sys_user (通过upload_user_id)
oss_file_access_log ←→ sys_user (通过access_user_id)
```

## 📊 索引设计

### 用户模块索引
#### `sys_user` 表
- `PRIMARY KEY` (`user_id`)
- `UNIQUE KEY` `uk_phone` (`phone`)
- `UNIQUE KEY` `uk_wechat_id` (`wechat_id`)
- `UNIQUE KEY` `uk_youzan_id` (`youzan_id`)
- `UNIQUE KEY` `uk_username` (`username`)
- `KEY` `idx_user_type` (`user_type`)
- `KEY` `idx_status` (`status`)

#### `user_profile` 表
- `PRIMARY KEY` (`profile_id`)

### 发布者模块索引
#### `publisher_task` 表
- `PRIMARY KEY` (`task_id`)
- `KEY` `idx_task_type` (`task_type_id`)
- `KEY` `idx_status` (`status_id`)
- `KEY` `idx_created_time` (`created_time`)

#### `publisher_task_detail` 表
- `PRIMARY KEY` (`detail_id`)
- `UNIQUE KEY` `uk_task_id` (`task_id`)

#### `publisher_task_completion` 表
- `PRIMARY KEY` (`completion_id`)
- `KEY` `idx_task_id` (`task_id`)
- `KEY` `idx_user_id` (`user_id`)
- `KEY` `idx_completion_status` (`completion_status`)
- `KEY` `idx_created_time` (`created_time`)

#### `publisher_share_review` 表
- `PRIMARY KEY` (`share_review_id`)
- `KEY` `idx_task_id` (`task_id`)
- `KEY` `idx_review_status` (`review_status_id`)
- `KEY` `idx_created_time` (`created_time`)

### OSS模块索引
#### `oss_file_storage` 表
- `PRIMARY KEY` (`file_id`)
- `KEY` `idx_upload_user_id` (`upload_user_id`)
- `KEY` `idx_upload_time` (`upload_time`)
- `KEY` `idx_file_type` (`file_type`)

#### `oss_file_access_log` 表
- `PRIMARY KEY` (`log_id`)
- `KEY` `idx_file_id` (`file_id`)
- `KEY` `idx_access_user_id` (`access_user_id`)
- `KEY` `idx_access_time` (`access_time`)
- `KEY` `idx_access_type` (`access_type`)

## 📋 标准字段说明

所有表都包含以下标准字段（遵循 `.docs/RULES/DB-RULES.md` 规范）：

```sql
`created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
`updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
`created_by` VARCHAR(32) DEFAULT NULL COMMENT '创建人ID',
`updated_by` VARCHAR(32) DEFAULT NULL COMMENT '更新人ID',
`deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除'
```

## 📈 数据量预估

### 用户模块
- `sys_user`: 预计10万用户
- `user_profile`: 预计8万用户档案

### 发布者模块
- `publisher_task`: 预计1万任务
- `publisher_task_detail`: 预计1万任务详情
- `publisher_task_completion`: 预计50万完成记录
- `publisher_share_review`: 预计5万分享审核

### OSS模块
- `oss_file_storage`: 预计10万文件
- `oss_file_access_log`: 预计100万访问记录

## 🔄 数据迁移策略

### 版本管理
- 使用SQL脚本进行版本管理
- 每个版本都有对应的升级脚本
- 支持向前兼容

### 备份策略
- 每日全量备份
- 每小时增量备份
- 重要操作前手动备份

### 恢复策略
- 支持时间点恢复
- 支持单表恢复
- 支持跨环境数据同步

## 📚 相关文档

- **开发规则**: `.docs/RULES/DB-RULES.md` - 数据库开发规则和约束
- **原始规范**: `zinfra/database/MySQL数据开发规范.md` - MySQL数据开发规范
- **模型说明**: `zinfra/database/DATABASE-MODEL-SUMMARY.md` - 数据库模型设计说明

---

**版本**: v2.1  
**创建日期**: 2025-08-04  
**最后更新**: 2025-08-04  
**维护者**: scccy  
**来源**: `zinfra/database/MySQL数据开发规范.md` 
# 数据库模型设计说明

## 📊 项目概述

本项目采用微服务架构，数据库设计遵循以下原则：
- **模块化设计**：每个微服务模块独立管理自己的数据表
- **简化权限**：使用用户类型字段替代复杂的RBAC权限系统
- **统一规范**：所有表遵循统一的命名和字段规范
- **性能优化**：合理的索引设计，支持高效查询

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

### 3. 第三方OSS模块 (`oss-schema.sql`) - 1个表
**文件路径**: `zinfra/database/data/third-party/oss-schema.sql`

#### 核心表结构

##### `oss_file_storage` - 文件存储记录表
```sql
-- 主要字段
storage_id VARCHAR(32)     -- 存储记录ID（主键）
original_name VARCHAR(255) -- 原始文件名
file_size BIGINT           -- 文件大小(字节)
object_key VARCHAR(500)    -- OSS对象键（唯一）
access_url VARCHAR(500)    -- 文件访问URL
source_service VARCHAR(50) -- 来源服务(如:core-publisher)
business_type VARCHAR(50)  -- 业务类型(如:task-image)
user_id VARCHAR(32)        -- 上传用户ID
```



## 🔧 设计特点

### 1. 权限控制简化
- **去除了复杂的RBAC系统**：不再需要角色表、权限表、角色权限关联表
- **使用用户类型字段**：通过 `user_type` 字段直接控制用户权限
- **权限检查简单**：使用 `UserPermissionUtil` 工具类进行权限验证

### 2. 统一命名规范
- **主键命名**：使用 `表名_id` 格式（如：`user_id`, `task_id`, `storage_id`）
- **字段命名**：使用下划线命名法，避免驼峰命名
- **索引命名**：使用 `idx_` 前缀，唯一索引使用 `uk_` 前缀

### 3. 数据类型优化
- **ID字段**：统一使用 `VARCHAR(32)` 类型，支持分布式ID生成
- **状态字段**：使用 `INT` 类型，便于扩展和维护
- **时间字段**：使用 `DATETIME` 类型，支持时区处理

### 4. 关联逻辑设计 ⭐
- **正确关联**：左表（主表）存在右表ID，而不是右表有左表ID
- **一对一关联**：`sys_user.profile_id` → `user_profile.profile_id`
- **一对多关联**：`publisher_task_completion.task_id` → `publisher_task.task_id`
- **用户关联**：`oss_file_storage.user_id` → `sys_user.user_id`

### 5. 索引设计
- **主键索引**：所有表都有主键索引
- **唯一索引**：用户名、手机号、OSS对象键等字段设置唯一索引
- **普通索引**：状态、类型、时间等常用查询字段设置索引
- **复合索引**：针对复杂查询场景设计复合索引

## 📋 数据库初始化步骤

### 1. 创建数据库
```sql
CREATE DATABASE banyu_mall CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_bin;
```

### 2. 执行SQL脚本
```bash
# 用户模块
mysql -u root -p banyu_mall < zinfra/database/data/user/user-schema.sql

# 发布者模块
mysql -u root -p banyu_mall < zinfra/database/data/publisher/publisher-schema.sql

# 第三方OSS模块
mysql -u root -p banyu_mall < zinfra/database/data/third-party/oss-schema.sql
```

### 3. 验证初始化
```sql
-- 检查用户表
SELECT COUNT(*) FROM sys_user;

-- 检查任务表
SELECT COUNT(*) FROM publisher_task;

-- 检查文件存储表
SELECT COUNT(*) FROM oss_file_storage;
```

## 🔍 查询示例

### 1. 用户权限查询
```sql
-- 查询所有发布者
SELECT * FROM sys_user WHERE user_type = 2 AND status = 1;

-- 查询所有管理员
SELECT * FROM sys_user WHERE user_type = 1 AND status = 1;
```

### 2. 任务管理查询
```sql
-- 查询上架的任务
SELECT * FROM publisher_task WHERE status_id = 2;

-- 查询任务完成情况
SELECT t.task_name, COUNT(c.completion_id) as completion_count
FROM publisher_task t
LEFT JOIN publisher_task_completion c ON t.task_id = c.task_id
WHERE t.status_id = 2
GROUP BY t.task_id;
```

### 3. 文件存储查询
```sql
-- 查询指定服务的文件
SELECT * FROM oss_file_storage 
WHERE source_service = 'core-publisher' 
AND business_type = 'task-image';

-- 查询用户上传的文件
SELECT s.*, u.username, u.nickname
FROM oss_file_storage s
LEFT JOIN sys_user u ON s.user_id = u.user_id
WHERE s.user_id = 'user_001';
```

### 4. 用户信息关联查询
```sql
-- 查询用户及其扩展信息
SELECT u.*, p.real_name, p.company_name, p.description
FROM sys_user u
LEFT JOIN user_profile p ON u.profile_id = p.profile_id
WHERE u.status = 1;
```

## 📈 性能优化建议

### 1. 索引优化
- 定期分析慢查询日志，优化索引设计
- 对于大表，考虑分区表设计
- 避免在索引字段上使用函数

### 2. 查询优化
- 使用 `EXPLAIN` 分析查询执行计划
- 避免 `SELECT *`，只查询需要的字段
- 合理使用分页查询，避免大量数据返回

### 3. 数据维护
- 定期清理过期数据（如登录日志）
- 监控表大小，及时归档历史数据
- 定期更新表统计信息

---

**最后更新**: 2025-01-27  
**版本**: v2.0  
**作者**: scccy 
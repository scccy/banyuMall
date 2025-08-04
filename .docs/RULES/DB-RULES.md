# 数据库开发规则

## 📋 规则概述

**ID**: DB-RULES-001  
**Name**: 数据库开发规范  
**Status**: Active  
**创建时间**: 2025-08-04  

## 🎯 核心原则

### 1. 命名规范
- **数据库名**: 使用小写字母，单词间用下划线分隔，如：`banyu`
- **表名**: 使用小写字母，单词间用下划线分隔，如：`sys_user`
- **字段名**: 使用小写字母，单词间用下划线分隔，如：`user_id`
- **索引名**: 普通索引使用 `idx_` 前缀，唯一索引使用 `uk_` 前缀

### 2. 主键设计规范 ⭐
- **主键命名**: **必须 (MUST)** 使用 `表名_id` 格式，如：`user_id`, `task_id`, `storage_id`
- **主键类型**: **必须 (MUST)** 使用 `VARCHAR(32)` 类型，支持分布式ID生成
- **主键约束**: **必须 (MUST)** 设置 `NOT NULL` 和 `PRIMARY KEY`

### 3. 字段类型规范
- **主键ID**: 统一使用 `VARCHAR(32)` 类型
- **状态字段**: 使用 `INT` 类型，便于扩展和维护
- **时间字段**: 使用 `DATETIME` 类型，支持时区处理
- **金额字段**: 使用 `DECIMAL(10,2)` 类型，保证精度
- **文本字段**: 根据长度选择 `VARCHAR` 或 `TEXT`
- **逻辑删除**: 使用 `TINYINT(1)` 类型，0-未删除，1-已删除

## 🔗 关联逻辑规范 ⭐

### 重要原则：左表（主表）存在右表ID，而不是右表有左表ID

#### 一对一关联
```sql
-- 正确：主表包含关联字段
CREATE TABLE `sys_user` (
    `user_id` VARCHAR(32) NOT NULL,
    `profile_id` VARCHAR(32) DEFAULT NULL,  -- 关联user_profile表
    PRIMARY KEY (`user_id`)
);

CREATE TABLE `user_profile` (
    `profile_id` VARCHAR(32) NOT NULL,
    `real_name` VARCHAR(50),
    PRIMARY KEY (`profile_id`)
);
```

#### 一对多关联
```sql
-- 正确：从表包含主表ID
CREATE TABLE `publisher_task` (
    `task_id` VARCHAR(32) NOT NULL,
    `task_name` VARCHAR(100),
    PRIMARY KEY (`task_id`)
);

CREATE TABLE `publisher_task_completion` (
    `completion_id` VARCHAR(32) NOT NULL,
    `task_id` VARCHAR(32) NOT NULL,  -- 关联publisher_task表
    `user_id` VARCHAR(32) NOT NULL,  -- 关联sys_user表
    PRIMARY KEY (`completion_id`)
);
```

#### 多对多关联
```sql
-- 正确：中间表包含两个主表ID
CREATE TABLE `sys_user` (
    `user_id` VARCHAR(32) NOT NULL,
    PRIMARY KEY (`user_id`)
);

CREATE TABLE `sys_role` (
    `role_id` VARCHAR(32) NOT NULL,
    PRIMARY KEY (`role_id`)
);

CREATE TABLE `sys_user_role` (
    `id` VARCHAR(32) NOT NULL,
    `user_id` VARCHAR(32) NOT NULL,  -- 关联sys_user表
    `role_id` VARCHAR(32) NOT NULL,  -- 关联sys_role表
    PRIMARY KEY (`id`)
);
```

## 📊 索引设计规范

### 索引类型
- **主键索引**: 所有表都必须有主键索引
- **唯一索引**: 用户名、手机号、邮箱等唯一字段设置唯一索引
- **普通索引**: 状态、类型、时间等常用查询字段设置索引
- **复合索引**: 针对复杂查询场景设计复合索引

### 索引命名
- **普通索引**: `idx_字段名` 格式
- **唯一索引**: `uk_字段名` 格式
- **复合索引**: `idx_字段1_字段2` 格式

## 🏗️ 表结构规范

### 基础配置
- **字符集**: 统一使用 `utf8mb4`
- **排序规则**: 统一使用 `utf8mb4_0900_bin`
- **存储引擎**: 统一使用 `InnoDB`
- **注释**: 表名和字段名都必须有中文注释

### 标准字段
每个表都应该包含以下标准字段：

```sql
`created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
`updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
`created_by` VARCHAR(32) DEFAULT NULL COMMENT '创建人ID',
`updated_by` VARCHAR(32) DEFAULT NULL COMMENT '更新人ID',
`deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除'
```

## 🔧 建表示例

### 标准表结构模板
```sql
CREATE TABLE `table_name` (
    `table_id` VARCHAR(32) NOT NULL COMMENT '主键ID',
    `field_name` VARCHAR(100) NOT NULL COMMENT '字段说明',
    `status` INT NOT NULL DEFAULT 1 COMMENT '状态：1-正常，2-禁用',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `created_by` VARCHAR(32) DEFAULT NULL COMMENT '创建人ID',
    `updated_by` VARCHAR(32) DEFAULT NULL COMMENT '更新人ID',
    `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`table_id`),
    UNIQUE KEY `uk_field_name` (`field_name`),
    KEY `idx_status` (`status`),
    KEY `idx_created_time` (`created_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_bin COMMENT='表说明';
```

## 🚫 禁止事项

### 严格禁止
1. **禁止使用外键约束** - 通过应用层控制数据一致性
2. **禁止使用触发器** - 影响性能和维护性
3. **禁止使用存储过程** - 影响可移植性
4. **禁止使用视图** - 影响性能
5. **禁止使用临时表** - 影响性能
6. **禁止使用游标** - 影响性能
7. **禁止使用动态SQL** - 影响安全性

### 不推荐
1. **避免使用VARCHAR存储枚举** - 使用INT类型
2. **避免使用TEXT存储小数据** - 优先使用VARCHAR
3. **避免使用BLOB存储文件** - 使用文件存储服务

## ✅ 推荐事项

### 最佳实践
1. **使用逻辑删除** - 通过 `deleted` 字段标记删除状态
2. **使用软时间戳** - 通过 `created_time` 和 `updated_time` 记录时间
3. **使用审计字段** - 通过 `created_by` 和 `updated_by` 记录操作人
4. **使用状态字段** - 通过 `status` 字段管理记录状态
5. **使用类型字段** - 通过 `type` 字段区分不同类型

### 性能优化
1. **合理使用索引** - 避免过度索引
2. **优化查询语句** - 避免SELECT *
3. **使用分页查询** - 大数据量使用LIMIT
4. **避免深度分页** - 使用游标分页

## 📊 查询优化建议

### 索引使用
- 避免在索引字段上使用函数
- 避免使用 `SELECT *`，只查询需要的字段
- 合理使用复合索引，注意字段顺序

### 关联查询
- 使用 `INNER JOIN` 进行内连接
- 使用 `LEFT JOIN` 进行左连接
- 避免使用 `RIGHT JOIN`
- 关联条件要使用索引字段

### 分页查询
- 使用 `LIMIT` 进行分页
- 大数据量分页使用游标分页
- 避免使用 `OFFSET` 进行深度分页

## 🔄 模块对应规则

### 用户模块 (user-schema.sql)
- **表前缀**: `sys_`, `user_`
- **核心表**: `sys_user`, `user_profile`
- **关联规则**: 用户扩展信息通过 `profile_id` 关联

### 发布者模块 (publisher-schema.sql)
- **表前缀**: `publisher_`
- **核心表**: `publisher_task`, `publisher_task_detail`, `publisher_task_completion`, `publisher_share_review`
- **关联规则**: 任务完成记录通过 `task_id` 关联任务表

### 第三方OSS模块 (oss-schema.sql)
- **表前缀**: `oss_`
- **核心表**: `oss_file_storage`, `oss_file_access_log`
- **关联规则**: 文件访问日志通过 `file_id` 关联文件存储表

## 📝 文档维护

### SQL文件组织
```
zinfra/database/data/
├── user/
│   └── user-schema.sql
├── publisher/
│   └── publisher-schema.sql
└── third-party/
    └── oss-schema.sql
```

### 文档更新
- 新增表结构 → 更新对应的schema.sql文件
- 字段变更 → 更新表结构注释
- 索引变更 → 更新索引说明

---

**版本**: v1.0  
**创建日期**: 2025-08-04  
**最后更新**: 2025-08-04  
**维护者**: scccy  
**来源**: `zinfra/database/MySQL数据开发规范.md` 
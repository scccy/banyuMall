# MySQL数据开发规范

## 📋 基本原则

### 1. 命名规范
- **数据库名**：使用小写字母，单词间用下划线分隔，如：`banyu`
- **表名**：使用小写字母，单词间用下划线分隔，如：`sys_user`
- **字段名**：使用小写字母，单词间用下划线分隔，如：`user_id`
- **索引名**：普通索引使用 `idx_` 前缀，唯一索引使用 `uk_` 前缀

### 2. 字段类型规范
- **主键ID**：统一使用 `VARCHAR(32)` 类型，支持分布式ID生成
- **状态字段**：使用 `INT` 类型，便于扩展和维护
- **时间字段**：使用 `DATETIME` 类型，支持时区处理
- **金额字段**：使用 `DECIMAL(10,2)` 类型，保证精度
- **文本字段**：根据长度选择 `VARCHAR` 或 `TEXT`
- **逻辑删除**：使用 `TINYINT(1)` 类型，0-未删除，1-已删除

### 3. 主键设计规范
- **主键命名**：使用 `表名_id` 格式，如：`user_id`, `task_id`, `storage_id`
- **主键类型**：统一使用 `VARCHAR(32)` 类型
- **主键约束**：必须设置 `NOT NULL` 和 `PRIMARY KEY`

### 4. 关联逻辑规范 ⭐
**重要原则：左表（主表）存在右表ID，而不是右表有左表ID**

#### 4.1 一对一关联
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

#### 4.2 一对多关联
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

#### 4.3 多对多关联
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

### 5. 索引设计规范
- **主键索引**：所有表都必须有主键索引
- **唯一索引**：用户名、手机号、邮箱等唯一字段设置唯一索引
- **普通索引**：状态、类型、时间等常用查询字段设置索引
- **复合索引**：针对复杂查询场景设计复合索引
- **索引命名**：`idx_字段名` 或 `uk_字段名`

### 6. 表结构规范
- **字符集**：统一使用 `utf8mb4`
- **排序规则**：统一使用 `utf8mb4_0900_bin`
- **存储引擎**：统一使用 `InnoDB`
- **注释**：表名和字段名都必须有中文注释

### 7. 字段约束规范
- **非空约束**：主键和必填字段设置 `NOT NULL`
- **默认值**：为字段设置合理的默认值
- **外键约束**：不建议使用外键约束，通过应用层控制

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

## 📊 查询优化建议

### 1. 索引使用
- 避免在索引字段上使用函数
- 避免使用 `SELECT *`，只查询需要的字段
- 合理使用复合索引，注意字段顺序

### 2. 关联查询
- 使用 `INNER JOIN` 进行内连接
- 使用 `LEFT JOIN` 进行左连接
- 避免使用 `RIGHT JOIN`
- 关联条件要使用索引字段

### 3. 分页查询
- 使用 `LIMIT` 进行分页
- 大数据量分页使用游标分页
- 避免使用 `OFFSET` 进行深度分页

## 🚫 禁止事项

1. **禁止使用外键约束**
2. **禁止使用触发器**
3. **禁止使用存储过程**
4. **禁止使用视图**
5. **禁止使用临时表**
6. **禁止使用游标**
7. **禁止使用动态SQL**

## ✅ 推荐事项

1. **使用逻辑删除**：通过 `deleted` 字段标记删除状态
2. **使用软时间戳**：通过 `created_time` 和 `updated_time` 记录时间
3. **使用审计字段**：通过 `created_by` 和 `updated_by` 记录操作人
4. **使用状态字段**：通过 `status` 字段管理记录状态
5. **使用类型字段**：通过 `type` 字段区分不同类型

---

**版本**: v2.0  
**更新日期**: 2025-01-27  
**作者**: scccy
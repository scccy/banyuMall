# 数据库初始化指南

## 概述

本指南说明如何在开发和生产环境中初始化BanyuMall项目的数据库。

## 开发环境初始化

### 自动初始化（推荐）

开发环境已配置自动执行SQL文件：

```yaml
# service-auth 开发环境配置
spring:
  sql:
    init:
      mode: always
      schema-locations: classpath:db/user-schema.sql
      data-locations: classpath:db/user-init-data.sql
      continue-on-error: true
```

**启动顺序**：
1. 启动 `service-auth` 服务（自动执行SQL初始化）
2. 启动 `service-user` 服务（只读取数据）

### 手动初始化

如果自动初始化失败，可以手动执行：

```sql
-- 1. 连接到MySQL数据库
mysql -u root -p banyu_mall

-- 2. 执行表结构创建
source /path/to/banyuMall/service/service-auth/src/main/resources/db/user-schema.sql;

-- 3. 执行初始化数据
source /path/to/banyuMall/service/service-auth/src/main/resources/db/user-init-data.sql;
```

## 生产环境初始化

### 手动执行（推荐）

生产环境建议手动执行SQL文件，确保数据安全：

```bash
# 1. 备份现有数据库（如果有）
mysqldump -u root -p banyu_mall > backup_$(date +%Y%m%d_%H%M%S).sql

# 2. 执行初始化脚本
mysql -u root -p banyu_mall < service/service-auth/src/main/resources/db/user-schema.sql
mysql -u root -p banyu_mall < service/service-auth/src/main/resources/db/user-init-data.sql
```

### 配置说明

生产环境配置：
```yaml
spring:
  sql:
    init:
      mode: never  # 生产环境禁用自动初始化
```

## 验证初始化结果

### 检查表结构
```sql
-- 查看所有表
SHOW TABLES;

-- 检查用户表结构
DESCRIBE sys_user;

-- 检查角色表结构
DESCRIBE sys_role;

-- 检查权限表结构
DESCRIBE sys_permission;
```

### 检查初始化数据
```sql
-- 检查用户数据
SELECT id, username, nickname, status FROM sys_user;

-- 检查角色数据
SELECT id, name, code, status FROM sys_role;

-- 检查权限数据
SELECT id, name, code, type FROM sys_permission;

-- 检查用户角色关联
SELECT u.username, r.name as role_name 
FROM sys_user u 
JOIN sys_user_role ur ON u.id = ur.user_id 
JOIN sys_role r ON ur.role_id = r.id;
```

## 默认数据说明

### 默认用户
- **用户名**: admin
- **密码**: 123456
- **角色**: 超级管理员
- **状态**: 正常

### 默认角色
1. **ROLE_ADMIN**: 超级管理员（拥有所有权限）
2. **ROLE_USER**: 普通用户（基本查看权限）
3. **ROLE_PUBLISHER**: 任务发布者（任务管理权限）

### 权限体系
- **系统管理**: 用户、角色、权限管理
- **任务管理**: 任务列表、发布、编辑、删除

## 故障排除

### 1. 表已存在错误
```sql
-- 如果表已存在，可以先删除
DROP TABLE IF EXISTS sys_user, sys_role, sys_user_role, sys_permission, sys_role_permission, user_profile, user_config;
```

### 2. 外键约束错误
```sql
-- 检查外键约束
SELECT * FROM information_schema.KEY_COLUMN_USAGE 
WHERE TABLE_SCHEMA = 'banyu_mall' AND REFERENCED_TABLE_NAME IS NOT NULL;
```

### 3. 数据重复错误
初始化脚本使用了 `ON DUPLICATE KEY UPDATE`，会自动处理重复数据。

### 4. 权限不足
确保数据库用户有足够的权限：
```sql
-- 授予必要权限
GRANT ALL PRIVILEGES ON banyu_mall.* TO 'your_user'@'localhost';
FLUSH PRIVILEGES;
```

## 数据迁移

### 从旧版本迁移
1. **备份现有数据**
2. **执行新表结构**
3. **迁移用户数据**
4. **迁移角色权限数据**
5. **验证数据完整性**

### 迁移脚本示例
```sql
-- 迁移用户数据（示例）
INSERT INTO sys_user (id, username, password, nickname, email, phone, status, user_type)
SELECT id, username, password, nickname, email, phone, status, user_type
FROM old_user_table
ON DUPLICATE KEY UPDATE
  nickname = VALUES(nickname),
  email = VALUES(email),
  phone = VALUES(phone),
  status = VALUES(status);
```

## 注意事项

### 1. 数据安全
- 生产环境务必备份数据
- 使用强密码
- 定期备份数据库

### 2. 性能考虑
- 大表添加适当的索引
- 定期清理无用数据
- 监控数据库性能

### 3. 版本控制
- SQL文件纳入版本控制
- 记录数据库变更历史
- 使用数据库迁移工具（如Flyway）

## 相关文档

- [统一SQL文件管理说明](./UNIFIED-SQL-MANAGEMENT.md)
- [Spring Security配置说明](./SPRING-SECURITY-SETUP.md)
- [API文档配置说明](./API-DOCS-SETUP.md) 
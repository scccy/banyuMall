# 统一SQL文件管理说明

## 概述

为了统一管理用户和权限相关的数据库结构，避免重复定义和冲突，我们将所有用户相关的SQL文件统一到 `service/service-auth/src/main/resources/db/` 目录下。

## 文件结构

### 统一后的SQL文件
```
service/service-auth/src/main/resources/db/
├── user-schema.sql    # 统一用户表结构
└── user-init-data.sql      # 统一初始化数据
```

### 已删除的重复文件
- ❌ `service/service-auth/src/main/resources/db/auth-schema.sql`
- ❌ `service/service-auth/src/main/resources/db/init.sql`
- ❌ `service/service-user/src/main/resources/db/user-schema.sql`

## 表结构说明

### 1. 共享表（service-auth和service-user共同使用）

#### sys_user - 系统用户表
- **用途**: 存储用户基本信息
- **管理**: service-auth负责认证相关字段，service-user负责扩展信息
- **关键字段**: 
  - `password`: 由service-auth管理
  - `last_login_time`: 由service-auth管理
  - `user_type`: 用户类型标识

#### sys_role - 系统角色表
- **用途**: 存储角色信息
- **管理**: service-auth统一管理

#### sys_user_role - 用户角色关联表
- **用途**: 用户与角色的多对多关系
- **管理**: service-auth统一管理

#### sys_permission - 系统权限表
- **用途**: 存储权限信息
- **管理**: service-auth统一管理

#### sys_role_permission - 角色权限关联表
- **用途**: 角色与权限的多对多关系
- **管理**: service-auth统一管理

### 2. 专用表（service-user使用）

#### user_profile - 用户扩展信息表
- **用途**: 存储用户扩展信息
- **管理**: service-user管理
- **外键**: 关联sys_user表

#### user_config - 用户配置表
- **用途**: 存储用户个性化配置
- **管理**: service-user管理
- **外键**: 关联sys_user表

## 数据初始化

### 默认用户
- **admin**: 系统管理员，密码123456
- **test**: 测试用户，密码123456

### 默认角色
- **ROLE_ADMIN**: 超级管理员
- **ROLE_USER**: 普通用户
- **ROLE_PUBLISHER**: 任务发布者

### 权限体系
- **系统管理**: 用户、角色、权限管理
- **任务管理**: 任务列表、发布、编辑、删除

## 使用方式

### 1. 数据库初始化
```sql
-- 执行表结构创建
source user-schema.sql;

-- 执行初始化数据
source user-init-data.sql;
```

### 2. 服务配置
确保两个服务都指向同一个数据库，并正确配置数据源。

### 3. 权限控制
- service-auth负责认证和基础权限控制
- service-user负责业务权限控制

## 优势

### 1. 统一管理
- ✅ 避免表结构重复定义
- ✅ 统一数据初始化
- ✅ 减少维护成本

### 2. 数据一致性
- ✅ 用户信息统一存储
- ✅ 权限体系统一管理
- ✅ 避免数据冲突

### 3. 模块职责清晰
- ✅ service-auth: 认证和基础权限
- ✅ service-user: 用户扩展信息和业务权限

## 注意事项

### 1. 数据库连接
确保两个服务使用相同的数据库连接配置。

### 2. 事务管理
涉及跨服务的数据操作需要特别注意事务一致性。

### 3. 数据同步
如果使用分布式数据库，需要确保数据同步策略。

### 4. 备份策略
统一的数据需要统一的备份和恢复策略。

## 迁移指南

### 从旧版本迁移
1. 备份现有数据
2. 执行新的表结构创建脚本
3. 迁移现有数据到新结构
4. 更新服务配置
5. 验证数据一致性

### 数据验证
```sql
-- 检查用户数据
SELECT COUNT(*) FROM sys_user;

-- 检查角色数据
SELECT COUNT(*) FROM sys_role;

-- 检查权限数据
SELECT COUNT(*) FROM sys_permission;

-- 检查关联关系
SELECT COUNT(*) FROM sys_user_role;
SELECT COUNT(*) FROM sys_role_permission;
```

## 故障排除

### 1. 表不存在
- 检查是否执行了表结构创建脚本
- 确认数据库连接配置正确

### 2. 数据不一致
- 检查初始化脚本是否完整执行
- 验证外键约束是否正确

### 3. 权限问题
- 确认用户角色关联正确
- 检查权限配置是否完整

## 相关文档

- [Spring Security配置说明](./SPRING-SECURITY-SETUP.md)
- [API文档配置说明](./API-DOCS-SETUP.md)
- [安全配置说明](./SECURITY-SETUP.md) 
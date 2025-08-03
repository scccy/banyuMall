# 数据库统一管理目录

## 概述

本目录统一管理BanyuMall项目的所有数据库相关文件，包括表结构、初始化数据、迁移脚本等。

## 目录结构

```
zinfra/database/
├── README.md                    # 本说明文档
├── MySQL数据开发规范.md          # MySQL数据开发规范
├── schema/                      # 表结构定义
│   ├── user-schema.sql  # 统一用户表结构
│   └── README.md               # 表结构说明
├── data/                        # 初始化数据
│   ├── user-init-data.sql    # 统一初始化数据
│   └── README.md               # 数据说明
├── migration/                   # 数据库迁移脚本
│   ├── V1.0.0__init.sql        # 初始版本
│   └── README.md               # 迁移说明
└── scripts/                     # 数据库管理脚本
    ├── init-database.sh        # 数据库初始化脚本
    ├── backup-database.sh      # 数据库备份脚本
    └── README.md               # 脚本说明
```

## 开发规范

### MySQL数据开发规范
所有数据库开发工作必须遵循 [MySQL数据开发规范](./MySQL数据开发规范.md)，包括：
- 命名规范（数据库、表、字段、索引）
- 表结构设计规范
- 索引设计原则
- 性能优化指南
- 安全规范要求
- 开发流程规范

### 建表逻辑约束
- **通用字段**: 所有表必须包含id、created_time、updated_time、deleted字段
- **主键设计**: 统一使用VARCHAR(32)类型，雪花算法生成UUID
- **软删除**: 所有重要数据使用软删除机制
- **字段注释**: 每个字段必须有中文注释
- **索引设计**: 遵循索引命名规范和设计原则

## 使用方式

### 1. 开发环境初始化
```bash
# 执行数据库初始化
./zinfra/database/scripts/init-database.sh
```

### 2. 生产环境部署
```bash
# 备份现有数据库
./zinfra/database/scripts/backup-database.sh

# 执行迁移脚本
mysql -u root -p banyu_mall < zinfra/database/migration/V1.0.0__init.sql
```

### 3. 服务配置
各服务需要更新配置文件中的SQL文件路径：

```yaml
spring:
  sql:
    init:
      schema-locations: classpath:../../zinfra/database/schema/user-schema.sql
      data-locations: classpath:../../zinfra/database/data/user-init-data.sql
```

## 版本管理

- 使用语义化版本号管理数据库变更
- 每个版本对应一个迁移脚本
- 支持向前和向后兼容

## 相关文档

- [数据库初始化指南](../../DATABASE-INIT-GUIDE.md)
- [统一SQL文件管理说明](../../UNIFIED-SQL-MANAGEMENT.md) 
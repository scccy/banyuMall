# 数据库统一管理目录

## 概述

本目录统一管理BanyuMall项目的所有数据库相关文件，包括表结构、初始化数据、迁移脚本等。

## 目录结构

```
infra/database/
├── README.md                    # 本说明文档
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

## 使用方式

### 1. 开发环境初始化
```bash
# 执行数据库初始化
./infra/database/scripts/init-database.sh
```

### 2. 生产环境部署
```bash
# 备份现有数据库
./infra/database/scripts/backup-database.sh

# 执行迁移脚本
mysql -u root -p banyu_mall < infra/database/migration/V1.0.0__init.sql
```

### 3. 服务配置
各服务需要更新配置文件中的SQL文件路径：

```yaml
spring:
  sql:
    init:
      schema-locations: classpath:../../infra/database/schema/user-schema.sql
      data-locations: classpath:../../infra/database/data/user-init-data.sql
```

## 版本管理

- 使用语义化版本号管理数据库变更
- 每个版本对应一个迁移脚本
- 支持向前和向后兼容

## 相关文档

- [数据库初始化指南](../../DATABASE-INIT-GUIDE.md)
- [统一SQL文件管理说明](../../UNIFIED-SQL-MANAGEMENT.md) 
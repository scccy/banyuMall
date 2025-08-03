# 基础设施统一管理

## 概述

本目录统一管理BanyuMall项目的所有基础设施相关文件，包括数据库、模板、配置、网关等。

## 目录结构

```
zinfra/
├── README.md                    # 本说明文档
├── database/                    # 数据库管理
│   ├── README.md               # 数据库管理说明
│   ├── schema/                 # 表结构定义
│   ├── data/                   # 初始化数据
│   ├── migration/              # 数据库迁移脚本
│   └── scripts/                # 数据库管理脚本
├── templates/                   # 模板管理
│   ├── README.md               # 模板管理说明
│   ├── email/                  # 邮件模板
│   ├── notification/           # 通知模板
│   ├── document/               # 文档模板
│   └── config/                 # 配置模板
└── scripts/                    # 基础设施脚本
    ├── deploy.sh               # 部署脚本
    ├── backup.sh               # 备份脚本
    └── monitor.sh              # 监控脚本
└── scripts/                    # 基础设施脚本
    ├── deploy.sh               # 部署脚本
    ├── backup.sh               # 备份脚本
    └── monitor.sh              # 监控脚本
```

## 管理原则

### 1. 统一管理
- 所有基础设施文件集中管理
- 避免重复和冲突
- 统一版本控制

### 2. 环境隔离
- 开发、测试、生产环境配置分离
- 使用环境变量管理敏感信息
- 支持多环境部署

### 3. 自动化
- 提供自动化脚本
- 支持一键部署
- 自动化备份和恢复

## 使用方式

### 1. 数据库管理
```bash
# 初始化数据库
./zinfra/database/scripts/init-database.sh

# 备份数据库
./zinfra/database/scripts/backup-database.sh

# 清理旧备份
./zinfra/database/scripts/backup-database.sh --cleanup
```

### 2. 模板管理
```bash
# 生成配置
envsubst < zinfra/templates/config/nacos-config-template.yml > nacos-config.yml

# 使用邮件模板
java -cp . TemplateProcessor zinfra/templates/email/welcome.html
```

### 3. 网关部署
```bash
# 构建网关
cd service/service-gateway && mvn clean package

# 部署网关
./zinfra/scripts/deploy.sh gateway
```

## 配置管理

### 1. 环境变量
创建 `.env` 文件管理环境变量：
```bash
# 数据库配置
DB_HOST=localhost
DB_PORT=3306
DB_NAME=banyu_mall
DB_USERNAME=root
DB_PASSWORD=123456

# Redis配置
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=

# 服务配置
SERVER_PORT=8080
CONTEXT_PATH=/
```

### 2. 配置文件
- 使用模板生成配置文件
- 支持环境变量替换
- 避免硬编码敏感信息

### 3. 版本控制
- 配置文件纳入版本控制
- 敏感信息使用环境变量
- 记录配置变更历史

## 部署流程

### 1. 开发环境
```bash
# 1. 初始化数据库
./zinfra/database/scripts/init-database.sh

# 2. 启动服务
./mvnw spring-boot:run -pl service/service-auth
./mvnw spring-boot:run -pl service/service-user

# 3. 启动网关
./mvnw spring-boot:run -pl service/service-gateway
```

### 2. 生产环境
```bash
# 1. 备份现有数据
./zinfra/database/scripts/backup-database.sh

# 2. 部署服务
./zinfra/scripts/deploy.sh all

# 3. 验证部署
./zinfra/scripts/monitor.sh
```

## 监控和维护

### 1. 健康检查
```bash
# 检查服务状态
curl http://localhost:8080/actuator/health

# 检查数据库连接
./zinfra/database/scripts/check-database.sh
```

### 2. 日志管理
```bash
# 查看服务日志
tail -f logs/application.log

# 查看错误日志
tail -f logs/error.log
```

### 3. 性能监控
```bash
# 监控系统资源
./zinfra/scripts/monitor.sh --resources

# 监控数据库性能
./zinfra/scripts/monitor.sh --database
```

## 安全考虑

### 1. 敏感信息
- 使用环境变量管理密码
- 避免在代码中硬编码敏感信息
- 定期轮换密钥和密码

### 2. 访问控制
- 限制数据库访问权限
- 使用防火墙保护服务端口
- 实施最小权限原则

### 3. 备份策略
- 定期备份数据库
- 备份配置文件
- 测试恢复流程

## 故障排除

### 1. 常见问题
- 数据库连接失败
- 服务启动失败
- 配置加载错误

### 2. 排查步骤
1. 检查日志文件
2. 验证配置文件
3. 测试网络连接
4. 检查资源使用

### 3. 恢复流程
1. 停止故障服务
2. 恢复备份数据
3. 重新启动服务
4. 验证服务状态

## 最佳实践

### 1. 配置管理
- 使用配置模板
- 环境变量管理敏感信息
- 版本控制配置文件

### 2. 部署管理
- 自动化部署流程
- 蓝绿部署策略
- 回滚机制

### 3. 监控管理
- 实时监控服务状态
- 告警机制
- 性能指标收集

## 相关文档

- [数据库管理说明](./database/README.md)
- [模板管理说明](./templates/README.md)
- [网关服务说明](./infra-gateway/README.md)
- [部署指南](../DEPLOYMENT-GUIDE.md) 
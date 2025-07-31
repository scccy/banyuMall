# 配置文件管理说明

## 概述

本文档说明半语积分商城项目的配置文件管理策略，包括本地配置和云端Nacos配置的分离管理。

## 配置架构

### 1. 本地配置文件
- **application.yml**: 主配置文件，包含基础配置和Nacos连接配置
- **application-dev.yml**: 开发环境配置，包含本地开发所需的所有配置
- **application-prod.yml**: 生产环境配置，包含生产环境特定的配置

### 2. 云端Nacos配置
- **service-auth.yaml**: auth服务的云端配置，包含业务配置和敏感信息

## 配置分离策略

### 本地配置（application.yml）
```yaml
# 只包含基础配置
server:
  port: 8081
  servlet:
    context-path: /auth

spring:
  application:
    name: service-auth
  config:
    import: nacos:${spring.application.name}.${spring.cloud.nacos.config.file-extension}
  cloud:
    nacos:
      # Nacos连接配置
```

### 云端配置（service-auth.yaml）
```yaml
# 包含业务配置和敏感信息
spring:
  datasource:
    url: ${MYSQL_URL}
    username: ${MYSQL_USERNAME}
    password: ${MYSQL_PASSWORD}
  
jwt:
  secret: ${JWT_SECRET}
  expiration: ${JWT_EXPIRATION:3600000}
```

## 环境变量配置

### 必需环境变量
| 变量名 | 说明 | 示例 |
|--------|------|------|
| JWT_SECRET | JWT密钥 | `nG9dT@e4^M7#pKc!Wz0qF8vRtLx*A6s1YhJ2BrCm` |
| MYSQL_URL | MySQL连接URL | `jdbc:mysql://mysql-service:3306/banyu_mall` |
| MYSQL_USERNAME | MySQL用户名 | `banyu_user` |
| MYSQL_PASSWORD | MySQL密码 | `banyu_password` |
| REDIS_HOST | Redis主机 | `redis-service` |
| REDIS_PASSWORD | Redis密码 | `redis_password` |

### 可选环境变量
| 变量名 | 说明 | 默认值 |
|--------|------|--------|
| JWT_EXPIRATION | JWT过期时间 | `3600000` |
| JWT_TOKEN_PREFIX | JWT令牌前缀 | `Bearer` |
| JWT_ENABLE_BLACKLIST | 是否启用黑名单 | `true` |
| NACOS_SERVER_ADDR | Nacos服务器地址 | `localhost:8848` |
| NACOS_USERNAME | Nacos用户名 | `nacos` |
| NACOS_PASSWORD | Nacos密码 | `nacos` |

## 部署配置

### 开发环境
```bash
# 启动命令
mvn spring-boot:run -pl service/service-auth -Dspring.profiles.active=dev

# 环境变量设置
export JWT_SECRET="your-dev-secret-key"
export MYSQL_URL="jdbc:mysql://localhost:3306/banyu_mall"
export MYSQL_USERNAME="root"
export MYSQL_PASSWORD="123456"
```

### 生产环境
```bash
# 启动命令
mvn spring-boot:run -pl service/service-auth -Dspring.profiles.active=prod

# 环境变量设置
export JWT_SECRET="your-production-secret-key"
export MYSQL_URL="jdbc:mysql://mysql-service:3306/banyu_mall"
export MYSQL_USERNAME="banyu_user"
export MYSQL_PASSWORD="banyu_password"
export REDIS_HOST="redis-service"
export REDIS_PASSWORD="redis_password"
```

## Nacos配置管理

### 1. 创建配置
在Nacos控制台创建配置：
- **Data ID**: `service-auth.yaml`
- **Group**: `DEFAULT_GROUP`
- **Namespace**: `public`
- **配置格式**: YAML

### 2. 配置内容
使用 `service-auth-nacos.yml` 模板文件的内容，根据实际环境修改环境变量。

### 3. 配置刷新
- 支持配置热刷新
- 修改配置后自动生效
- 无需重启服务

## 安全考虑

### 1. 敏感信息保护
- 所有敏感信息使用环境变量
- 不在代码中硬编码密码
- 生产环境使用强密钥

### 2. 配置加密
- 支持Nacos配置加密
- 敏感配置项加密存储
- 运行时解密使用

### 3. 访问控制
- Nacos访问权限控制
- 配置修改审计日志
- 定期轮换密钥

## 监控和运维

### 1. 配置监控
- 配置变更监控
- 配置加载状态监控
- 配置错误告警

### 2. 健康检查
- 配置加载健康检查
- 数据库连接健康检查
- Redis连接健康检查

### 3. 日志管理
- 配置加载日志
- 配置变更日志
- 错误日志记录

## 故障排查

### 1. 配置加载失败
```bash
# 检查Nacos连接
curl -X GET "http://nacos-server:8848/nacos/v1/cs/configs?dataId=service-auth.yaml&group=DEFAULT_GROUP"

# 检查环境变量
env | grep -E "(JWT|MYSQL|REDIS|NACOS)"
```

### 2. 配置不生效
- 检查配置刷新是否启用
- 检查配置优先级
- 检查环境变量是否正确

### 3. 性能问题
- 检查配置缓存
- 检查数据库连接池
- 检查Redis连接池

## 最佳实践

### 1. 配置管理
- 使用版本控制管理配置模板
- 定期备份配置
- 配置变更前测试

### 2. 安全实践
- 定期轮换密钥
- 使用强密码
- 限制配置访问权限

### 3. 运维实践
- 监控配置变更
- 定期检查配置有效性
- 建立配置变更流程 
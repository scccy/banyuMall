# 认证服务配置文件说明

## 概述
本文档描述了认证服务的配置文件结构和使用方法，包括本地配置和Nacos配置中心的配置。

## 配置文件结构

### 1. 主配置文件 (application.yml)
**位置**: `service/service-auth/src/main/resources/application.yml`

**作用**: 
- 定义应用名称和通用配置
- 通过 `spring.profiles.active` 切换环境
- 包含所有环境通用的配置项

**关键配置**:
```yaml
spring:
  application:
    name: service-auth
  profiles:
    active: dev  # 默认使用开发环境，可切换为 prod
```

### 2. 开发环境配置 (application-dev.yml)
**位置**: `service/service-auth/src/main/resources/application-dev.yml`

**特点**:
- 本地直接连接数据库和Redis
- 启用调试日志和懒加载
- 包含Swagger文档配置
- 不连接Nacos配置中心

**主要配置项**:
- 数据源：本地MySQL (localhost:3306)
- Redis：本地Redis (localhost:6379)
- 日志级别：DEBUG
- 懒加载：启用
- Swagger：启用

### 3. 生产环境配置 (application-prod.yml)
**位置**: `service/service-auth/src/main/resources/application-prod.yml`

**特点**:
- 连接Nacos服务注册与发现
- 从Nacos配置中心获取配置
- 生产环境优化设置
- 关闭调试功能

**主要配置项**:
- Nacos服务注册与发现
- 环境变量配置
- 生产环境日志级别
- 性能优化配置

### 4. Nacos在线配置 (nacos-config-template-auth.yml)
**位置**: `infra/infra-gateway/nacos-config-template-auth.yml`

**作用**:
- 存储在Nacos配置中心的配置模板
- 生产环境动态配置
- 支持配置热更新

**配置ID**: `service-auth.yml`
**配置组**: `DEFAULT_GROUP`
**命名空间**: `prod`

## 环境切换方法

### 1. 开发环境
```bash
# 使用默认配置（开发环境）
java -jar service-auth.jar

# 或显式指定开发环境
java -jar service-auth.jar --spring.profiles.active=dev
```

### 2. 生产环境
```bash
# 指定生产环境
java -jar service-auth.jar --spring.profiles.active=prod
```

### 3. 环境变量方式
```bash
# 设置环境变量
export SPRING_PROFILES_ACTIVE=prod
java -jar service-auth.jar
```

## 配置优先级

1. **命令行参数** (最高优先级)
2. **环境变量**
3. **Nacos配置中心** (生产环境)
4. **本地配置文件**
5. **默认配置** (最低优先级)

## 关键配置项说明

### 1. 数据源配置
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/banyu_mall
    username: root
    password: 123456
    hikari:
      maximum-pool-size: 20  # 开发环境
      maximum-pool-size: 50  # 生产环境
```

### 2. Redis配置
```yaml
spring:
  data:
    redis:
      host: localhost  # 开发环境
      host: ${REDIS_HOST:redis-service}  # 生产环境
      port: 6379
      password: 
      database: 0
```

### 3. JWT配置
```yaml
jwt:
  secret: your-secret-key-here-should-be-at-least-32-bytes-long-for-security
  expiration: 3600000  # 1小时
  header: Authorization
  tokenPrefix: Bearer
```

### 4. Nacos配置
```yaml
spring:
  cloud:
    nacos:
      discovery:
        server-addr: ${NACOS_SERVER:localhost:8848}
        namespace: ${NACOS_NAMESPACE:prod}
        group: ${NACOS_GROUP:DEFAULT_GROUP}
      config:
        server-addr: ${NACOS_SERVER:localhost:8848}
        namespace: ${NACOS_NAMESPACE:prod}
        group: ${NACOS_GROUP:DEFAULT_GROUP}
        file-extension: yml
```

## 环境变量配置

### 开发环境
无需特殊环境变量，使用本地配置。

### 生产环境
```bash
# Nacos配置
export NACOS_SERVER=nacos-server:8848
export NACOS_NAMESPACE=prod
export NACOS_GROUP=DEFAULT_GROUP

# 数据库配置
export MYSQL_HOST=mysql-service
export MYSQL_PORT=3306
export MYSQL_DATABASE=banyu_mall
export MYSQL_USERNAME=root
export MYSQL_PASSWORD=password

# Redis配置
export REDIS_HOST=redis-service
export REDIS_PORT=6379
export REDIS_PASSWORD=

# JWT配置
export JWT_SECRET=your-production-secret-key
export JWT_EXPIRATION=3600000
```

## 配置热更新

### 1. Nacos配置更新
- 在Nacos控制台修改配置
- 应用自动刷新配置
- 无需重启服务

### 2. 配置刷新范围
- 数据库连接池配置
- Redis连接配置
- JWT配置
- 自定义业务配置

### 3. 不支持热更新的配置
- 数据源URL
- 应用端口
- 日志配置

## 安全配置

### 1. 开发环境
- 允许Swagger访问
- 开启调试日志
- 简化安全配置

### 2. 生产环境
- 关闭Swagger
- 限制管理端点访问
- 启用安全配置
- 使用环境变量管理敏感信息

## 性能优化配置

### 1. 开发环境
```yaml
spring:
  main:
    lazy-initialization: true  # 启用懒加载
  jmx:
    enabled: false  # 关闭JMX
```

### 2. 生产环境
```yaml
spring:
  main:
    lazy-initialization: false  # 关闭懒加载
  jmx:
    enabled: false  # 关闭JMX
  cache:
    type: redis  # 启用Redis缓存
```

## 监控配置

### 1. 健康检查
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: always  # 开发环境
      show-details: when-authorized  # 生产环境
```

### 2. 指标监控
```yaml
management:
  metrics:
    export:
      prometheus:
        enabled: true  # 生产环境启用Prometheus监控
```

## 故障排查

### 1. 配置加载问题
- 检查配置文件路径
- 验证YAML语法
- 确认环境变量设置

### 2. 连接问题
- 检查数据库连接
- 验证Redis连接
- 确认Nacos服务状态

### 3. 权限问题
- 检查数据库用户权限
- 验证Redis访问权限
- 确认Nacos命名空间权限

## 版本历史
- 2025-01-27: 创建认证服务配置文件说明文档
- 2025-01-27: 完善配置切换和环境变量说明 
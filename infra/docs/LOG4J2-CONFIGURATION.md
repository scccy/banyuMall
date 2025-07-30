# Log4j2 配置说明

## 概述
本配置文件为微服务架构提供统一的日志管理方案，支持多服务隔离和个性化配置。

## 核心特性

### 1. 服务隔离
- **动态路径**: 使用 `${SERVICE_NAME}` 变量，根据 `spring.application.name` 自动生成服务专属日志目录
- **文件命名**: 日志文件名包含服务名，避免冲突
- **目录结构**: `./logs/{service-name}/` 格式

### 2. 配置变量
```xml
<!-- 服务名称，从Spring Boot配置中获取 -->
<Property name="SERVICE_NAME">${sys:spring.application.name:-unknown-service}</Property>

<!-- 日志文件路径，包含服务名 -->
<Property name="LOG_FILE_PATH">./logs/${SERVICE_NAME}</Property>
```

### 3. 文件命名规则
- **应用日志**: `{service-name}.log`
- **错误日志**: `{service-name}-error.log`
- **归档文件**: `{service-name}-{date}-{index}.log.gz`

## 使用示例

### service-auth 服务
- **日志目录**: `./logs/service-auth/`
- **应用日志**: `service-auth.log`
- **错误日志**: `service-auth-error.log`

### service-user 服务
- **日志目录**: `./logs/service-user/`
- **应用日志**: `service-user.log`
- **错误日志**: `service-user-error.log`

## 配置要求

### 1. 微服务配置
每个微服务必须在 `application.yml` 中设置：
```yaml
spring:
  application:
    name: service-xxx  # 服务名称
```

### 2. 日志级别配置
可以在微服务的 `application.yml` 中覆盖日志级别：
```yaml
logging:
  level:
    com.origin.xxx: debug  # 特定服务的日志级别
```

## 优势

1. **避免冲突**: 每个服务有独立的日志目录和文件
2. **易于管理**: 日志文件按服务分类存储
3. **便于排查**: 快速定位问题来源
4. **统一配置**: 所有服务使用相同的日志格式和策略
5. **灵活扩展**: 支持服务特定的日志配置

## 注意事项

1. 确保每个微服务都正确配置了 `spring.application.name`
2. 日志目录会自动创建，无需手动创建
3. 日志文件会自动按大小和时间滚动
4. 支持异步写入，提高性能 
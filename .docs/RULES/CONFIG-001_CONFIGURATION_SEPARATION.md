# 配置文件分离规则

**ID**: CONFIG-001  
**Name**: 配置文件分离规则  
**Status**: Active  
**创建时间**: 2025-07-30  

## 触发情景 (Context/Trigger)
当设计微服务架构时，需要管理应用配置，特别是使用配置中心（如Nacos）的场景。

## 指令 (Directive)

### 1. 配置分离原则
- **必须 (MUST)** 将配置文件分为两部分：本地配置和远程配置
- **必须 (MUST)** 本地配置只包含连接配置中心的基本信息
- **必须 (MUST)** 远程配置包含业务逻辑相关的配置

### 2. 本地配置文件 (application.yml)
- **必须 (MUST)** 包含应用基本信息（端口、应用名等）
- **必须 (MUST)** 包含配置中心连接信息（Nacos地址、命名空间、分组等）
- **必须 (MUST)** 包含配置导入语句
- **禁止 (MUST NOT)** 包含业务逻辑配置

### 3. 远程配置文件 (Nacos配置)
- **必须 (MUST)** 包含所有业务逻辑配置
- **必须 (MUST)** 包含路由、过滤器、限流等网关配置
- **必须 (MUST)** 包含数据库、Redis等中间件配置
- **必须 (MUST)** 包含管理端点、监控配置

### 4. 配置命名规范
- **必须 (MUST)** 使用 `${spring.application.name}.${spring.cloud.nacos.config.file-extension}` 格式
- **必须 (MUST)** 配置文件名与应用名保持一致
- **必须 (MUST)** 使用命名空间ID而不是名称

### 5. 配置更新策略
- **必须 (MUST)** 启用配置自动刷新 `refresh-enabled: true`
- **必须 (MUST)** 支持配置热更新，无需重启服务
- **必须 (MUST)** 配置变更后自动生效

## 理由 (Justification)
此规则源于任务 `task_20250127_1600_gateway_config_refactor.md`。在该任务中，将网关配置分离为本地配置和Nacos远程配置，实现了配置的动态管理和环境隔离，提高了系统的可维护性和灵活性。

## 最佳实践

### 1. 本地配置文件示例
```yaml
server:
  port: 8080

spring:
  application:
    name: infra-gateway
  
  # 配置导入 - 从Nacos加载配置
  config:
    import: nacos:${spring.application.name}.${spring.cloud.nacos.config.file-extension}
  
  cloud:
    nacos:
      discovery:
        server-addr: 117.50.197.170:8849
        username: nacos
        password: ${nacos.password}
        namespace: ${nacos.namespace.id}
        group: DEFAULT_GROUP
        register-enabled: true
      config:
        server-addr: 117.50.197.170:8849
        username: nacos
        password: ${nacos.password}
        namespace: ${nacos.namespace.id}
        group: DEFAULT_GROUP
        file-extension: yml
        refresh-enabled: true

# 基础日志配置
logging:
  level:
    root: info
```

### 2. 远程配置文件示例 (Nacos)
```yaml
spring:
  cloud:
    gateway:
      discovery:
        locator:
          enabled: true
          lower-case-service-id: true
      routes:
        - id: auth-service
          uri: lb://service-auth
          predicates:
            - Path=/auth/**
          filters:
            - StripPrefix=1

# 管理端点配置
management:
  endpoints:
    web:
      exposure:
        include: health,info,gateway
  endpoint:
    health:
      show-details: always

# 业务配置
gateway:
  rate-limit:
    enabled: true
    default-rate: 1000
  circuit-breaker:
    enabled: true
    fallback-uri: forward:/fallback

# 数据库配置
datasource:
  url: jdbc:mysql://${db.host}:${db.port}/${db.name}
  username: ${db.username}
  password: ${db.password}

# Redis配置
redis:
  host: ${redis.host}
  port: ${redis.port}
  password: ${redis.password}
```

### 3. 环境变量配置
```yaml
# 使用环境变量或外部配置
nacos:
  password: ${NACOS_PASSWORD:default-password}
  namespace:
    id: ${NACOS_NAMESPACE_ID:default-namespace}

db:
  host: ${DB_HOST:localhost}
  port: ${DB_PORT:3306}
  name: ${DB_NAME:default-db}
  username: ${DB_USERNAME:root}
  password: ${DB_PASSWORD:password}

redis:
  host: ${REDIS_HOST:localhost}
  port: ${REDIS_PORT:6379}
  password: ${REDIS_PASSWORD:}
```

## 配置管理流程

### 1. 配置创建
1. 创建本地配置文件 `application.yml`
2. 在配置中心创建远程配置文件
3. 配置导入语句指向远程配置

### 2. 配置更新
1. 修改配置中心中的配置
2. 配置自动刷新到应用
3. 验证配置生效

### 3. 环境管理
1. 不同环境使用不同命名空间
2. 使用环境变量管理敏感信息
3. 配置模板化减少重复

## 检查清单
- [ ] 本地配置是否只包含连接信息
- [ ] 远程配置是否包含所有业务配置
- [ ] 配置命名是否符合规范
- [ ] 是否启用了配置自动刷新
- [ ] 是否使用了命名空间ID
- [ ] 敏感信息是否使用环境变量
- [ ] 配置是否支持热更新
- [ ] 不同环境是否使用不同命名空间

## 相关规则
- [DEP-001_MAVEN_DEPENDENCY_MANAGEMENT.md](./DEP-001_MAVEN_DEPENDENCY_MANAGEMENT.md) - 依赖管理规则
- [PERF-001_SPRING_BOOT_PERFORMANCE_RULES.md](./PERF-001_SPRING_BOOT_PERFORMANCE_RULES.md) - 性能优化规则 
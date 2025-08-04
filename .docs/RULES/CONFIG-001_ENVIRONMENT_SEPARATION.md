# 配置文件分离规则

## 📋 规则概述

**ID**: CONFIG-001  
**Name**: 配置文件分离规则  
**Status**: Active  
**创建时间**: 2025-08-04  

## 🎯 核心原则

### 1. 配置分离原则
- **本地配置**: 基础配置和Nacos连接信息
- **远程配置**: 业务配置和敏感信息
- **环境变量**: 覆盖敏感配置和动态配置
- **分层管理**: 按环境、模块、功能分层管理

### 2. 配置文件组织
```
src/main/resources/
├── dev/
│   ├── application.yml          # 本地配置文件
│   ├── service-xxx.yaml         # Nacos远程配置文件
│   └── README.md                # 配置说明文档
├── test/
│   ├── application.yml
│   ├── service-xxx.yaml
│   └── README.md
├── prod/
│   ├── application.yml
│   ├── service-xxx.yaml
│   └── README.md
└── mapper/
    └── *.xml
```

### 3. 配置加载顺序
1. **本地配置** (`application.yml`) - 基础配置和Nacos连接
2. **Nacos配置** (`service-xxx.yaml`) - 业务配置和敏感信息
3. **环境变量** - 覆盖敏感配置

## 📁 配置文件职责分工

### 1. application.yml (本地配置文件)
**职责**: 基础配置和Nacos连接信息

#### 必须包含的配置
```yaml
# 服务器配置
server:
  port: 8082

# Spring基础配置
spring:
  application:
    name: service-user
  
  # 配置导入 - 从Nacos加载配置
  config:
    import: nacos:${spring.application.name}.${spring.cloud.nacos.config.file-extension}
  
  # Nacos服务注册与发现配置
  cloud:
    nacos:
      discovery:
        server-addr: 117.50.197.170:8848
        username: nacos
        password: olN2y)]VzEISLITO#_}L0J^$>Nk)1S~W
        namespace: ba1493dc-20fa-413b-84e1-d929c9a4aeac
        group: DEFAULT_GROUP
        register-enabled: true
        heart-beat-interval: 5000
        heart-beat-timeout: 15000
        ip-delete-timeout: 30000
      
      config:
        server-addr: 117.50.197.170:8848
        username: nacos
        password: olN2y)]VzEISLITO#_}L0J^$>Nk)1S~W
        namespace: ba1493dc-20fa-413b-84e1-d929c9a4aeac
        group: DEFAULT_GROUP
        file-extension: yaml
        refresh-enabled: true
        timeout: 3000
        retry-time: 2000
        long-poll-timeout: 46000

  # 启动性能优化（开发环境）
  main:
    lazy-initialization: true
  jmx:
    enabled: false

# 管理端点配置
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: always
  info:
    env:
      enabled: true
    git:
      mode: full

# 日志配置
logging:
  config: classpath:log4j2.xml
  level:
    com.origin.user: debug
    org.springframework.web: debug
    com.origin.user.service: debug
    org.apache.logging.log4j.status: ERROR
    org.apache.logging.log4j.core.config: ERROR
    root: info
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
  file:
    name: logs/service-user.log
    max-size: 100MB
    max-history: 30
```

### 2. service-xxx.yaml (Nacos远程配置文件)
**职责**: 业务配置和敏感信息

#### 必须包含的配置
```yaml
# 数据源配置
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: ${MYSQL_URL:jdbc:mysql://117.50.197.170:3306/banyu?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8}
    username: ${MYSQL_USERNAME:root}
    password: ${MYSQL_PASSWORD:qUhquc-dagpup-5rubvu}
    hikari:
      minimum-idle: 5
      maximum-pool-size: 20
      auto-commit: true
      idle-timeout: 30000
      pool-name: HikariCP
      max-lifetime: 1800000
      connection-timeout: 30000
      connection-test-query: SELECT 1

  # Redis配置
  data:
    redis:
      host: ${REDIS_HOST:117.50.197.170}
      port: ${REDIS_PORT:16379}
      password: ${REDIS_PASSWORD:qUhquc-dagpup-5rubvu}
      database: ${REDIS_DATABASE:2}
      timeout: 10000ms
      lettuce:
        pool:
          max-active: 8
          max-wait: -1ms
          max-idle: 8
          min-idle: 0

  # MyBatis-Plus配置
  mybatis-plus:
    configuration:
      map-underscore-to-camel-case: true
      cache-enabled: false
      call-setters-on-nulls: true
      jdbc-type-for-null: 'null'
      log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
    global-config:
      db-config:
        id-type: assign_id
        logic-delete-field: isDeleted
        logic-delete-value: 1
        logic-not-delete-value: 0
    mapper-locations: classpath*:/mapper/**/*.xml
    type-aliases-package: com.origin.user.entity

  # 缓存配置
  cache:
    type: redis
    redis:
      time-to-live: 3600000
      cache-null-values: false

# 业务配置
cache:
  user:
    info:
      key-prefix: "user:info:"
      ttl: 1800
    profile:
      key-prefix: "user:profile:"
      ttl: 3600
    list:
      key-prefix: "user:list:"
      ttl: 300

# 安全配置
security:
  permit-all:
    - /user/test/**
    - /profile/test/**
    - /swagger-ui/**
    - /v3/api-docs/**
    - /actuator/health
    - /actuator/info
    - /actuator/metrics

# Swagger配置
springdoc:
  swagger-ui:
    path: /swagger-ui.html
    enabled: true
  api-docs:
    path: /v3/api-docs
    enabled: true
  packages-to-scan: com.origin.user.controller

# 业务服务配置
user:
  create:
    # 用户创建相关配置
```

## 🔧 环境配置规范

### 1. 开发环境 (dev/)
**特点**: 详细日志、调试功能、宽松配置

#### 性能优化
```yaml
spring:
  main:
    lazy-initialization: true  # 开发环境启用懒加载
  jmx:
    enabled: false  # 开发环境关闭JMX

logging:
  level:
    com.origin.user: debug
    org.springframework.web: debug
    com.origin.user.service: debug
```

#### 调试功能
```yaml
springdoc:
  swagger-ui:
    enabled: true
  api-docs:
    enabled: true

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
```

### 2. 测试环境 (test/)
**特点**: 平衡性能、基本监控、测试配置

#### 性能配置
```yaml
spring:
  main:
    lazy-initialization: false
  jmx:
    enabled: true

logging:
  level:
    com.origin.user: info
    org.springframework.web: warn
    root: warn
```

#### 监控配置
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  metrics:
    export:
      prometheus:
        enabled: true
```

### 3. 生产环境 (prod/)
**特点**: 高性能、安全配置、最小日志

#### 性能优化
```yaml
spring:
  main:
    lazy-initialization: false
  jmx:
    enabled: true

logging:
  level:
    com.origin.user: warn
    org.springframework.web: error
    root: error
  file:
    max-size: 500MB
    max-history: 60
```

#### 安全配置
```yaml
springdoc:
  swagger-ui:
    enabled: false
  api-docs:
    enabled: false

management:
  endpoints:
    web:
      exposure:
        include: health,info
  endpoint:
    health:
      show-details: never
```

## 🔐 敏感信息管理

### 1. 环境变量配置
**原则**: 敏感信息使用环境变量覆盖

#### 数据库配置
```yaml
spring:
  datasource:
    url: ${MYSQL_URL:jdbc:mysql://localhost:3306/banyu}
    username: ${MYSQL_USERNAME:root}
    password: ${MYSQL_PASSWORD:password}
```

#### Redis配置
```yaml
spring:
  data:
    redis:
      host: ${REDIS_HOST:localhost}
      port: ${REDIS_PORT:6379}
      password: ${REDIS_PASSWORD:password}
      database: ${REDIS_DATABASE:0}
```

#### Nacos配置
```yaml
spring:
  cloud:
    nacos:
      discovery:
        username: ${NACOS_USERNAME:nacos}
        password: ${NACOS_PASSWORD:nacos}
      config:
        username: ${NACOS_USERNAME:nacos}
        password: ${NACOS_PASSWORD:nacos}
```

### 2. 配置加密
**原则**: 生产环境敏感配置必须加密

#### 使用Jasypt加密
```yaml
# 加密配置
jasypt:
  encryptor:
    password: ${JASYPT_PASSWORD:encryptorPassword}
    algorithm: PBEWithMD5AndDES
    iv-generator-classname: org.jasypt.iv.RandomIvGenerator

# 加密后的配置
spring:
  datasource:
    password: ENC(encryptedPassword)
```

## 📝 配置文档规范

### 1. README.md 必须包含
- 配置文件说明
- 配置加载顺序
- 环境特性说明
- 启动方式
- 配置验证方法
- 注意事项
- 常见问题

### 2. 配置注释规范
```yaml
# 配置分组说明
spring:
  # 数据源配置
  datasource:
    # 数据库连接URL
    url: jdbc:mysql://localhost:3306/banyu
    # 数据库用户名
    username: root
    # 数据库密码（生产环境使用环境变量）
    password: ${MYSQL_PASSWORD:password}
```

## 🚫 禁止事项

### 严格禁止
1. **硬编码敏感信息** - 密码、密钥等敏感信息不能硬编码
2. **混合环境配置** - 不同环境的配置不能混用
3. **缺少配置文档** - 每个环境目录必须有README.md
4. **配置重复** - 避免配置项重复定义

### 不推荐
1. **过度配置** - 避免不必要的配置项
2. **配置耦合** - 避免配置项之间的强耦合
3. **缺少验证** - 避免缺少配置验证机制

## ✅ 推荐事项

### 最佳实践
1. **配置外部化** - 敏感配置使用环境变量
2. **配置验证** - 启动时验证关键配置
3. **配置监控** - 监控配置变更和加载状态
4. **配置备份** - 定期备份重要配置
5. **配置版本管理** - 配置变更进行版本管理

### 配置验证
```yaml
# 配置验证
spring:
  config:
    import: optional:classpath:config-validation.yml
  profiles:
    active: dev

# 配置验证规则
validation:
  datasource:
    required: true
    url-pattern: "jdbc:mysql://.*"
  redis:
    required: true
    host-pattern: ".*"
```

## 🔄 配置更新流程

### 1. 配置变更流程
1. **需求分析** - 分析配置变更需求
2. **影响评估** - 评估配置变更影响范围
3. **变更实施** - 按环境逐步实施变更
4. **验证测试** - 验证配置变更效果
5. **文档更新** - 更新配置文档

### 2. 配置发布流程
1. **开发环境** - 先在开发环境测试
2. **测试环境** - 在测试环境验证
3. **生产环境** - 最后在生产环境发布
4. **回滚准备** - 准备配置回滚方案

---

**版本**: v1.0  
**创建时间**: 2025-08-04  
**最后更新**: 2025-08-04  
**维护者**: scccy  
**参考**: `service/service-user/src/main/resources/dev/` 
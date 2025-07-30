# 微服务配置文件管理

## 概述

本目录包含BanyuMall项目的微服务配置文件模板和管理工具，支持配置分层管理：
- **公有配置**: 在Nacos中统一管理，包含数据库、Redis、JWT等敏感信息
- **个性配置**: 各微服务在本地配置文件中管理，包含服务特定配置
- **环境配置**: 通过Nacos命名空间和环境变量区分不同环境

## 目录结构

```
infra/templates/config/
├── application.yml.template              # 主配置文件模板
├── application-dev.yml.template          # 开发环境配置模板
├── application-test.yml.template         # 测试环境配置模板
├── application-prod.yml.template         # 生产环境配置模板
├── application-docker.yml.template       # Docker环境配置模板
├── nacos-config-template.yml             # 公有配置模板（所有微服务共享）
├── nacos-config-template-auth.yml        # 认证服务公有配置模板
├── nacos-config-template-user.yml        # 用户服务公有配置模板
├── nacos-config-template-gateway.yml     # 网关服务公有配置模板
├── create-service-config.sh              # 配置文件生成脚本
└── README.md                             # 本文件
```

## 快速开始

### 1. 生成服务配置文件

```bash
# 为现有服务生成配置文件
./infra/templates/config/create-service-config.sh service-auth
./infra/templates/config/create-service-config.sh service-user
./infra/templates/config/create-service-config.sh service-gateway

# 为新服务生成配置文件
./infra/templates/config/create-service-config.sh service-task
```

### 2. 配置分层结构

#### 公有配置（Nacos管理）
```
Nacos配置中心
├── 命名空间: dev/test/prod
│   ├── service-auth.yaml        # 认证服务公有配置
│   ├── service-user.yaml        # 用户服务公有配置
│   └── service-gateway.yaml     # 网关服务公有配置
```

#### 个性配置（本地管理）
```
service/service-xxx/src/main/resources/
├── application.yml              # 主配置文件
├── application-dev.yml          # 开发环境个性配置
├── application-test.yml         # 测试环境个性配置
├── application-prod.yml         # 生产环境个性配置
└── application-docker.yml       # Docker环境个性配置
```

## 环境配置说明

### 开发环境 (dev)
- **Nacos地址**: localhost:8848
- **命名空间**: dev
- **数据库**: 本地MySQL
- **Redis**: 本地Redis
- **特性**: 自动数据库初始化、调试模式、SQL日志

### 测试环境 (test)
- **Nacos地址**: test-nacos:8848
- **命名空间**: test
- **数据库**: 测试环境MySQL
- **Redis**: 测试环境Redis
- **特性**: 禁用数据库初始化、生产级别配置

### 生产环境 (prod)
- **Nacos地址**: prod-nacos:8848
- **命名空间**: prod
- **数据库**: 生产环境MySQL
- **Redis**: 生产环境Redis
- **特性**: 高可用配置、安全加固、性能优化

### Docker环境 (docker)
- **Nacos地址**: nacos:8848
- **命名空间**: public
- **数据库**: mysql:3306
- **Redis**: redis:6379
- **特性**: 容器化部署配置

## 配置分层管理

### 公有配置（Nacos管理）
```
Nacos配置中心
├── 命名空间: dev
│   ├── service-auth.yaml        # 认证服务公有配置
│   ├── service-user.yaml        # 用户服务公有配置
│   └── service-gateway.yaml     # 网关服务公有配置
├── 命名空间: test
│   ├── service-auth.yaml        # 认证服务公有配置
│   ├── service-user.yaml        # 用户服务公有配置
│   └── service-gateway.yaml     # 网关服务公有配置
└── 命名空间: prod
    ├── service-auth.yaml        # 认证服务公有配置
    ├── service-user.yaml        # 用户服务公有配置
    └── service-gateway.yaml     # 网关服务公有配置
```

### 公有配置内容
在Nacos中配置以下公有信息：
```yaml
# 数据库配置
datasource:
  url: jdbc:mysql://${MYSQL_HOST}:${MYSQL_PORT}/${MYSQL_DATABASE}
  username: ${MYSQL_USERNAME}
  password: ${MYSQL_PASSWORD}

# Redis配置
spring:
  redis:
    host: ${REDIS_HOST}
    password: ${REDIS_PASSWORD}

# JWT配置
banyumall:
  security:
    jwt:
      secret: ${JWT_SECRET}
      expiration: ${JWT_EXPIRATION:86400}

# 文件上传配置
banyumall:
  file:
    upload:
      max-size: ${FILE_MAX_SIZE:10MB}
      path: ${FILE_UPLOAD_PATH:/uploads}
```

### 个性配置（本地管理）
各微服务在本地配置文件中管理个性配置：
```yaml
# 服务特定配置
banyumall:
  service:
    name: ${SERVICE_NAME}
    version: ${SERVICE_VERSION:1.0.0}
  
  # 认证服务个性配置
  auth:
    password-encoder-strength: ${PASSWORD_ENCODER_STRENGTH:12}
    login-attempts-limit: ${LOGIN_ATTEMPTS_LIMIT:5}
  
  # 用户服务个性配置
  user:
    profile:
      avatar-upload-path: ${AVATAR_UPLOAD_PATH:/uploads/avatars}
```

## 配置优先级

1. **最高优先级**: 环境变量
2. **高优先级**: Nacos配置中心
3. **中优先级**: 本地配置文件
4. **低优先级**: 默认值

## 环境变量配置

### 开发环境
```bash
export SPRING_PROFILES_ACTIVE=dev
export NACOS_SERVER_ADDR=localhost:8848
export NACOS_NAMESPACE=dev
export NACOS_USERNAME=nacos
export NACOS_PASSWORD=nacos
```

### 测试环境
```bash
export SPRING_PROFILES_ACTIVE=test
export NACOS_SERVER_ADDR=test-nacos:8848
export NACOS_NAMESPACE=test
export NACOS_USERNAME=nacos
export NACOS_PASSWORD=nacos123
```

### 生产环境
```bash
export SPRING_PROFILES_ACTIVE=prod
export NACOS_SERVER_ADDR=prod-nacos:8848
export NACOS_NAMESPACE=prod
export NACOS_USERNAME=nacos
export NACOS_PASSWORD=prod123456
```

## 配置验证

### 1. 本地验证
```bash
# 启动服务验证配置
./mvnw spring-boot:run -Dspring.profiles.active=dev

# 验证Nacos连接
curl -X GET "http://localhost:8848/nacos/v1/ns/operator/metrics"
```

### 2. 配置热更新验证
```bash
# 在Nacos中修改配置
# 验证服务是否自动刷新配置
curl -X POST "http://localhost:8080/actuator/refresh"
```

## 最佳实践

### 1. 配置分层管理
- **公有配置**: 在Nacos中统一管理，包含数据库、Redis、JWT等敏感信息
- **个性配置**: 各微服务在本地配置文件中管理，包含服务特定配置
- **环境隔离**: 通过Nacos命名空间区分不同环境
- **配置热更新**: 公有配置支持热更新，个性配置需要重启服务

### 2. 环境隔离
- 不同环境使用不同的Nacos命名空间
- 环境特定的配置使用环境变量覆盖
- 生产环境配置与开发环境完全隔离

### 3. 配置安全
- 敏感信息使用环境变量或Nacos配置
- 生产环境密码定期更换
- 配置访问权限严格控制

### 4. 配置监控
- 使用Spring Boot Actuator监控配置状态
- 配置变更日志记录
- 配置热更新验证

## 故障排除

### 1. Nacos连接失败
```bash
# 检查Nacos服务状态
curl -X GET "http://localhost:8848/nacos/v1/ns/operator/metrics"

# 检查网络连接
telnet localhost 8848

# 检查认证信息
curl -X GET "http://localhost:8848/nacos/v1/auth/users/login" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "username=nacos&password=nacos"
```

### 2. 配置加载失败
```bash
# 检查配置文件语法
yamllint application.yml

# 检查环境变量
env | grep NACOS
env | grep SPRING

# 查看启动日志
tail -f logs/application.log
```

### 3. 配置热更新失败
```bash
# 检查配置刷新端点
curl -X POST "http://localhost:8080/actuator/refresh"

# 检查配置变更日志
grep "Configuration change" logs/application.log
```

## 相关文档

- [微服务配置文件管理规则](../.docs/RULES/DEV-007.md)
- [Docker容器化部署规则](../.docs/RULES/DEV-006.md)
- [Nacos配置中心文档](https://nacos.io/zh-cn/docs/v2/guide/user/configuration.html) 
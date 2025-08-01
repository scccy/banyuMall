> **文档位置**: infra/moudleDocs/{模块名称}/design.md

# Service-Gateway 模块设计文档

## 1. 模块概述

### 1.1 模块职责
**Service-Gateway** 是BanyuMall微服务架构的API网关模块，负责统一入口、路由转发、负载均衡、限流熔断、链路追踪等核心功能，为整个微服务架构提供统一的访问入口。

### 1.2 核心功能
- **统一入口**: 提供统一的API访问入口，隐藏内部服务细节
- **路由转发**: 根据请求路径将请求转发到对应的微服务
- **负载均衡**: 支持多种负载均衡策略，提高系统可用性
- **限流熔断**: 基于IP地址的限流保护，防止系统过载
- **链路追踪**: 自动生成请求ID，添加链路追踪信息
- **跨域处理**: 统一处理CORS跨域请求
- **异常处理**: 统一的异常处理和错误响应格式
- **请求日志**: 记录详细的请求和响应日志

### 1.3 技术栈
- **框架**: Spring Boot 3.x + Spring Cloud Gateway
- **响应式编程**: WebFlux + Reactor
- **服务发现**: Nacos Discovery
- **配置中心**: Nacos Config
- **负载均衡**: Spring Cloud LoadBalancer
- **限流**: Spring Cloud Gateway Rate Limiter
- **日志**: Log4j2
- **序列化**: FastJSON2

## 2. 路由配置设计

### 2.1 路由规则
**文件**: `GatewayConfig.java`
**职责**: 配置微服务路由规则

```java
@Configuration
public class GatewayConfig {
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // 认证服务路由
                .route("auth-service", r -> r
                        .path("/auth/**")
                        .filters(f -> f
                                .addRequestHeader("X-Gateway-Source", "service-gateway")
                                .addRequestHeader("X-Service-Name", "service-auth"))
                        .uri("lb://service-auth"))
                
                // 用户服务路由
                .route("user-service", r -> r
                        .path("/user/**")
                        .filters(f -> f
                                .addRequestHeader("X-Gateway-Source", "service-gateway")
                                .addRequestHeader("X-Service-Name", "service-user"))
                        .uri("lb://service-user"))
                
                .build();
    }
}
```

### 2.2 路由配置详情
| 服务名称 | 路由路径 | 目标服务 | 负载均衡 | 请求头添加 |
|----------|----------|----------|----------|------------|
| **认证服务** | `/auth/**` | `service-auth` | 是 | `X-Gateway-Source`, `X-Service-Name` |
| **用户服务** | `/user/**` | `service-user` | 是 | `X-Gateway-Source`, `X-Service-Name` |

## 3. 过滤器设计

### 3.1 全局过滤器
**文件**: `GlobalFilter.java`
**职责**: 处理链路追踪和请求日志

**核心功能**:
- **请求ID生成**: 自动生成或使用现有请求ID
- **链路追踪**: 添加链路追踪请求头
- **请求日志**: 记录详细的请求和响应日志
- **性能监控**: 记录请求处理时间

**请求头添加**:
- `X-Request-ID`: 请求唯一标识
- `X-Client-IP`: 客户端IP地址
- `X-User-Agent`: 用户代理信息
- `X-Request-Time`: 请求时间戳
- `X-Service-Name`: 服务名称
- `X-Gateway-Time`: 网关处理时间

### 3.2 限流配置
**文件**: `RateLimiterConfig.java`
**职责**: 配置基于IP地址的限流策略

```java
@Configuration
public class RateLimiterConfig {
    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> {
            String ip = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
            return Mono.just(ip);
        };
    }
}
```

## 4. 异常处理设计

### 4.1 全局异常处理器
**文件**: `GatewayExceptionHandler.java`
**职责**: 统一处理网关异常，返回标准响应格式

**支持的异常类型**:
- `BusinessException`: 业务异常
- `ConnectTimeoutException`: 连接超时异常
- `ReadTimeoutException`: 读取超时异常
- `ServiceUnavailableException`: 服务不可用异常
- `Exception`: 其他系统异常

**响应格式**:
```json
{
  "code": 500,
  "message": "网关服务异常，请稍后重试",
  "data": null,
  "timestamp": 1703123456789
}
```

## 5. 配置设计

### 5.1 WebFlux配置
**文件**: `GatewayWebConfig.java`
**职责**: 配置WebFlux响应式环境

**核心配置**:
- **消息编解码器**: 配置最大内存大小
- **CORS跨域**: 支持跨域请求
- **转换服务**: 提供mvcConversionService

### 5.2 应用配置
**文件**: `application.yml`
**职责**: 基础应用配置

```yaml
# 服务器配置
server:
  port: 8080

# Spring配置
spring:
  application:
    name: service-gateway
  
  # 响应式Web应用
  main:
    web-application-type: reactive
  
  # 自动配置排除
  autoconfigure:
    exclude:
      - org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration
      - org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
      - org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration
      - org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration
      - org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration
      - org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration
      - org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration

# 管理端点
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,gateway
  endpoint:
    health:
      show-details: always
```

## 6. 项目结构

```
src/main/java/com/origin/gateway/
├── config/           # 配置类
│   ├── GatewayConfig.java
│   ├── RateLimiterConfig.java
│   └── GatewayWebConfig.java
├── filter/           # 过滤器
│   └── GlobalFilter.java
├── exception/        # 异常处理
│   └── GatewayExceptionHandler.java
└── GatewayApplication.java
```

## 7. 应用配置

### 7.1 本地配置
```yaml
# application.yml
spring:
  profiles:
    active: dev
  application:
    name: service-gateway
```

### 7.2 Nacos配置
```yaml
# nacos-config-template-gateway.yml
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
            - AddRequestHeader=X-Gateway-Source, service-gateway
            - AddRequestHeader=X-Service-Name, service-auth
            - name: RequestRateLimiter
              args:
                redis-rate-limiter.replenishRate: 10
                redis-rate-limiter.burstCapacity: 20
                key-resolver: "#{@userKeyResolver}"
        
        - id: user-service
          uri: lb://service-user
          predicates:
            - Path=/user/**
          filters:
            - AddRequestHeader=X-Gateway-Source, service-gateway
            - AddRequestHeader=X-Service-Name, service-user
            - name: RequestRateLimiter
              args:
                redis-rate-limiter.replenishRate: 20
                redis-rate-limiter.burstCapacity: 40
                key-resolver: "#{@userKeyResolver}"

# 限流配置
spring:
  cloud:
    gateway:
      default-filters:
        - DedupeResponseHeader=Access-Control-Allow-Credentials Access-Control-Allow-Origin

# 跨域配置
spring:
  cloud:
    gateway:
      globalcors:
        cors-configurations:
          '[/**]':
            allowedOriginPatterns: "*"
            allowedMethods: "*"
            allowedHeaders: "*"
            allowCredentials: true
```

## 8. 依赖管理

### 8.1 核心依赖
```xml
<dependencies>
    <!-- Spring Cloud Gateway -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-gateway</artifactId>
    </dependency>
    
    <!-- Nacos Discovery -->
    <dependency>
        <groupId>com.alibaba.cloud</groupId>
        <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
    </dependency>
    
    <!-- Nacos Config -->
    <dependency>
        <groupId>com.alibaba.cloud</groupId>
        <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
    </dependency>
    
    <!-- LoadBalancer -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-loadbalancer</artifactId>
    </dependency>
    
    <!-- Actuator -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
    
    <!-- Log4j2 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-log4j2</artifactId>
    </dependency>
    
    <!-- FastJSON2 -->
    <dependency>
        <groupId>com.alibaba.fastjson2</groupId>
        <artifactId>fastjson2</artifactId>
    </dependency>
</dependencies>
```

### 8.2 依赖排除
```xml
<!-- 排除默认的Logback -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter</artifactId>
    <exclusions>
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-logging</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

## 9. 使用说明

### 9.1 启动配置
```bash
# 开发环境启动
java -jar service-gateway.jar --spring.profiles.active=dev

# 生产环境启动
java -jar service-gateway.jar --spring.profiles.active=prod
```

### 9.2 路由访问
```bash
# 访问认证服务
curl -X POST "http://localhost:8080/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "123456"}'

# 访问用户服务
curl -X GET "http://localhost:8080/user/profile/123" \
  -H "Authorization: Bearer token"
```

### 9.3 监控端点
```bash
# 健康检查
curl -X GET "http://localhost:8080/actuator/health"

# 网关路由信息
curl -X GET "http://localhost:8080/actuator/gateway/routes"

# 应用信息
curl -X GET "http://localhost:8080/actuator/info"
```

## 10. 最佳实践

### 10.1 性能优化
- **响应式编程**: 使用WebFlux提高并发处理能力
- **连接池**: 合理配置连接池参数
- **缓存**: 使用Redis缓存路由信息
- **限流**: 配置合理的限流策略

### 10.2 安全配置
- **HTTPS**: 生产环境启用HTTPS
- **认证**: 集成JWT认证
- **限流**: 防止恶意攻击
- **日志**: 记录安全相关日志

### 10.3 监控告警
- **健康检查**: 定期检查服务健康状态
- **性能监控**: 监控响应时间和吞吐量
- **错误告警**: 配置错误率告警
- **链路追踪**: 集成分布式链路追踪

### 10.4 故障处理
- **熔断**: 配置熔断器防止级联故障
- **重试**: 配置合理的重试策略
- **降级**: 提供降级服务
- **恢复**: 自动故障恢复

## 11. 版本历史

| 版本 | 日期 | 变更内容 |
|------|------|----------|
| 1.0.0 | 2024-12-19 | 初始版本，基础路由功能 |
| 1.1.0 | 2025-01-27 | 添加全局过滤器和异常处理 |
| 1.2.0 | 2025-07-31 | 优化性能，添加限流和监控 | 
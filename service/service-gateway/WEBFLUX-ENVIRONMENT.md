# Gateway WebFlux环境配置说明

## 问题描述

Gateway服务启动时出现以下错误：

```
Description:
Web application could not be started as there was no org.springframework.boot.web.servlet.server.ServletWebServerFactory bean defined in the context.

Action:
Check your application's dependencies for a supported servlet web server.
Check the configured web application type.
```

## 原因分析

1. **Web环境冲突**: Spring Cloud Gateway需要WebFlux环境，但service-base包含了传统的Servlet Web依赖
2. **依赖冲突**: `spring-boot-starter-web`与`spring-cloud-starter-gateway`冲突
3. **配置错误**: 应用配置了错误的Web环境类型

## 解决方案

### 1. 排除Servlet Web依赖

在Gateway服务的service-base依赖中排除Servlet Web相关依赖：

```xml
<dependency>
    <groupId>com.origin</groupId>
    <artifactId>service-base</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <exclusions>
        <!-- 排除Servlet Web依赖，Gateway使用WebFlux -->
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </exclusion>
        <!-- 排除Validation依赖，Gateway不需要 -->
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </exclusion>
        <!-- 排除MyBatis Plus，Gateway不需要数据库 -->
        <exclusion>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
        </exclusion>
        <!-- 排除Redis，Gateway不需要 -->
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </exclusion>
        <!-- 排除OSS，Gateway不需要 -->
        <exclusion>
            <groupId>com.aliyun.oss</groupId>
            <artifactId>aliyun-sdk-oss</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

### 2. 配置WebFlux环境

创建专门的WebFlux配置类：

```java
@Configuration
@EnableWebFlux
public class GatewayWebConfig implements WebFluxConfigurer {
    // WebFlux配置
}
```

### 3. 确保包扫描配置

在GatewayApplication中配置正确的包扫描：

```java
@SpringBootApplication(scanBasePackages = {"com.origin"})
@EnableDiscoveryClient
public class GatewayApplication {
    // ...
}
```

## WebFlux vs Servlet Web

### Spring Cloud Gateway (WebFlux)
- **特点**: 响应式编程模型，非阻塞I/O
- **适用场景**: 高并发、低延迟的API网关
- **依赖**: `spring-cloud-starter-gateway`
- **Web环境**: WebFlux

### 传统Spring Boot Web (Servlet)
- **特点**: 传统的同步编程模型
- **适用场景**: 业务服务、REST API
- **依赖**: `spring-boot-starter-web`
- **Web环境**: Servlet

## 依赖管理策略

### Gateway服务需要的依赖
- `spring-cloud-starter-gateway` (包含WebFlux)
- `spring-boot-starter-actuator`
- `spring-cloud-starter-alibaba-nacos-discovery`
- `spring-cloud-starter-alibaba-nacos-config`
- `service-base` (排除Servlet Web依赖)

### Gateway服务不需要的依赖
- `spring-boot-starter-web` (Servlet Web)
- `spring-boot-starter-validation`
- `mybatis-plus-spring-boot3-starter`
- `spring-boot-starter-data-redis`
- `aliyun-sdk-oss`

## 配置验证

### 1. 检查Web环境
启动后检查日志，确认使用的是WebFlux环境：
```
Started GatewayApplication in X.XXX seconds (process running for X.XXX)
```

### 2. 验证路由功能
测试Gateway路由是否正常工作：
```bash
curl -X GET "http://localhost:8080/actuator/health"
```

### 3. 检查依赖冲突
使用Maven命令检查依赖：
```bash
./mvnw dependency:tree -pl service/service-gateway
```

## 注意事项

1. **环境隔离**: Gateway服务必须使用WebFlux环境，不能与Servlet Web混用
2. **依赖排除**: 必须排除service-base中的Servlet Web依赖
3. **配置分离**: Gateway的配置应该与其他服务分离
4. **性能优化**: WebFlux环境适合高并发场景

## 相关文件

- `service/service-gateway/pom.xml`
- `service/service-gateway/src/main/java/com/origin/gateway/config/GatewayWebConfig.java`
- `service/service-gateway/src/main/java/com/origin/gateway/GatewayApplication.java`
- `service/service-gateway/src/main/resources/dev/application.yml` 
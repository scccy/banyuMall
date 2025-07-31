> **文档位置**: infra/moudleDocs/{模块名称}/design.md

# Service-Base 模块设计文档

## 1. 模块概述

### 1.1 模块职责
**Service-Base** 是BanyuMall微服务架构的基础配置模块，负责提供Spring Boot公共配置，为其他业务模块提供统一的基础设施支持。

### 1.2 核心功能
- **配置管理**: 提供MyBatis-Plus、Redis、Knife4j等核心组件的统一配置
- **异常处理**: 全局异常处理器，统一处理各类异常
- **跨域配置**: 提供CORS跨域支持
- **事务管理**: 声明式事务配置
- **日志配置**: Log4j2日志框架配置
- **文件上传**: OSS对象存储配置
- **服务调用**: OpenFeign客户端配置

### 1.3 技术栈
- **框架**: Spring Boot 3.x
- **ORM**: MyBatis-Plus
- **缓存**: Redis + FastJSON2
- **文档**: Knife4j (Swagger)
- **日志**: Log4j2
- **文件存储**: OSS
- **服务调用**: OpenFeign
- **事务**: Spring声明式事务

## 2. 配置类设计

### 2.1 MyBatis-Plus配置
**文件**: `MyBatisPlusConfig.java`
**职责**: 配置MyBatis-Plus分页插件和数据库类型

```java
@Configuration
@ConditionalOnClass(MybatisConfiguration.class)
public class MyBatisPlusConfig {
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
```

### 2.2 Redis配置
**文件**: `RedisConfig.java`
**职责**: 配置RedisTemplate，使用FastJSON2序列化

```java
@Configuration
@ConditionalOnClass(RedisTemplate.class)
public class RedisConfig {
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        // 使用FastJsonRedisSerializer序列化value
        // 使用StringRedisSerializer序列化key
    }
}
```

### 2.3 Knife4j配置
**文件**: `Knife4jConfig.java`
**职责**: 配置API文档信息

```java
@Configuration
public class Knife4jConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("BanyuMall API文档")
                .description("BanyuMall微服务架构API接口文档")
                .version("1.0.0"));
    }
}
```

### 2.4 全局异常处理器
**文件**: `GlobalExceptionHandler.java`
**职责**: 统一处理各类异常，返回标准响应格式

**支持的异常类型**:
- `BusinessException`: 业务异常
- `MethodArgumentNotValidException`: 参数校验异常
- `BindException`: 参数绑定异常
- `ConstraintViolationException`: 约束违反异常
- `RuntimeException`: 运行时异常
- `Exception`: 其他异常

### 2.5 Web MVC配置
**文件**: `WebMvcConfig.java`
**职责**: 配置跨域、拦截器、消息转换器等

### 2.6 事务配置
**文件**: `TransactionConfig.java`
**职责**: 配置声明式事务管理

### 2.7 日志配置
**文件**: `Log4j2Config.java`
**职责**: 配置Log4j2日志框架

### 2.8 OSS配置
**文件**: `OssConfig.java`
**职责**: 配置对象存储服务

### 2.9 OpenFeign配置
**文件**: `OpenFeignConfig.java`
**职责**: 配置Feign客户端

## 3. 项目结构

```
src/main/java/com/origin/base/
├── config/           # 配置类
│   ├── MyBatisPlusConfig.java
│   ├── RedisConfig.java
│   ├── Knife4jConfig.java
│   ├── WebMvcConfig.java
│   ├── TransactionConfig.java
│   ├── Log4j2Config.java
│   ├── OssConfig.java
│   └── OpenFeignConfig.java
└── exception/        # 异常处理
    └── GlobalExceptionHandler.java
```

## 4. 应用配置

### 4.1 本地配置
```yaml
# application.yml
spring:
  profiles:
    active: dev
  application:
    name: service-base
```

### 4.2 Nacos配置
```yaml
# nacos-config-template-base.yml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/banyumall?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: ${MYSQL_USERNAME:root}
    password: ${MYSQL_PASSWORD:123456}
  
  redis:
    host: ${REDIS_HOST:localhost}
    port: ${REDIS_PORT:6379}
    database: 0
    timeout: 3000ms
    lettuce:
      pool:
        max-active: 8
        max-idle: 8
        min-idle: 0
        max-wait: -1ms

mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  global-config:
    db-config:
      logic-delete-field: isDeleted
      logic-delete-value: 1
      logic-not-delete-value: 0

knife4j:
  enable: true
  setting:
    language: zh-CN
    enable-swagger-models: true
    enable-document-manage: true
    swagger-model-name: 实体类列表
```

## 5. 依赖管理

### 5.1 核心依赖
```xml
<dependencies>
    <!-- Spring Boot Starter -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    
    <!-- MyBatis Plus -->
    <dependency>
        <groupId>com.baomidou</groupId>
        <artifactId>mybatis-plus-boot-starter</artifactId>
    </dependency>
    
    <!-- Redis -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-redis</artifactId>
    </dependency>
    
    <!-- FastJSON2 -->
    <dependency>
        <groupId>com.alibaba.fastjson2</groupId>
        <artifactId>fastjson2</artifactId>
    </dependency>
    
    <!-- Knife4j -->
    <dependency>
        <groupId>com.github.xiaoymin</groupId>
        <artifactId>knife4j-openapi3-spring-boot-starter</artifactId>
    </dependency>
    
    <!-- Log4j2 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-log4j2</artifactId>
    </dependency>
    
    <!-- OpenFeign -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-openfeign</artifactId>
    </dependency>
    
    <!-- Validation -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
</dependencies>
```

### 5.2 依赖排除
```xml
<!-- 排除默认的Jackson和Logback -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <exclusions>
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-logging</artifactId>
        </exclusion>
        <exclusion>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

## 6. 使用说明

### 6.1 引入依赖
其他业务模块需要引入service-base依赖：
```xml
<dependency>
    <groupId>com.origin</groupId>
    <artifactId>service-base</artifactId>
    <version>${project.version}</version>
</dependency>
```

### 6.2 自动配置
引入依赖后，以下配置会自动生效：
- MyBatis-Plus分页插件
- Redis序列化配置
- 全局异常处理
- 跨域配置
- 事务管理
- 日志配置

### 6.3 配置覆盖
业务模块可以通过以下方式覆盖默认配置：
- 在`application.yml`中重新定义配置项
- 创建同名的配置类并添加`@Primary`注解
- 使用`@ConditionalOnMissingBean`条件注解

## 7. 最佳实践

### 7.1 配置原则
- **条件化配置**: 使用`@ConditionalOnClass`等注解确保配置只在需要时生效
- **默认值设置**: 为所有配置项提供合理的默认值
- **环境适配**: 支持不同环境的配置差异

### 7.2 异常处理
- **统一响应格式**: 所有异常都返回`ResultData`格式
- **日志记录**: 记录异常详情便于问题排查
- **安全考虑**: 生产环境不暴露敏感错误信息

### 7.3 性能优化
- **懒加载**: 配置类使用懒加载减少启动时间
- **连接池**: 合理配置数据库和Redis连接池
- **序列化**: 使用高效的FastJSON2序列化

## 8. 版本历史

| 版本 | 日期 | 变更内容 |
|------|------|----------|
| 1.0.0 | 2024-12-19 | 初始版本，包含基础配置 |
| 1.1.0 | 2025-01-27 | 添加全局异常处理器 |
| 1.2.0 | 2025-07-31 | 优化配置结构，添加条件化配置 | 
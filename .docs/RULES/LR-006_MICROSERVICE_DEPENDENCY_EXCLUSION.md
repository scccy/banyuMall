# 微服务依赖排除规则

**ID**: LR-006  
**Name**: 微服务依赖排除规则  
**Status**: Active  
**创建时间**: 2025-07-30  
**重命名时间**: 2025-08-02 (从LR-004重命名)

## 触发情景 (Context/Trigger)
当创建新的微服务模块或修改现有微服务的依赖配置时。

## 指令 (Directive)

### 1. 日志框架统一
- **必须 (MUST)** 在所有微服务模块中排除Logback相关依赖
- **必须 (MUST)** 统一使用Log4j2作为日志框架
- **禁止 (MUST NOT)** 在微服务中引入Logback或Commons Logging

### 2. JSON序列化统一
- **必须 (MUST)** 在所有微服务模块中排除Jackson相关依赖
- **必须 (MUST)** 统一使用FastJSON2作为JSON序列化框架
- **禁止 (MUST NOT)** 在微服务中引入Jackson

### 3. 依赖排除配置
每个微服务模块的`pom.xml`中必须包含以下排除配置：

#### 3.1 Spring Boot Starter Web
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <exclusions>
        <!-- 排除Jackson，使用FastJSON2 -->
        <exclusion>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
        </exclusion>
        <exclusion>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-core</artifactId>
        </exclusion>
        <exclusion>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-annotations</artifactId>
        </exclusion>
        <exclusion>
            <groupId>com.fasterxml.jackson.datatype</groupId>
            <artifactId>jackson-datatype-jsr310</artifactId>
        </exclusion>
        <exclusion>
            <groupId>com.fasterxml.jackson.datatype</groupId>
            <artifactId>jackson-datatype-jdk8</artifactId>
        </exclusion>
        <exclusion>
            <groupId>com.fasterxml.jackson.module</groupId>
            <artifactId>jackson-module-parameter-names</artifactId>
        </exclusion>
        <!-- 排除默认日志，使用Log4j2 -->
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-logging</artifactId>
        </exclusion>
        <!-- 排除commons-logging -->
        <exclusion>
            <groupId>commons-logging</groupId>
            <artifactId>commons-logging</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

#### 3.2 其他Spring Boot Starters
对于以下依赖，必须添加相同的排除配置：
- `spring-cloud-starter-openfeign`
- `spring-boot-starter-actuator`
- `mybatis-plus-spring-boot3-starter`
- `spring-boot-starter-data-redis`
- `spring-boot-starter-validation`
- `knife4j-openapi3-jakarta-spring-boot-starter`
- `spring-boot-starter-jdbc`
- `spring-boot-starter-aop`

#### 3.3 显式添加依赖
```xml
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

<!-- FastJSON2 Spring Boot Starter -->
<dependency>
    <groupId>com.alibaba.fastjson2</groupId>
    <artifactId>fastjson2-extension-spring6</artifactId>
</dependency>
```

### 4. 配置验证
- **必须 (MUST)** 验证排除配置生效
- **必须 (MUST)** 确保Log4j2配置文件正确加载
- **必须 (MUST)** 确保FastJSON2序列化正常工作

## 理由 (Justification)
此规则源于多个任务的经验总结，确保微服务架构中日志框架和JSON序列化框架的统一性，避免依赖冲突和配置不一致问题。

## 版本历史
| 版本 | 日期 | 变更内容 |
|------|------|----------|
| 1.0.0 | 2025-07-30 | 初始版本 |
| 1.1.0 | 2025-08-02 | 重命名为LR-006，解决规则ID冲突 | 
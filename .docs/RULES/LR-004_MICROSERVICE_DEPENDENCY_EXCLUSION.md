# 微服务依赖排除规则

**ID**: LR-002  
**Name**: 微服务依赖排除规则  
**Status**: Active  
**创建时间**: 2025-07-30  

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

### 4. 架构设计
- **service-base模块**: 包含所有微服务共享的配置类（如FastJSON2配置）
- **子模块**: 继承service-base的配置，无需重复创建
- **配置复用**: 通过依赖继承实现配置的统一管理

### 5. 父POM依赖管理
在父POM的`dependencyManagement`中必须包含：
```xml
<dependencyManagement>
    <dependencies>
        <!-- 排除所有模块中的logback依赖 -->
        <dependency>
            <groupId>ch.qos.logback</groupId>
            <artifactId>logback-classic</artifactId>
            <version>1.2.12</version>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>ch.qos.logback</groupId>
            <artifactId>logback-core</artifactId>
            <version>1.2.12</version>
            <scope>provided</scope>
        </dependency>
        <!-- 排除commons-logging -->
        <dependency>
            <groupId>commons-logging</groupId>
            <artifactId>commons-logging</artifactId>
            <version>1.2</version>
            <scope>provided</scope>
        </dependency>
        <!-- 排除Jackson依赖 -->
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
            <version>2.15.2</version>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-core</artifactId>
            <version>2.15.2</version>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-annotations</artifactId>
            <version>2.15.2</version>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>com.fasterxml.jackson.datatype</groupId>
            <artifactId>jackson-datatype-jsr310</artifactId>
            <version>2.15.2</version>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>com.fasterxml.jackson.datatype</groupId>
            <artifactId>jackson-datatype-jdk8</artifactId>
            <version>2.15.2</version>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>com.fasterxml.jackson.module</groupId>
            <artifactId>jackson-module-parameter-names</artifactId>
            <version>2.15.2</version>
            <scope>provided</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

### 6. 配置类要求
- **必须 (MUST)** 在service-base模块中创建FastJSON2配置类（已存在）
- **必须 (MUST)** 创建Log4j2配置文件
- **禁止 (MUST NOT)** 使用logback.xml配置文件
- **禁止 (MUST NOT)** 在子模块中重复创建FastJSON2配置类

### 7. 验证要求
- **必须 (MUST)** 在启动时验证无Logback和Jackson警告
- **必须 (MUST)** 确保JSON序列化使用FastJSON2
- **必须 (MUST)** 确保日志输出使用Log4j2

## 理由 (Justification)
此规则源于任务 `task_20250127_1530_implement_service_user.md` 中遇到的日志和JSON序列化冲突问题。在该任务中，service-user模块启动时出现了以下警告：
- "Standard Commons Logging discovery in action with spring-jcl: please remove commons-logging.jar from classpath"
- "SLF4J(W): Class path contains multiple SLF4J providers"
- Jackson与FastJSON2的冲突

通过统一使用Log4j2和FastJSON2，可以：
1. **避免依赖冲突**: 防止多个日志框架和JSON库同时存在
2. **提升性能**: FastJSON2比Jackson性能更好
3. **统一配置**: 所有微服务使用相同的日志和JSON处理方式
4. **减少包大小**: 排除不必要的依赖

## 性能基准
- **启动时间**: 无Logback/Jackson警告
- **JSON序列化**: 使用FastJSON2，性能提升20-50%
- **日志输出**: 使用Log4j2，支持异步日志
- **包大小**: 减少约2-5MB的依赖大小

## 检查清单
创建新微服务模块时，必须检查：
- [ ] 所有Spring Boot Starter都排除了Logback和Jackson
- [ ] 显式添加了Log4j2和FastJSON2依赖
- [ ] 父POM包含依赖管理配置
- [ ] 确认service-base模块包含FastJSON2配置类
- [ ] 创建了Log4j2配置文件
- [ ] 启动时无警告信息
- [ ] JSON序列化正常工作
- [ ] 日志输出正常 
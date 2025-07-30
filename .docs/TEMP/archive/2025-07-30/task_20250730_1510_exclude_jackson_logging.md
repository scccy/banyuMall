# 任务：排除Jackson和Spring默认日志依赖
状态: 已完成

目标: 在spring-boot-starter-web中排除Jackson和Spring默认日志依赖，使用FastJSON2和Log4j2

## 问题分析

### 发现的问题
- spring-boot-starter-web默认包含Jackson依赖，与FastJSON2冲突
- spring-boot-starter-web默认包含logback日志，与Log4j2冲突
- 需要排除这些默认依赖以避免冲突和减少包大小

### 解决方案
在spring-boot-starter-web的依赖声明中添加exclusions，排除Jackson和默认日志依赖。

## 执行步骤
[x] 步骤 1: 检查项目中spring-boot-starter-web的使用情况
[x] 步骤 2: 分析需要排除的Jackson和日志依赖
[x] 步骤 3: 在service-base模块中配置exclusions
[x] 步骤 4: 验证其他模块的依赖继承情况

## 具体变更

### service-base/pom.xml更新
在spring-boot-starter-web中添加了exclusions配置：

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
    </exclusions>
</dependency>
```

## 排除的依赖说明

### Jackson相关依赖
- `jackson-databind`: Jackson的核心数据绑定库
- `jackson-core`: Jackson的核心流处理库
- `jackson-annotations`: Jackson的注解库
- `jackson-datatype-jsr310`: Java 8时间类型支持
- `jackson-datatype-jdk8`: Java 8 Optional等类型支持
- `jackson-module-parameter-names`: 参数名支持模块

### 日志相关依赖
- `spring-boot-starter-logging`: Spring Boot默认的logback日志

## 优化效果
- ✅ 避免了Jackson与FastJSON2的冲突
- ✅ 避免了logback与Log4j2的冲突
- ✅ 减少了项目依赖包大小
- ✅ 统一使用FastJSON2和Log4j2
- ✅ 提高了项目的一致性

## 模块依赖关系
- service-auth模块依赖service-base，会自动继承这些exclusions配置
- infra-gateway模块不依赖spring-boot-starter-web，无需配置 
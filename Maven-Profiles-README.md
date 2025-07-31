# Maven Profiles 配置说明

## 概述

本项目已配置Maven Profiles来支持多环境部署，包括开发环境(dev)、测试环境(test)和生产环境(prod)。

## Profiles 配置

### 1. 开发环境 (dev) - 默认激活
```xml
<profile>
   <id>dev</id>
   <properties>
      <profiles.activation>dev</profiles.activation>
   </properties>
   <activation>
      <activeByDefault>true</activeByDefault>
   </activation>
</profile>
```

### 2. 生产环境 (prod)
```xml
<profile>
   <id>prod</id>
   <properties>
      <profiles.activation>prod</profiles.activation>
   </properties>
</profile>
```

### 3. 测试环境 (test)
```xml
<profile>
   <id>test</id>
   <properties>
      <profiles.activation>test</profiles.activation>
   </properties>
</profile>
```

## 资源文件管理

### 资源目录结构
```
src/main/resources/
├── application.yml          # 主配置文件
├── application-dev.yml      # 开发环境配置
├── application-test.yml     # 测试环境配置
├── application-prod.yml     # 生产环境配置
├── dev/                     # 开发环境特定资源
├── test/                    # 测试环境特定资源
└── prod/                    # 生产环境特定资源
```

### 资源处理规则
1. **Java目录资源**: 包含 `src/main/java` 目录下的 `.properties` 和 `.xml` 文件
2. **主资源目录**: 包含 `src/main/resources` 目录，但排除环境特定目录
3. **环境特定资源**: 根据激活的profile动态包含对应的环境目录

## 使用方法

### 1. 激活特定环境
```bash
# 开发环境（默认）
mvn clean package

# 测试环境
mvn clean package -P test

# 生产环境
mvn clean package -P prod
```

### 2. 使用Maven Wrapper
```bash
# 开发环境
./mvnw clean package

# 测试环境
./mvnw clean package -P test

# 生产环境
./mvnw clean package -P prod
```

### 3. 运行Spring Boot应用
```bash
# 开发环境
./mvnw spring-boot:run -pl service/service-auth

# 测试环境
./mvnw spring-boot:run -pl service/service-auth -P test

# 生产环境
./mvnw spring-boot:run -pl service/service-auth -P prod
```

## 插件配置

### 1. Spring Boot Maven Plugin
- **fork**: 启用fork模式，支持devtools热重启
- **includeSystemScope**: 包含系统范围的依赖

### 2. Maven Resources Plugin
- **encoding**: 设置为UTF-8，确保资源文件编码正确

### 3. Maven Compiler Plugin
- **source/target**: 使用Java 21
- **annotationProcessorPaths**: 配置Lombok注解处理器

## 环境特定配置

### 开发环境特性
- 自动激活（activeByDefault=true）
- 支持热重启
- 详细日志输出
- 本地数据库连接

### 测试环境特性
- 测试数据库连接
- 测试级别日志
- 禁用热重启

### 生产环境特性
- 生产数据库连接
- 精简日志输出
- 性能优化配置
- 安全加固

## 注意事项

1. **环境变量**: 生产环境敏感信息应使用环境变量
2. **配置文件**: 不同环境的配置文件应分别管理
3. **资源文件**: 环境特定的资源文件应放在对应的目录中
4. **构建顺序**: 先构建基础模块，再构建业务模块

## 最佳实践

1. **配置分离**: 将环境特定配置与通用配置分离
2. **安全考虑**: 生产环境配置不应包含敏感信息
3. **版本控制**: 配置文件应纳入版本控制
4. **文档维护**: 及时更新配置说明文档 
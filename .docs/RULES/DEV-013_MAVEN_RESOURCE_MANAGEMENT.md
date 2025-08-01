# Maven资源文件管理规则

**ID**: DEV-013  
**Name**: Maven资源文件管理规则  
**Status**: Active  
**创建时间**: 2025-01-27  
**重命名时间**: 2025-08-02 (从DEV-008分离)

## 概述

本规则定义了项目中Maven资源文件的管理策略，包括环境特定的配置文件组织、打包逻辑和最佳实践。

## 触发情景 (Context/Trigger)

当需要为不同环境（开发、测试、生产）配置不同的资源文件时，必须遵循本规则。

## 指令 (Directive)

### 1. 资源目录结构

**必须 (MUST)** 按照以下结构组织资源文件：

```
src/main/resources/
├── application.yml              # 主配置文件（基础配置）
├── application-dev.yml          # 开发环境配置（已废弃，移至dev目录）
├── application-test.yml         # 测试环境配置（已废弃，移至test目录）
├── application-prod.yml         # 生产环境配置（已废弃，移至prod目录）
├── dev/                         # 开发环境特定资源
│   └── application.yml          # 开发环境配置
├── test/                        # 测试环境特定资源
│   └── application.yml          # 测试环境配置
└── prod/                        # 生产环境特定资源
    └── application.yml          # 生产环境配置
```

### 2. Maven Profiles配置

**必须 (MUST)** 在根pom.xml中配置以下profiles：

```xml
<profiles>
    <profile>
       <id>dev</id>
       <properties>
          <profiles.activation>dev</profiles.activation>
       </properties>
       <activation>
          <activeByDefault>true</activeByDefault>
       </activation>
    </profile>
    <profile>
       <id>prod</id>
       <properties>
          <profiles.activation>prod</profiles.activation>
       </properties>
    </profile>
    <profile>
       <id>test</id>
       <properties>
          <profiles.activation>test</profiles.activation>
       </properties>
    </profile>
</profiles>
```

### 3. 资源文件处理规则

**必须 (MUST)** 配置以下资源处理规则：

```xml
<resources>
   <resource>
      <directory>src/main/java</directory>
      <includes>
         <include>**/*.properties</include>
         <include>**/*.xml</include>
      </includes>
   </resource>
   <resource>
      <directory>src/main/resources</directory>
      <excludes>
         <exclude>test/*</exclude>
         <exclude>prod/*</exclude>
         <exclude>dev/*</exclude>
      </excludes>
   </resource>
   <resource>
      <directory>src/main/resources/${profiles.activation}</directory>
   </resource>
</resources>
```

### 4. 配置文件内容规范

#### 主配置文件 (application.yml)
**必须 (MUST)** 只包含：
- 服务器基础配置
- Spring基础配置
- Nacos连接配置
- 管理端点配置

#### 环境特定配置文件
**必须 (MUST)** 包含：
- 数据库连接配置
- Redis连接配置
- 日志配置
- 业务相关配置

### 5. 打包逻辑

**必须 (MUST)** 对不同文件类型应用特定的处理规则：
- **配置文件**: 根据环境变量选择
- **静态资源**: 直接复制到目标目录
- **模板文件**: 根据环境进行变量替换

## 理由 (Justification)

此规则确保项目在不同环境下的配置管理一致性和可维护性，避免配置错误和部署问题。

## 版本历史
| 版本 | 日期 | 变更内容 |
|------|------|----------|
| 1.0.0 | 2025-01-27 | 初始版本 |
| 1.1.0 | 2025-08-02 | 重命名为DEV-013，解决规则ID冲突 | 
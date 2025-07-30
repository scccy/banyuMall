# Maven模块配置修复说明

## 问题描述
遇到错误：`源根 '/Volumes/project/github/banyuMall/service/service-base/src/main/java' 在模块 'service-base' 中重复。`

## 问题原因
service-base模块的POM文件中定义了重复的版本属性，导致Maven编译时出现路径冲突。

## 解决方案

### 1. 移除重复的版本属性
在`service/service-base/pom.xml`中移除了以下重复配置：
```xml
<!-- 移除的重复配置 -->
<properties>
    <spring-boot.version>3.5.4</spring-boot.version>  <!-- 重复 -->
    <spring-cloud.version>2023.0.0</spring-cloud.version>  <!-- 重复 -->
</properties>
```

### 2. 移除重复的dependencyManagement
在`service/service-base/pom.xml`中移除了重复的dependencyManagement配置：
```xml
<!-- 移除的重复配置 -->
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-dependencies</artifactId>
            <version>${spring-cloud.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

### 3. 版本管理原则
- **根POM**: 统一管理所有版本属性
- **父模块**: 继承根POM的版本配置
- **子模块**: 不重复定义版本属性

### 4. 项目结构
```
banyuMall/
├── pom.xml                     # 根POM (统一版本管理)
├── service/
│   ├── pom.xml                 # service父模块
│   ├── service-base/
│   │   ├── pom.xml             # service-base模块
│   │   └── src/main/java/
│   └── service-auth/
│       ├── pom.xml             # service-auth模块
│       └── src/main/java/
└── infra/
    ├── pom.xml                 # infra父模块
    └── infra-gateway/
        ├── pom.xml             # infra-gateway模块
        └── src/main/java/
```

### 5. 验证步骤
```bash
# 清理项目
./mvnw clean

# 编译项目
./mvnw compile

# 检查依赖树
./mvnw dependency:tree
```

## 注意事项
- 确保每个模块只定义自己特有的版本属性
- 通用版本属性统一在根POM中管理
- 避免在子模块中重复定义父模块已有的配置 
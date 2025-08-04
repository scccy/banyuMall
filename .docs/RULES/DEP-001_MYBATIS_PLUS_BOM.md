# MyBatis-Plus Maven BOM安装管理规则

## 📋 规则概述

**ID**: DEP-001  
**Name**: MyBatis-Plus Maven BOM安装管理  
**Status**: Active  
**创建时间**: 2025-01-27  
**参考文档**: [MyBatis-Plus官方安装文档](https://baomidou.com/getting-started/install/)

## 🎯 核心原则

### 1. 使用Maven BOM管理依赖
- **必须 (MUST)** 使用 `mybatis-plus-bom` 管理MyBatis-Plus相关依赖版本
- **必须 (MUST)** 避免版本冲突，统一管理依赖版本
- **禁止 (MUST NOT)** 直接引入MyBatis相关依赖，避免版本差异问题

### 2. 依赖版本选择
- **Spring Boot 2.x**: 使用 `mybatis-plus-boot-starter`
- **Spring Boot 3.x**: 使用 `mybatis-plus-spring-boot3-starter`
- **JDK 8**: 使用 `mybatis-plus-jsqlparser-4.9`
- **JDK 11+**: 使用 `mybatis-plus-jsqlparser`

## 🔧 配置实现

### Maven配置
```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-bom</artifactId>
            <version>3.5.12</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>

<!-- Spring Boot 3.x 引入 -->
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
</dependency>

<!-- JDK 11+ 引入 -->
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-jsqlparser</artifactId>
</dependency>

<!-- JDK 8 引入 -->
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-jsqlparser-4.9</artifactId>
</dependency>
```

### Gradle配置
```gradle
// 依赖管理
dependencyManagement {
    imports {
        mavenBom "com.baomidou:mybatis-plus-bom:3.5.12"
    }
}

// Spring Boot 3.x 引入
implementation("com.baomidou:mybatis-plus-spring-boot3-starter")

// JDK 11+ 引入
implementation("com.baomidou:mybatis-plus-jsqlparser")

// JDK 8 引入
implementation("com.baomidou:mybatis-plus-jsqlparser-4.9")
```

## 📋 版本兼容性

### Spring Boot版本兼容
| Spring Boot版本 | MyBatis-Plus版本 | 推荐Starter |
|----------------|------------------|-------------|
| 2.x | 3.5.x | mybatis-plus-boot-starter |
| 3.x | 3.5.x | mybatis-plus-spring-boot3-starter |

### JDK版本兼容
| JDK版本 | 推荐jsqlparser版本 | 说明 |
|---------|-------------------|------|
| JDK 8 | mybatis-plus-jsqlparser-4.9 | 固定版本，确保兼容性 |
| JDK 11+ | mybatis-plus-jsqlparser | 跟随最新版本更新 |

## 🚫 禁止事项

### 1. 依赖冲突
```xml
<!-- 禁止：不要同时引入MyBatis相关依赖 -->
<dependency>
    <groupId>org.mybatis.spring.boot</groupId>
    <artifactId>mybatis-spring-boot-starter</artifactId>
</dependency>
<dependency>
    <groupId>org.mybatis</groupId>
    <artifactId>mybatis</artifactId>
</dependency>
```

### 2. 版本不一致
```xml
<!-- 禁止：不要指定具体版本号 -->
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-boot-starter</artifactId>
    <version>3.5.12</version>  <!-- 不要指定版本 -->
</dependency>
```

## ✅ 正确配置示例

### 父项目配置 (推荐)
```xml
<!-- 在父项目的pom.xml中配置 -->
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-bom</artifactId>
            <version>3.5.12</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

### 子模块配置
```xml
<!-- 在子模块的pom.xml中配置 -->
<dependencies>
    <!-- Spring Boot 3.x 项目 -->
    <dependency>
        <groupId>com.baomidou</groupId>
        <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
    </dependency>
    
    <!-- JDK 11+ 项目 -->
    <dependency>
        <groupId>com.baomidou</groupId>
        <artifactId>mybatis-plus-jsqlparser</artifactId>
    </dependency>
</dependencies>
```

## 🔍 验证配置

### 1. 依赖检查
```bash
# 检查依赖树，确保没有版本冲突
mvn dependency:tree | grep mybatis
```

### 2. 版本验证
```java
// 在测试中验证版本
@Test
public void testMyBatisPlusVersion() {
    // 验证MyBatis-Plus版本
    String version = MybatisPlusVersion.getVersion();
    assertThat(version).startsWith("3.5");
}
```

## 📊 性能优化

### 1. 依赖优化
- **使用BOM**: 统一管理版本，减少版本冲突
- **按需引入**: 只引入必要的模块
- **版本锁定**: 避免意外版本升级

### 2. 启动优化
- **自动配置**: 利用Spring Boot自动配置
- **懒加载**: 合理配置懒加载策略
- **连接池**: 配置合适的数据库连接池

## 🚨 常见问题

### 1. 版本冲突
**问题**: 出现MyBatis版本冲突错误
**解决**: 移除直接引入的MyBatis依赖，使用BOM管理

### 2. 启动失败
**问题**: Spring Boot启动时MyBatis-Plus配置错误
**解决**: 检查Starter版本与Spring Boot版本兼容性

### 3. 功能异常
**问题**: 某些MyBatis-Plus功能无法使用
**解决**: 检查是否引入了正确的jsqlparser版本

## 📝 配置检查清单

- [ ] 已配置 `mybatis-plus-bom` 依赖管理
- [ ] 已引入正确的Starter依赖
- [ ] 已引入正确的jsqlparser版本
- [ ] 已移除冲突的MyBatis依赖
- [ ] 已验证依赖版本兼容性
- [ ] 已配置正确的数据库连接

---

**版本**: v1.0  
**创建日期**: 2025-01-27  
**维护者**: scccy  
**参考**: [MyBatis-Plus官方文档](https://baomidou.com/getting-started/install/) 
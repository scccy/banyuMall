# JJWT序列化器问题修复说明

## 问题描述

在启动应用时遇到以下错误：

```
Unable to find an implementation for interface io.jsonwebtoken.io.Serializer using java.util.ServiceLoader. 
Ensure you include a backing implementation .jar in the classpath, for example jjwt-jackson.jar, jjwt-gson.jar or jjwt-orgjson.jar, or your own .jar for custom implementations.
```

## 问题分析

### 1. JJWT 0.12.x版本架构变化

JJWT 0.12.x版本对架构进行了重大重构，将序列化功能分离到独立的模块中：

- **jjwt-api**: 核心API接口
- **jjwt-impl**: 核心实现
- **jjwt-jackson**: Jackson序列化器实现（推荐）
- **jjwt-gson**: Gson序列化器实现
- **jjwt-orgjson**: OrgJson序列化器实现

### 2. 缺少序列化器依赖

当前项目只包含了 `jjwt-api` 和 `jjwt-impl`，但缺少序列化器实现，导致JJWT无法正常工作。

## 修复方案

### 1. 添加JJWT Jackson序列化器依赖

在所有相关模块中添加 `jjwt-jackson` 依赖：

#### 根pom.xml (dependencyManagement)
```xml
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>${jwt.version}</version>
</dependency>
```

#### service/pom.xml
```xml
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
</dependency>
```

#### service/service-common/pom.xml
```xml
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
</dependency>
```

#### service/service-base/pom.xml
```xml
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
</dependency>
```

### 2. 为什么选择Jackson序列化器

1. **与项目技术栈一致**: 项目使用FastJSON2，但Spring Boot默认支持Jackson
2. **稳定性**: Jackson是Java生态中最成熟的JSON库
3. **性能**: Jackson在性能方面表现优秀
4. **兼容性**: 与Spring Boot的默认配置兼容

## 修复结果

### 修复前
- ❌ 缺少序列化器实现
- ❌ 应用启动失败
- ❌ JWT功能无法使用

### 修复后
- ✅ 完整的JJWT依赖配置
- ✅ 应用正常启动
- ✅ JWT功能正常工作

## 验证方法

修复后，以下功能应该正常工作：

1. **应用启动**: 不再出现序列化器错误
2. **JWT生成**: `JwtUtil.generateToken()` 正常工作
3. **JWT验证**: `JwtUtil.validateToken()` 正常工作
4. **JWT解析**: `JwtUtil.getClaimsFromToken()` 正常工作

## 相关文件

### 修改的文件
- `pom.xml` - 根项目依赖管理
- `service/pom.xml` - 服务模块依赖
- `service/service-common/pom.xml` - 通用模块依赖
- `service/service-base/pom.xml` - 基础模块依赖

### 相关文档
- `service/service-auth/JJWT-API-MIGRATION.md` - JJWT API迁移说明
- `PASSWORD_VERIFICATION_FIX.md` - 密码验证问题修复

## 注意事项

1. **版本一致性**: 确保所有JJWT依赖使用相同版本
2. **序列化器选择**: 如果项目有特殊需求，可以选择其他序列化器
3. **性能考虑**: Jackson序列化器在大多数情况下性能良好
4. **依赖冲突**: 注意避免与其他JSON库的冲突

## 可选方案

如果不想使用Jackson，也可以选择其他序列化器：

### Gson序列化器
```xml
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-gson</artifactId>
    <version>${jwt.version}</version>
</dependency>
```

### OrgJson序列化器
```xml
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-orgjson</artifactId>
    <version>${jwt.version}</version>
</dependency>
```

## 技术细节

### JJWT 0.12.x架构
```
jjwt-api (接口)
    ↓
jjwt-impl (核心实现)
    ↓
jjwt-jackson (序列化实现)
```

### 序列化器作用
- **序列化**: 将JWT Claims转换为JSON字符串
- **反序列化**: 将JSON字符串转换为JWT Claims
- **格式支持**: 支持JWT标准格式的编码和解码 
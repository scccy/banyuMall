# JJWT API 迁移说明

## 问题描述

在使用JJWT 0.12.5版本时，遇到了以下编译错误：

```
java: io.jsonwebtoken中的io.jsonwebtoken.SignatureAlgorithm已过时
java: 找不到符号 方法 parserBuilder()
```

## 原因分析

JJWT 0.12.x版本对API进行了重大更新，移除了过时的API并引入了新的API。

## 修复内容

### 1. 移除过时的导入

**修复前**:
```java
import io.jsonwebtoken.SignatureAlgorithm;
```

**修复后**:
```java
// 移除SignatureAlgorithm导入，新版本自动处理算法
```

### 2. 更新JWT解析API

**修复前**:
```java
return Jwts.parserBuilder()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
```

**修复后**:
```java
return Jwts.parser()
        .verifyWith(getSigningKey())
        .build()
        .parseSignedClaims(token)
        .getPayload();
```

### 3. 更新JWT构建API

**修复前**:
```java
return Jwts.builder()
        .setClaims(claims)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + expiration))
        .signWith(getSigningKey(), SignatureAlgorithm.HS256)
        .compact();
```

**修复后**:
```java
return Jwts.builder()
        .claims(claims)
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + expiration))
        .signWith(getSigningKey())
        .compact();
```

## API变化总结

| 旧API | 新API | 说明 |
|-------|-------|------|
| `Jwts.parserBuilder()` | `Jwts.parser()` | 解析器构建方式简化 |
| `.setSigningKey()` | `.verifyWith()` | 验证方式更明确 |
| `.parseClaimsJws().getBody()` | `.parseSignedClaims().getPayload()` | 解析结果获取方式更新 |
| `.setClaims()` | `.claims()` | 设置声明方式简化 |
| `.setIssuedAt()` | `.issuedAt()` | 设置签发时间方式简化 |
| `.setExpiration()` | `.expiration()` | 设置过期时间方式简化 |
| `.signWith(key, SignatureAlgorithm.HS256)` | `.signWith(key)` | 算法自动推断，无需显式指定 |

## 版本兼容性

- **JJWT 0.11.x**: 使用旧API
- **JJWT 0.12.x**: 使用新API（当前版本）

## 优势

1. **API简化**: 新API更加简洁明了
2. **自动算法推断**: 无需显式指定签名算法
3. **更好的类型安全**: 新API提供更好的类型检查
4. **性能优化**: 新版本在性能方面有所提升

## 注意事项

1. **向后不兼容**: JJWT 0.12.x与0.11.x不兼容
2. **依赖更新**: 确保所有相关依赖都更新到兼容版本
3. **测试验证**: 更新后需要充分测试JWT功能

## 相关文件

- `service/service-auth/src/main/java/com/origin/auth/util/JwtUtil.java`
- `pom.xml` (JJWT版本配置) 
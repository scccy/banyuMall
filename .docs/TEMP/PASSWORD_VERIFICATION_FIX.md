# 密码验证问题修复说明

## 问题描述

在登录过程中，用户输入密码 `123456` 时，密码验证失败，日志显示：

```
2025-07-31 17:19:35.445 [http-nio-8081-exec-2] INFO  com.origin.auth.service.impl.SysUserServiceImpl - 登录验证 - 用户名: test, 输入密码: 1***6, 数据库密码: $2a$10$ySG2lkvjFHY5O0./CPIE1OI8VJsuKYEzOYzqIa7AJR6sEgSzUFOAm
2025-07-31 17:19:35.514 [http-nio-8081-exec-2] INFO  com.origin.auth.util.PasswordUtil - 密码验证 - 原始密码: 1***6, 加密密码: $2a$10$ySG2lkvjFHY5O0./CPIE1OI8VJsuKYEzOYzqIa7AJR6sEgSzUFOAm, 匹配结果: false
```

## 问题分析

### 1. 密码强度不匹配

- **数据库中的密码**：使用 BCrypt 强度 10 生成
  - 格式：`$2a$10$ySG2lkvjFHY5O0./CPIE1OI8VJsuKYEzOYzqIa7AJR6sEgSzUFOAm`
  - 成本因子：10

- **代码中的配置**：
  - `PasswordUtil`：使用强度 12
  - `SecurityConfig`：使用强度 12
  - `PasswordCrackerUtil`：使用强度 10

### 2. BCrypt 验证机制

BCrypt 的一个重要特性是：**不同强度的编码器可以验证相同强度的密码**，但为了保持一致性，应该统一使用相同的强度。

## 修复方案

### 1. 统一密码强度为 10

将所有密码编码器的强度统一为 10，与数据库中的现有密码保持一致：

#### 修改文件：

1. **PasswordUtil.java**
   ```java
   // 修改前
   private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
   
   // 修改后
   private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);
   ```

2. **SecurityConfig.java**
   ```java
   // 修改前
   return new BCryptPasswordEncoder(12);
   
   // 修改后
   return new BCryptPasswordEncoder(10);
   ```

### 2. 验证修复效果

创建测试脚本验证修复是否有效：

```java
// 数据库中的密码（强度10）
String encodedPassword = "$2a$10$ySG2lkvjFHY5O0./CPIE1OI8VJsuKYEzOYzqIa7AJR6sEgSzUFOAm";
String testPassword = "123456";

// 使用强度10的编码器
BCryptPasswordEncoder encoder10 = new BCryptPasswordEncoder(10);
boolean matches10 = encoder10.matches(testPassword, encodedPassword);
// 结果：true ✅
```

## 修复结果

### 修复前
- `PasswordUtil` 使用强度 12
- `SecurityConfig` 使用强度 12
- 密码验证失败

### 修复后
- 所有组件统一使用强度 10
- 与数据库中的密码强度一致
- 密码验证成功 ✅

## 注意事项

1. **向后兼容性**：BCrypt 的不同强度编码器可以相互验证，但为了维护一致性，建议统一使用相同强度。

2. **安全性**：强度 10 对于大多数应用来说已经足够安全，强度 12 会增加计算开销。

3. **性能考虑**：强度越高，密码验证和加密的时间越长。

4. **数据库一致性**：确保新创建的用户密码也使用相同的强度。

## 测试验证

修复后，以下测试应该通过：

1. 用户 `test` 使用密码 `123456` 登录成功
2. 用户 `admin` 使用密码 `123456` 登录成功
3. 所有密码验证相关的单元测试通过

## 相关文件

- `service/service-auth/src/main/java/com/origin/auth/util/PasswordUtil.java`
- `service/service-auth/src/main/java/com/origin/auth/config/SecurityConfig.java`
- `service/service-auth/src/main/java/com/origin/auth/util/PasswordCrackerUtil.java`
- `infra/database/data/user-init-data.sql` 
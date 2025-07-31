# JWT Token生成详解

## JWT Token的组成部分

JWT (JSON Web Token) 由三部分组成，用点号分隔：
```
Header.Payload.Signature
```

### 1. Header（头部）
```json
{
  "alg": "HS256",
  "typ": "JWT"
}
```

### 2. Payload（载荷）
包含用户信息和声明：
```json
{
  "userId": "2",
  "username": "test",
  "roles": "[\"ROLE_USER\"]",
  "permissions": "[\"task:list:view\"]",
  "iat": 1753954732,
  "exp": 1753958332
}
```

### 3. Signature（签名）
使用密钥对Header和Payload进行签名，确保数据完整性。

## 生成过程详解

### 1. 数据准备阶段

#### 1.1 用户信息收集
```java
// 从数据库获取用户信息
SysUser user = getByUsername(loginRequest.getUsername());

// 查询用户角色
List<String> roles = sysRoleService.getRoleCodesByUserId(user.getId());

// 查询用户权限
List<String> permissions = sysPermissionService.getPermissionCodesByUserId(user.getId());
```

#### 1.2 构建Claims（声明）
```java
Map<String, Object> claims = new HashMap<>();
claims.put("userId", userId);           // 用户ID
claims.put("username", username);       // 用户名
claims.put("roles", JSON.toJSONString(roles));           // 角色列表（JSON字符串）
claims.put("permissions", JSON.toJSONString(permissions)); // 权限列表（JSON字符串）
```

### 2. Token生成阶段

#### 2.1 调用生成方法
```java
String token = jwtUtil.generateToken(user.getId(), user.getUsername(), roles, permissions);
```

#### 2.2 内部生成逻辑
```java
private String createToken(Map<String, Object> claims) {
    return Jwts.builder()
            .claims(claims)                                    // 设置声明
            .issuedAt(new Date())                             // 签发时间
            .expiration(new Date(System.currentTimeMillis() + expiration)) // 过期时间
            .signWith(getSigningKey())                        // 签名
            .compact();                                       // 生成最终token
}
```

### 3. 签名密钥生成

#### 3.1 密钥配置
```yaml
jwt:
  secret: ${JWT_SECRET:your-secret-key-here-should-be-at-least-32-bytes-long}
```

#### 3.2 密钥处理
```java
private SecretKey getSigningKey() {
    return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
}
```

## 生成的数据来源

### 1. 用户基本信息
- **用户ID**: 来自数据库 `sys_user.id`
- **用户名**: 来自数据库 `sys_user.username`

### 2. 角色信息
- **来源**: `sys_role` 表
- **查询**: `sysRoleService.getRoleCodesByUserId(userId)`
- **示例**: `["ROLE_USER", "ROLE_ADMIN"]`

### 3. 权限信息
- **来源**: `sys_permission` 表
- **查询**: `sysPermissionService.getPermissionCodesByUserId(userId)`
- **示例**: `["task:list:view", "user:edit"]`

### 4. 时间信息
- **签发时间 (iat)**: 当前时间
- **过期时间 (exp)**: 当前时间 + 配置的过期时间

### 5. 配置信息
- **密钥**: 从配置文件或环境变量获取
- **过期时间**: 从配置文件获取（默认1小时）

## 完整的生成流程

```mermaid
graph TD
    A[用户登录] --> B[验证用户名密码]
    B --> C[查询用户信息]
    C --> D[查询用户角色]
    D --> E[查询用户权限]
    E --> F[构建Claims]
    F --> G[生成JWT Token]
    G --> H[存储到Redis]
    H --> I[返回Token给用户]
```

## 实际示例

### 1. 输入数据
```java
userId = "2"
username = "test"
roles = ["ROLE_USER"]
permissions = ["task:list:view"]
```

### 2. 生成的Claims
```json
{
  "userId": "2",
  "username": "test",
  "roles": "[\"ROLE_USER\"]",
  "permissions": "[\"task:list:view\"]",
  "iat": 1753954732,
  "exp": 1753958332
}
```

### 3. 最终Token
```
eyJhbGciOiJIUzI1NiJ9.eyJwZXJtaXNzaW9ucyI6IltcInRhc2s6bGlzdDp2aWV3XCJdIiwiciJbXCJST0xFX1VTRVJcIl0iLCJ1c2VySWQiOiIyIiwidXNlcm5hbWUiOiJ0ZXN0IiwiaWF0IjoxNzUzOTU0NzMyLCJleHAiOjE3NTM5NTgzMzJ9.ZsihDo1hZAARmYpB5boXDbNOiNPK9-1LObFS16oLKF8
```

## 安全性考虑

### 1. 密钥安全
- **长度**: 至少32字节
- **存储**: 使用环境变量
- **轮换**: 定期更换密钥

### 2. 过期时间
- **短期**: 1小时（访问token）
- **长期**: 24小时（刷新token）

### 3. 信息保护
- **敏感信息**: 不在token中存储密码等敏感信息
- **最小化**: 只包含必要的用户信息

## 验证和解析

### 1. 验证Token
```java
public boolean validateToken(String token) {
    try {
        Claims claims = getClaimsFromToken(token);
        Date expiration = claims.getExpiration();
        return !expiration.before(new Date());
    } catch (Exception e) {
        return false;
    }
}
```

### 2. 解析用户信息
```java
public String getUserIdFromToken(String token) {
    Claims claims = getClaimsFromToken(token);
    return claims.get("userId", String.class);
}
```

## 总结

JWT Token的生成基于：

1. **用户认证信息**: 用户名、密码验证
2. **用户基本信息**: 用户ID、用户名
3. **权限信息**: 角色、权限列表
4. **时间信息**: 签发时间、过期时间
5. **安全配置**: 密钥、算法

整个过程确保了token的唯一性、安全性和完整性！ 
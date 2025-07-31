# Redis Token存储问题分析

## 问题描述

用户多次登录后，Redis中存储了多个相同的token值，这不符合规范。

## 问题分析

### 1. 当前实现逻辑

#### 1.1 登录时的存储逻辑
```java
// 在SysUserServiceImpl.login()方法中
String token = jwtUtil.generateToken(user.getId(), user.getUsername(), roles, permissions);
// 每次登录都会存储新的token到Redis
tokenBlacklistUtil.markAsValid(token, jwtExpiration / 1000);
```

#### 1.2 TokenBlacklistUtil.markAsValid()实现
```java
public void markAsValid(String token, long expirationTime) {
    String key = validTokenPrefix + token;
    // 每次都设置新的key-value对
    redisTemplate.opsForValue().set(key, "1", expirationTime, TimeUnit.SECONDS);
}
```

### 2. 问题根源

#### 2.1 设计问题
- **每次登录都生成新token**: 每次登录都会生成新的JWT token
- **每次都存储到Redis**: 每个新token都会被存储到Redis
- **没有清理旧token**: 旧的token记录没有被清理

#### 2.2 具体问题
1. **重复存储**: 同一用户多次登录会产生多个token记录
2. **内存浪费**: Redis中存储了大量重复或过期的token
3. **管理复杂**: 难以管理和清理token记录

## 解决方案

### 方案1: 基于用户ID的token管理（推荐）

#### 1.1 修改存储策略
```java
// 使用用户ID作为key，而不是token
public void markUserTokenAsValid(String userId, String token, long expirationTime) {
    String key = validTokenPrefix + "user:" + userId;
    // 存储用户ID和token的映射关系
    redisTemplate.opsForValue().set(key, token, expirationTime, TimeUnit.SECONDS);
}
```

#### 1.2 登录时更新逻辑
```java
// 在登录时，先清理旧token，再存储新token
public void updateUserToken(String userId, String newToken, long expirationTime) {
    String key = validTokenPrefix + "user:" + userId;
    // 原子操作：删除旧token，设置新token
    redisTemplate.opsForValue().set(key, newToken, expirationTime, TimeUnit.SECONDS);
}
```

### 方案2: Token去重存储

#### 2.1 使用Set数据结构
```java
// 使用Redis Set存储有效token
public void markAsValid(String token, long expirationTime) {
    String key = validTokenPrefix + "tokens";
    // 添加到Set中，自动去重
    redisTemplate.opsForSet().add(key, token);
    // 设置过期时间
    redisTemplate.expire(key, expirationTime, TimeUnit.SECONDS);
}
```

#### 2.2 检查token有效性
```java
public boolean isValid(String token) {
    String key = validTokenPrefix + "tokens";
    return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, token));
}
```

### 方案3: 基于会话的token管理

#### 3.1 会话管理
```java
// 为每个用户维护一个活跃会话列表
public void addUserSession(String userId, String token, long expirationTime) {
    String sessionKey = validTokenPrefix + "user:" + userId + ":sessions";
    // 使用Set存储用户的所有活跃会话
    redisTemplate.opsForSet().add(sessionKey, token);
    redisTemplate.expire(sessionKey, expirationTime, TimeUnit.SECONDS);
}
```

#### 3.2 限制会话数量
```java
// 限制每个用户的最大会话数量
public void addUserSessionWithLimit(String userId, String token, long expirationTime, int maxSessions) {
    String sessionKey = validTokenPrefix + "user:" + userId + ":sessions";
    Set<String> sessions = redisTemplate.opsForSet().members(sessionKey);
    
    if (sessions != null && sessions.size() >= maxSessions) {
        // 移除最旧的会话
        String oldestSession = sessions.iterator().next();
        redisTemplate.opsForSet().remove(sessionKey, oldestSession);
    }
    
    // 添加新会话
    redisTemplate.opsForSet().add(sessionKey, token);
    redisTemplate.expire(sessionKey, expirationTime, TimeUnit.SECONDS);
}
```

## 推荐实现

### 1. 修改TokenBlacklistUtil类

```java
@Slf4j
@Component
@RequiredArgsConstructor
public class TokenBlacklistUtil {

    private final StringRedisTemplate redisTemplate;

    @Value("${jwt.enable-blacklist}")
    private Boolean enableBlacklist;

    @Value("${jwt.blacklist-prefix}")
    private String blacklistPrefix;

    @Value("${jwt.valid-token-prefix}")
    private String validTokenPrefix;

    @Value("${jwt.max-sessions-per-user:5}")
    private int maxSessionsPerUser;

    /**
     * 更新用户token（推荐方案）
     */
    public void updateUserToken(String userId, String token, long expirationTime) {
        if (!enableBlacklist) {
            return;
        }
        
        String key = validTokenPrefix + "user:" + userId;
        // 直接覆盖旧token
        redisTemplate.opsForValue().set(key, token, expirationTime, TimeUnit.SECONDS);
        log.debug("用户 {} 的token已更新", userId);
    }

    /**
     * 检查用户token是否有效
     */
    public boolean isUserTokenValid(String userId, String token) {
        if (!enableBlacklist) {
            return true;
        }
        
        String key = validTokenPrefix + "user:" + userId;
        String storedToken = redisTemplate.opsForValue().get(key);
        return token.equals(storedToken);
    }

    /**
     * 添加用户会话（支持多会话）
     */
    public void addUserSession(String userId, String token, long expirationTime) {
        if (!enableBlacklist) {
            return;
        }
        
        String sessionKey = validTokenPrefix + "user:" + userId + ":sessions";
        
        // 限制会话数量
        Set<String> sessions = redisTemplate.opsForSet().members(sessionKey);
        if (sessions != null && sessions.size() >= maxSessionsPerUser) {
            // 移除最旧的会话
            String oldestSession = sessions.iterator().next();
            redisTemplate.opsForSet().remove(sessionKey, oldestSession);
            log.debug("用户 {} 的旧会话已移除", userId);
        }
        
        // 添加新会话
        redisTemplate.opsForSet().add(sessionKey, token);
        redisTemplate.expire(sessionKey, expirationTime, TimeUnit.SECONDS);
        log.debug("用户 {} 的新会话已添加", userId);
    }

    /**
     * 检查token是否在用户会话中
     */
    public boolean isTokenInUserSessions(String userId, String token) {
        if (!enableBlacklist) {
            return true;
        }
        
        String sessionKey = validTokenPrefix + "user:" + userId + ":sessions";
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(sessionKey, token));
    }

    // ... 其他方法保持不变
}
```

### 2. 修改登录逻辑

```java
// 在SysUserServiceImpl.login()方法中
String token = jwtUtil.generateToken(user.getId(), user.getUsername(), roles, permissions);

// 方案1: 单会话模式（推荐）
tokenBlacklistUtil.updateUserToken(user.getId(), token, jwtExpiration / 1000);

// 或者方案2: 多会话模式
// tokenBlacklistUtil.addUserSession(user.getId(), token, jwtExpiration / 1000);
```

## 配置更新

### 1. 添加配置项
```yaml
jwt:
  # 每个用户最大会话数量
  max-sessions-per-user: 5
  # 是否启用多会话模式
  enable-multi-session: false
```

### 2. 环境变量支持
```yaml
jwt:
  max-sessions-per-user: ${JWT_MAX_SESSIONS_PER_USER:5}
  enable-multi-session: ${JWT_ENABLE_MULTI_SESSION:false}
```

## 优势

### 1. 存储优化
- **减少重复存储**: 每个用户只存储一个token或有限数量的token
- **内存节省**: 显著减少Redis内存占用
- **自动清理**: 利用Redis的过期机制自动清理

### 2. 管理简化
- **用户级管理**: 基于用户ID管理token
- **会话控制**: 可以限制每个用户的会话数量
- **易于监控**: 便于统计和管理用户会话

### 3. 安全性提升
- **强制单会话**: 可以强制用户只能有一个活跃会话
- **会话控制**: 可以主动清理用户会话
- **审计支持**: 便于审计用户登录行为

## 实施建议

1. **立即实施**: 建议立即修改TokenBlacklistUtil类
2. **渐进迁移**: 可以先实现单会话模式，再考虑多会话模式
3. **配置灵活**: 通过配置控制是否启用多会话
4. **监控告警**: 添加Redis存储监控和告警 
# 登录验证业务逻辑流程图

## 概述
本文档描述了半语积分商城项目的登录验证业务逻辑流程，包括登录、验证、登出等核心流程。

## 核心流程图

### 1. 用户登录流程

```mermaid
flowchart TD
    A[客户端发送登录请求] --> B[接收登录参数]
    B --> C{参数验证}
    C -->|参数无效| D[返回参数错误]
    C -->|参数有效| E[根据用户名查询用户]
    E --> F{用户是否存在}
    F -->|不存在| G[返回用户名或密码错误]
    F -->|存在| H[BCrypt验证密码]
    H --> I{密码是否正确}
    I -->|不正确| G
    I -->|正确| J{用户状态是否正常}
    J -->|禁用| K[返回账号已被禁用]
    J -->|正常| L[更新最后登录时间]
    L --> M[生成JWT令牌]
    M --> N[查询用户角色]
    N --> O[查询用户权限]
    O --> P[构建登录响应]
    P --> Q[返回登录成功]
```

### 2. 请求验证流程

```mermaid
flowchart TD
    A[接收API请求] --> B[提取JWT令牌]
    B --> C{令牌是否存在}
    C -->|不存在| D[返回未提供认证令牌]
    C -->|存在| E[检查令牌黑名单]
    E --> F{是否在黑名单}
    F -->|在黑名单| G[返回认证令牌已失效]
    F -->|不在黑名单| H[验证JWT令牌]
    H --> I{令牌是否有效}
    I -->|无效或过期| J[返回认证令牌无效或已过期]
    I -->|有效| K[提取用户信息]
    K --> L[设置请求属性]
    L --> M[继续处理请求]
```

### 3. 用户登出流程

```mermaid
flowchart TD
    A[接收登出请求] --> B[提取JWT令牌]
    B --> C{令牌是否存在}
    C -->|不存在| D[返回登出成功]
    C -->|存在| E[获取令牌剩余过期时间]
    E --> F[将令牌加入黑名单]
    F --> G[从有效令牌列表移除]
    G --> H[返回登出成功]
```

## 详细业务逻辑

### 1. 登录业务逻辑

#### 1.1 参数验证
```java
// 验证登录参数
if (StringUtils.isEmpty(loginRequest.getUsername()) || 
    StringUtils.isEmpty(loginRequest.getPassword())) {
    throw new BusinessException(ErrorCode.PARAM_ERROR, "用户名和密码不能为空");
}
```

#### 1.2 用户查询
```java
// 根据用户名查询用户
SysUser user = getByUsername(loginRequest.getUsername());
if (user == null) {
    throw new BusinessException(ErrorCode.PARAM_ERROR, "用户名或密码错误");
}
```

#### 1.3 密码验证
```java
// 使用BCrypt验证密码
if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
    throw new BusinessException(ErrorCode.PARAM_ERROR, "用户名或密码错误");
}
```

#### 1.4 状态检查
```java
// 检查用户状态
if (user.getStatus() != 1) {
    throw new BusinessException(ErrorCode.FORBIDDEN, "账号已被禁用");
}
```

#### 1.5 JWT令牌生成
```java
// 生成JWT令牌
String token = JwtUtil.generateToken(String.valueOf(user.getId()), user.getUsername());

// 将令牌标记为有效并存储到Redis
TokenBlacklistUtil.markAsValid(token, jwtExpiration / 1000);
```

#### 1.6 权限查询
```java
// 查询用户角色
List<String> roles = sysRoleService.getRoleCodesByUserId(user.getId());

// 查询用户权限
List<String> permissions = sysPermissionService.getPermissionCodesByUserId(user.getId());
```

### 2. 请求验证业务逻辑

#### 2.1 令牌提取
```java
// 从请求头中获取token
String token = request.getHeader(tokenHeader);

// 如果请求头中没有token，则尝试从请求参数中获取
if (!StringUtils.hasText(token)) {
    token = request.getParameter("token");
}
```

#### 2.2 黑名单检查
```java
// 检查token是否在黑名单中
if (tokenBlacklistUtil.isBlacklisted(token)) {
    throw new BusinessException(ErrorCode.UNAUTHORIZED, "认证令牌已失效");
}
```

#### 2.3 JWT验证
```java
// 验证token是否有效
if (!jwtUtil.validateToken(token)) {
    throw new BusinessException(ErrorCode.UNAUTHORIZED, "认证令牌无效或已过期");
}
```

#### 2.4 用户信息设置
```java
// 从token中获取用户ID和用户名，并设置到请求属性中
String userId = jwtUtil.getUserIdFromToken(token);
String username = jwtUtil.getUsernameFromToken(token);

request.setAttribute("userId", userId);
request.setAttribute("username", username);
```

### 3. 登出业务逻辑

#### 3.1 令牌处理
```java
// 从请求头中获取token
String token = request.getHeader("Authorization");

if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
    token = token.substring(7);
    
    // 获取token的剩余过期时间
    long expirationTime = JwtUtil.getExpirationTime(token);
    if (expirationTime > 0) {
        TokenBlacklistUtil.addToBlacklist(token, expirationTime);
    }
    
    // 从有效token列表中移除
    TokenBlacklistUtil.removeFromValid(token);
}
```

## 数据流转图

### 1. 登录数据流转

```mermaid
sequenceDiagram
    participant Client as 客户端
    participant Controller as 控制器
    participant Service as 服务层
    participant Mapper as 数据访问层
    participant DB as 数据库
    participant Redis as Redis缓存
    participant JWT as JWT工具

    Client->>Controller: 登录请求
    Controller->>Service: 调用登录服务
    Service->>Mapper: 查询用户信息
    Mapper->>DB: 执行SQL查询
    DB-->>Mapper: 返回用户数据
    Mapper-->>Service: 返回用户对象
    Service->>Service: 验证密码和状态
    Service->>DB: 更新登录时间
    Service->>JWT: 生成JWT令牌
    Service->>Mapper: 查询用户角色
    Service->>Mapper: 查询用户权限
    Service->>Redis: 存储有效令牌
    Service-->>Controller: 返回登录响应
    Controller-->>Client: 返回登录结果
```

### 2. 请求验证数据流转

```mermaid
sequenceDiagram
    participant Client as 客户端
    participant Interceptor as 拦截器
    participant Redis as Redis缓存
    participant JWT as JWT工具

    Client->>Interceptor: 携带令牌的请求
    Interceptor->>Interceptor: 提取JWT令牌
    Interceptor->>Redis: 检查黑名单
    Redis-->>Interceptor: 返回检查结果
    alt 令牌在黑名单
        Interceptor-->>Client: 返回令牌失效错误
    else 令牌不在黑名单
        Interceptor->>JWT: 验证JWT令牌
        JWT-->>Interceptor: 返回验证结果
        alt 令牌无效
            Interceptor-->>Client: 返回令牌无效错误
        else 令牌有效
            Interceptor->>Interceptor: 提取用户信息
            Interceptor-->>Client: 继续处理请求
        end
    end
```

## 错误处理流程

### 1. 登录错误处理

```mermaid
flowchart TD
    A[登录请求] --> B{参数验证}
    B -->|失败| C[返回参数错误]
    B -->|成功| D{用户查询}
    D -->|失败| E[返回用户名或密码错误]
    D -->|成功| F{密码验证}
    F -->|失败| E
    F -->|成功| G{状态检查}
    G -->|失败| H[返回账号禁用错误]
    G -->|成功| I[登录成功]
```

### 2. 认证错误处理

```mermaid
flowchart TD
    A[API请求] --> B{令牌提取}
    B -->|失败| C[返回未提供令牌错误]
    B -->|成功| D{黑名单检查}
    D -->|在黑名单| E[返回令牌失效错误]
    D -->|不在黑名单| F{JWT验证}
    F -->|失败| G[返回令牌无效错误]
    F -->|成功| H[验证通过]
```

## 性能优化策略

### 1. 数据库优化
- 用户名唯一索引：`uk_username`
- 用户状态索引：`idx_status`
- 角色编码唯一索引：`uk_code`
- 权限编码唯一索引：`uk_code`

### 2. 缓存优化
- Redis缓存用户权限信息
- 令牌黑名单自动过期
- 有效令牌列表管理

### 3. 查询优化
- 一次性查询用户角色和权限
- 使用MyBatis-Plus优化查询
- 避免N+1查询问题

## 安全机制

### 1. 密码安全
- BCrypt加密存储
- 密码错误统一提示
- 防止用户枚举攻击

### 2. 令牌安全
- JWT签名验证
- 令牌过期时间控制
- 黑名单机制

### 3. 权限控制
- RBAC权限模型
- 细粒度权限控制
- 动态权限验证

## 版本历史
- 2025-07-30: 创建登录验证业务逻辑流程图文档 
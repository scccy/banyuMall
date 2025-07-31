# 认证模块设计文档

## 1. 模块概述

认证模块是BanyuMall系统的核心安全模块，负责用户身份认证、JWT令牌管理和会话控制。该模块提供统一的认证服务，支持多服务间的单点登录，确保系统安全性和用户体验。

**核心职责**:
- 用户登录认证和身份验证
- JWT令牌的生成、验证和管理
- 用户会话控制和登出管理
- 安全策略实施

## 2. 数据模型设计

### 2.1 数据库表结构
参考: [user-schema.sql](../database/data/user/user-schema.sql)

### 2.2 实体类设计

#### 2.2.1 SysUser实体类
```java
@Data
@TableName("sys_user")
public class SysUser extends BaseEntity {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    @TableField("username")
    private String username;
    
    @TableField("password")
    private String password;
    
    @TableField("nickname")
    private String nickname;
    
    @TableField("avatar")
    private String avatar;
    
    @TableField("email")
    private String email;
    
    @TableField("phone")
    private String phone;
    
    @TableField("gender")
    private Integer gender;
    
    @TableField("birthday")
    private LocalDate birthday;
    
    @TableField("status")
    private Integer status;
    
    @TableField("user_type")
    private Integer userType;
    
    @TableField("last_login_time")
    private LocalDateTime lastLoginTime;
}
```

#### 2.2.2 DTO类设计
```java
// 登录请求DTO
@Data
public class LoginRequest {
    @NotBlank(message = "用户名不能为空")
    private String username;
    
    @NotBlank(message = "密码不能为空")
    private String password;
}

// 登录响应DTO
@Data
public class LoginResponse {
    private String userId;
    private String username;
    private String nickname;
    private String avatar;
    private String token;
    private String tokenType = "Bearer";
    private Long expiresIn;
}
```

## 3. 接口设计

### 3.1 认证管理接口

#### 3.1.1 用户登录
- **接口路径**: `POST /auth/login`
- **请求头**: `Content-Type: application/json`
- **请求参数**:
  ```json
  {
    "username": "string",        // 用户名（必填）
    "password": "string"         // 密码（必填）
  }
  ```
- **响应结果**:
  ```json
  {
    "code": 200,
    "message": "登录成功",
    "data": {
      "userId": "string",
      "username": "string",
      "nickname": "string",
      "avatar": "string",
      "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
      "tokenType": "Bearer",
      "expiresIn": 3600
    }
  }
  ```
- **错误码**:
  - `400`: 参数验证失败
  - `401`: 用户名或密码错误
  - `403`: 账号已被禁用
  - `500`: 服务器内部错误
- **业务规则**:
  - 用户名和密码必填
  - 密码使用BCrypt加密验证
  - 用户状态必须为正常(1)
  - 登录成功后更新最后登录时间
  - 生成JWT令牌包含用户信息

#### 3.1.2 用户登出
- **接口路径**: `POST /auth/logout`
- **请求头**: `Authorization: Bearer {token}`
- **响应结果**:
  ```json
  {
    "code": 200,
    "message": "登出成功",
    "data": "登出成功"
  }
  ```
- **错误码**:
  - `400`: 缺少有效的Authorization头
  - `401`: 无效的token
  - `500`: 服务器内部错误
- **业务规则**:
  - 需要有效的JWT令牌
  - 将token加入黑名单
  - 清除用户会话信息
  - 记录登出日志

#### 3.1.3 强制登出用户
- **接口路径**: `POST /auth/logout/force/{userId}`
- **请求头**: `Authorization: Bearer {token}`
- **路径参数**:
  ```
  userId: string  // 用户ID
  ```
- **响应结果**:
  ```json
  {
    "code": 200,
    "message": "强制登出成功",
    "data": "强制登出成功"
  }
  ```
- **错误码**:
  - `401`: 无权限操作
  - `404`: 用户不存在
  - `500`: 服务器内部错误
- **业务规则**:
  - 需要管理员权限
  - 强制清除用户所有会话
  - 记录管理员操作日志

#### 3.1.4 认证服务健康检查
- **接口路径**: `GET /auth/test`
- **响应结果**:
  ```json
  {
    "code": 200,
    "message": "认证服务正常运行",
    "data": "Auth Service is running!"
  }
  ```
- **业务规则**:
  - 用于服务健康检查
  - 记录请求追踪信息

## 4. 功能特性

### 4.1 认证功能
- ✅ **用户登录认证**
  - 支持用户名密码登录
  - BCrypt密码加密验证
  - 用户状态验证
  - 登录失败次数限制
  
- ✅ **JWT令牌管理**
  - JWT令牌生成和验证
  - 令牌过期时间管理
  - 令牌黑名单机制
  - 令牌刷新机制
  
- ✅ **会话控制**
  - 用户登出功能
  - 强制登出功能
  - 会话状态管理
  - 多设备登录控制

### 4.2 安全特性
- ✅ **认证授权**
  - JWT Token验证
  - 用户权限验证
  - 数据访问控制
  - 跨服务认证
  
- ✅ **安全防护**
  - 密码加密存储
  - 敏感信息脱敏
  - 输入参数验证
  - SQL注入防护
  - XSS攻击防护

## 5. 技术实现

### 5.1 技术栈
- **框架**: Spring Boot 3.2.x
- **数据库**: MySQL 8.0
- **ORM**: MyBatis-Plus 3.5.x
- **缓存**: Redis 7.x
- **认证**: JWT Token
- **安全**: Spring Security 6.x
- **文档**: Knife4j (Swagger 3)
- **验证**: Jakarta Validation
- **日志**: Log4j2
- **服务发现**: Nacos

### 5.2 核心配置
```yaml
# application.yml
spring:
  application:
    name: service-auth
  datasource:
    url: jdbc:mysql://localhost:3306/banyu_mall
    username: root
    password: password
  redis:
    host: localhost
    port: 6379
    database: 1
  cloud:
    nacos:
      discovery:
        server-addr: 117.50.197.170:8848
      config:
        server-addr: 117.50.197.170:8848

server:
  port: 8081

# JWT配置
jwt:
  secret: your-secret-key
  expiration: 3600
  token-prefix: Bearer
```

### 5.3 缓存策略
- **用户信息缓存**: Redis Key: `auth:user:{userId}`, TTL: 30分钟
- **令牌黑名单**: Redis Key: `auth:blacklist:{token}`, TTL: 根据令牌过期时间
- **缓存更新策略**: 登录时更新缓存，登出时清除缓存

### 5.4 项目结构
```
service-auth/
├── src/main/java/com/origin/auth/
│   ├── controller/          # 控制器层
│   ├── service/            # 服务层
│   ├── mapper/             # 数据访问层
│   ├── entity/             # 实体类
│   ├── dto/                # 数据传输对象
│   ├── feign/              # Feign客户端
│   ├── config/             # 配置类
│   ├── interceptor/        # 拦截器
│   ├── util/               # 工具类
│   └── exception/          # 异常处理
└── src/main/resources/
    ├── dev/                # 开发环境配置
    ├── prod/               # 生产环境配置
    ├── test/               # 测试环境配置
    └── log4j2.xml          # 日志配置
```

### 5.5 依赖关系
- **service-base**: spring boot 配置类
- **service-common**: 公共组件和工具类
- **service-user**: 用户信息服务（通过Feign调用）
- **service-gateway**: 网关服务（路由转发）

## 6. 部署配置

### 6.1 应用配置
```yaml
# application-dev.yml
spring:
  profiles:
    active: dev
  datasource:
    url: jdbc:mysql://localhost:3306/banyu_mall_dev
  redis:
    host: localhost
    port: 6379
    database: 1
  security:
    user:
      name: admin
      password: admin123
      roles: ADMIN

# application-prod.yml
spring:
  profiles:
    active: prod
  datasource:
    url: jdbc:mysql://prod-mysql:3306/banyu_mall
  redis:
    host: prod-redis
    port: 6379
    database: 1
```

### 6.2 网关路由配置
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: auth-service
          uri: lb://service-auth
          predicates:
            - Path=/auth/**
          filters:
            - StripPrefix=0
            - name: RequestRateLimiter
              args:
                redis-rate-limiter.replenishRate: 5
                redis-rate-limiter.burstCapacity: 10
```

### 6.3 Docker配置
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/service-auth.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

## 7. 测试用例

### 7.1 单元测试
- **AuthController测试**
  - 用户登录成功测试
  - 用户登录失败测试
  - 用户登出测试
  - 强制登出测试
  - 健康检查测试
  
- **SysUserService测试**
  - 用户认证测试
  - 密码验证测试
  - 用户状态验证测试
  - JWT令牌生成测试

### 7.2 集成测试
- **登录流程测试**
  - 正常登录流程
  - 异常登录处理
  - 权限验证流程
  - 会话管理测试

### 7.3 API测试
- **Postman测试集合**
  - 所有认证接口测试
  - 错误场景测试
  - 性能压力测试
  - 安全测试

## 8. 监控与日志

### 8.1 日志配置
- 使用Log4j2进行日志管理
- 按服务模块分离日志文件
- 支持日志级别动态调整
- 记录认证操作审计日志

### 8.2 监控指标
- 接口响应时间
- 登录成功率
- 令牌验证次数
- 缓存命中率
- 系统资源使用情况

## 9. 后续优化计划

### 9.1 性能优化
- [ ] JWT令牌缓存优化
- [ ] 数据库连接池优化
- [ ] Redis缓存策略优化

### 9.2 功能增强
- [ ] 多因子认证支持
- [ ] OAuth2.0集成
- [ ] SSO单点登录
- [ ] 登录行为分析

### 9.3 安全加固
- [ ] 密码策略增强
- [ ] 登录失败处理
- [ ] 会话安全控制
- [ ] 审计日志完善 
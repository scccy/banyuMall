> **文档位置**: infra/moudleDocs/{模块名称}/design.md

# Service-Auth 模块设计文档

## 1. 模块概述

### 1.1 模块职责
**Service-Auth** 是BanyuMall微服务架构的认证授权模块，负责用户认证、JWT令牌管理、权限控制、角色管理等核心安全功能，为整个微服务架构提供统一的安全保障。

### 1.2 核心功能
- **用户认证**: 用户名密码登录验证
- **JWT令牌管理**: 生成、验证、刷新JWT令牌
- **权限控制**: 基于角色的访问控制（RBAC）
- **令牌黑名单**: 管理已失效的令牌
- **用户会话管理**: 管理用户登录状态和会话信息
- **安全配置**: Spring Security安全配置
- **链路追踪**: 集成链路追踪和日志记录

### 1.3 技术栈
- **框架**: Spring Boot 3.x + Spring Security
- **认证**: JWT (JSON Web Token)
- **数据库**: MySQL + MyBatis-Plus
- **缓存**: Redis (令牌黑名单)
- **序列化**: FastJSON2
- **验证**: Jakarta Validation
- **日志**: Log4j2
- **文档**: Knife4j (Swagger)

## 2. 数据模型设计

### 2.1 用户实体
**文件**: `SysUser.java`
**职责**: 系统用户信息管理

```java
@Data
@TableName("sys_user")
public class SysUser extends BaseEntity {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;              // 用户ID
    
    @TableField("username")
    private String username;        // 用户名
    
    @TableField("password")
    private String password;        // 密码（加密存储）
    
    @TableField("nickname")
    private String nickname;        // 昵称
    
    @TableField("avatar")
    private String avatar;          // 头像
    
    @TableField("email")
    private String email;           // 邮箱
    
    @TableField("phone")
    private String phone;           // 手机号
    
    @TableField("gender")
    private Integer gender;         // 性别：0-未知，1-男，2-女
    
    @TableField("birthday")
    private LocalDate birthday;     // 生日
    
    @TableField("status")
    private Integer status;         // 状态：0-禁用，1-正常，2-待审核，3-已删除
    
    @TableField("user_type")
    private Integer userType;       // 用户类型：1-最高权限，2-普通发布者
    
    @TableField("last_login_time")
    private LocalDateTime lastLoginTime; // 最后登录时间
}
```

### 2.2 角色实体
**文件**: `SysRole.java`
**职责**: 系统角色信息管理

```java
@Data
@TableName("sys_role")
public class SysRole extends BaseEntity {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;              // 角色ID
    
    @TableField("role_code")
    private String roleCode;        // 角色编码
    
    @TableField("role_name")
    private String roleName;        // 角色名称
    
    @TableField("description")
    private String description;     // 角色描述
    
    @TableField("status")
    private Integer status;         // 状态：0-禁用，1-正常
}
```

### 2.3 权限实体
**文件**: `SysPermission.java`
**职责**: 系统权限信息管理

```java
@Data
@TableName("sys_permission")
public class SysPermission extends BaseEntity {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;              // 权限ID
    
    @TableField("permission_code")
    private String permissionCode;  // 权限编码
    
    @TableField("permission_name")
    private String permissionName;  // 权限名称
    
    @TableField("permission_type")
    private String permissionType;  // 权限类型：menu-菜单，button-按钮，api-接口
    
    @TableField("resource_path")
    private String resourcePath;    // 资源路径
    
    @TableField("description")
    private String description;     // 权限描述
    
    @TableField("status")
    private Integer status;         // 状态：0-禁用，1-正常
}
```

### 2.4 关联实体
- **SysUserRole**: 用户角色关联表
- **SysRolePermission**: 角色权限关联表

## 3. 接口设计

### 3.1 接口功能列表
| 序号 | 接口名称 | 请求方法 | 接口路径 | 功能描述 | 职责对应 | Feign调用 |
|------|----------|----------|----------|----------|----------|-----------|
| 1 | 用户登录 | POST | `/auth/login` | 用户登录认证，返回JWT令牌和用户信息 | 用户认证 | 否 |
| 2 | 用户登出 | POST | `/auth/logout` | 用户登出，清除会话信息和令牌 | 会话管理 | 否 |
| 3 | 强制登出 | POST | `/auth/logout/force/{userId}` | 管理员强制登出指定用户 | 会话管理 | 否 |
| 4 | 健康检查 | GET | `/auth/test` | 认证服务健康检查 | 基础设施 | 否 |

### 3.2 核心接口详情

#### 3.2.1 用户登录
**接口路径**: `POST /auth/login`
**功能描述**: 用户登录认证，返回JWT令牌和用户信息

**请求参数**:
```json
{
  "username": "admin",
  "password": "123456",
  "captcha": "1234",
  "captchaKey": "captcha_key",
  "rememberMe": true
}
```

**响应数据**:
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "userId": "123",
    "username": "admin",
    "nickname": "管理员",
    "avatar": "https://example.com/avatar.jpg",
    "roles": ["ADMIN", "USER"],
    "permissions": ["user:read", "user:write"],
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 3600
  },
  "timestamp": 1703123456789
}
```

#### 3.2.2 用户登出
**接口路径**: `POST /auth/logout`
**功能描述**: 用户登出，清除会话信息和令牌

**请求头**:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**响应数据**:
```json
{
  "code": 200,
  "message": "登出成功",
  "data": null,
  "timestamp": 1703123456789
}
```

## 4. 业务逻辑设计

### 4.1 登录流程
1. **参数验证**: 验证用户名、密码等参数
2. **用户查询**: 根据用户名查询用户信息
3. **密码验证**: 使用BCrypt验证密码
4. **状态检查**: 检查用户状态是否正常
5. **角色权限查询**: 查询用户角色和权限
6. **JWT生成**: 生成包含用户信息的JWT令牌
7. **会话管理**: 将令牌存储到Redis
8. **日志记录**: 记录登录日志和最后登录时间

### 4.2 令牌管理
- **令牌生成**: 使用JJWT库生成JWT令牌
- **令牌验证**: 验证令牌签名和过期时间
- **令牌刷新**: 支持令牌自动刷新
- **令牌黑名单**: 管理已失效的令牌
- **令牌存储**: 使用Redis存储用户令牌

### 4.3 权限控制
- **RBAC模型**: 基于角色的访问控制
- **权限验证**: 验证用户是否有访问权限
- **动态权限**: 支持动态权限配置
- **权限缓存**: 缓存用户权限信息

## 5. 安全配置

### 5.1 Spring Security配置
**文件**: `SecurityConfig.java`
**职责**: 配置Spring Security安全策略

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/login", "/auth/test").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}
```

### 5.2 JWT配置
```yaml
# JWT配置
jwt:
  secret: your-secret-key-here-must-be-at-least-256-bits-long
  expiration: 3600000  # 1小时
  refresh-expiration: 86400000  # 24小时
  token-prefix: "Bearer"
```

### 5.3 密码加密
```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

## 6. 项目结构

```
src/main/java/com/origin/auth/
├── config/           # 配置类
│   ├── SecurityConfig.java
│   └── AuthWebMvcConfig.java
├── controller/       # 控制器
│   └── AuthController.java
├── dto/             # 数据传输对象
│   ├── LoginRequest.java
│   └── LoginResponse.java
├── entity/          # 实体类
│   ├── SysUser.java
│   ├── SysRole.java
│   ├── SysPermission.java
│   ├── SysUserRole.java
│   └── SysRolePermission.java
├── exception/       # 异常处理
│   └── AuthExceptionHandler.java
├── feign/          # Feign客户端
│   ├── AuthFeignClient.java
│   └── AuthFeignClientFallback.java
├── interceptor/    # 拦截器
│   └── JwtInterceptor.java
├── mapper/         # 数据访问层
│   ├── SysUserMapper.java
│   ├── SysRoleMapper.java
│   ├── SysPermissionMapper.java
│   ├── SysUserRoleMapper.java
│   └── SysRolePermissionMapper.java
├── service/        # 业务逻辑层
│   ├── SysUserService.java
│   ├── SysRoleService.java
│   ├── SysPermissionService.java
│   └── impl/
│       ├── SysUserServiceImpl.java
│       ├── SysRoleServiceImpl.java
│       └── SysPermissionServiceImpl.java
├── util/           # 工具类
│   ├── JwtUtil.java
│   ├── PasswordUtil.java
│   └── TokenBlacklistUtil.java
└── ServiceAuthApplication.java
```

## 7. 应用配置

### 7.1 本地配置
```yaml
# application.yml
spring:
  profiles:
    active: dev
  application:
    name: service-auth
```

### 7.2 Nacos配置
```yaml
# nacos-config-template-auth.yml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/banyumall?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: ${MYSQL_USERNAME:root}
    password: ${MYSQL_PASSWORD:123456}
  
  redis:
    host: ${REDIS_HOST:localhost}
    port: ${REDIS_PORT:6379}
    database: 1
    timeout: 3000ms
    lettuce:
      pool:
        max-active: 8
        max-idle: 8
        min-idle: 0
        max-wait: -1ms

# JWT配置
jwt:
  secret: ${JWT_SECRET:your-secret-key-here-must-be-at-least-256-bits-long}
  expiration: ${JWT_EXPIRATION:3600000}
  refresh-expiration: ${JWT_REFRESH_EXPIRATION:86400000}
  token-prefix: ${JWT_TOKEN_PREFIX:Bearer}

# 安全配置
security:
  user:
    name: ${SECURITY_USER_NAME:admin}
    password: ${SECURITY_USER_PASSWORD:admin123}
    roles: ${SECURITY_USER_ROLES:ADMIN}

mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  global-config:
    db-config:
      logic-delete-field: isDeleted
      logic-delete-value: 1
      logic-not-delete-value: 0
```

## 8. 依赖管理

### 8.1 基础依赖包
```xml
<!-- 基础配置包 - Spring Boot公共配置 -->
<dependency>
    <groupId>com.origin</groupId>
    <artifactId>service-base</artifactId>
    <version>${project.version}</version>
</dependency>

<!-- 公共工具包 - 静态方法和工具类 -->
<dependency>
    <groupId>com.origin</groupId>
    <artifactId>service-common</artifactId>
    <version>${project.version}</version>
</dependency>
```

### 8.2 核心依赖
```xml
<dependencies>
    <!-- Spring Boot Starter -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    
    <!-- Spring Security -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    
    <!-- MyBatis Plus -->
    <dependency>
        <groupId>com.baomidou</groupId>
        <artifactId>mybatis-plus-boot-starter</artifactId>
    </dependency>
    
    <!-- Redis -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-redis</artifactId>
    </dependency>
    
    <!-- Validation -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    
    <!-- Knife4j -->
    <dependency>
        <groupId>com.github.xiaoymin</groupId>
        <artifactId>knife4j-openapi3-spring-boot-starter</artifactId>
    </dependency>
    
    <!-- Nacos -->
    <dependency>
        <groupId>com.alibaba.cloud</groupId>
        <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
    </dependency>
    <dependency>
        <groupId>com.alibaba.cloud</groupId>
        <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
    </dependency>
    
    <!-- OpenFeign -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-openfeign</artifactId>
    </dependency>
    
    <!-- JWT -->
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-impl</artifactId>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-jackson</artifactId>
    </dependency>
    
    <!-- FastJSON2 -->
    <dependency>
        <groupId>com.alibaba.fastjson2</groupId>
        <artifactId>fastjson2</artifactId>
    </dependency>
    
    <!-- Log4j2 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-log4j2</artifactId>
    </dependency>
</dependencies>
```

### 8.3 依赖排除
```xml
<!-- 排除默认的Jackson和Logback -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <exclusions>
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-logging</artifactId>
        </exclusion>
        <exclusion>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

## 9. 使用说明

### 9.1 启动配置
```bash
# 开发环境启动
java -jar service-auth.jar --spring.profiles.active=dev

# 生产环境启动
java -jar service-auth.jar --spring.profiles.active=prod
```
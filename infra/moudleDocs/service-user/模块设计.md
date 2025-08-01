# service-user 设计文档

## 1. 模块概述

service-user模块是BanyuMall系统的用户管理模块，负责用户基础信息管理和用户扩展信息管理。

**核心职责**:
1. 满足(管理员)对普通用户进行管理,包括增加/删除 (发布用户) (可批量)，对(发布用户)进行权限管理 修改(发布用户)相关数据(基础信息)
2. 用户(包含管理员、发布用户)可以修改自己的用户信息(基础数据)
3. 管理员和发布用户 微信id和有赞id默认是0

### 1.1 接口功能列表

| 序号 | 接口名称 | 请求方法 | 接口路径 | 功能描述 | 职责对应 | Feign调用 | 详细说明 |
|------|----------|----------|----------|----------|----------|-----------|----------|
| 1 | 创建用户 | POST | `/user` | 创建新用户，支持管理员和发布者两种用户类型 | 满足管理员对普通用户进行管理 | 否 | [查看详情](#31-用户基础信息管理接口) |
| 2 | 获取用户信息 | GET | `/user/{userId}` | 根据用户ID获取用户详细信息 | 用户信息查询和管理 | **是** | [查看详情](#31-用户基础信息管理接口) |
| 3 | 更新用户信息 | PUT | `/user/{userId}` | 更新用户的基础信息（昵称、头像、邮箱等） | 用户(包含管理员、发布用户)可以修改自己的用户信息 | 否 | [查看详情](#31-用户基础信息管理接口) |
| 4 | 删除用户 | DELETE | `/user/{userId}` | 软删除指定用户 | 满足管理员对普通用户进行管理 | 否 | [查看详情](#31-用户基础信息管理接口) |
| 5 | 用户列表查询 | GET | `/user/list` | 分页查询用户列表，支持多条件筛选 | 满足管理员对普通用户进行管理 | **是** | [查看详情](#31-用户基础信息管理接口) |
| 6 | 批量删除用户 | DELETE | `/user/batch` | 批量软删除多个用户 | 满足管理员对普通用户进行管理(可批量) | 否 | [查看详情](#31-用户基础信息管理接口) |
| 7 | 获取用户扩展信息 | GET | `/profile/{userId}` | 获取用户的详细资料和公司信息 | 用户扩展信息管理 | **是** | [查看详情](#32-用户扩展信息管理接口) |
| 8 | 创建或更新用户扩展信息 | POST | `/profile/{userId}` | 创建或更新用户的扩展信息（真实姓名、公司信息等） | 用户扩展信息管理 | 否 | [查看详情](#32-用户扩展信息管理接口) |

### 1.2 技术栈
- **框架**: Spring Boot 3.x
- **数据库**: MySQL 8.0
- **ORM**: MyBatis-Plus
- **缓存**: Redis
- **认证**: JWT + Spring Security
- **文档**: Knife4j (Swagger)
- **配置中心**: Nacos
- **服务发现**: Nacos Discovery
- **服务调用**: OpenFeign
- **JSON处理**: FastJSON2
- **日志框架**: Log4j2

## 2. 数据模型设计

### 2.1 数据库表结构
参考: [user_init_creat.sql](../../database/data/user/user_init_creat.sql)

### 2.2 实体类设计

#### 2.2.1 SysUser实体类
```java
@Data
@TableName("sys_user")
public class SysUser extends BaseEntity {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    @TableField("phone")
    private String phone;
    
    @TableField("wechat_id")
    private String wechatId;
    
    @TableField("youzan_id")
    private String youzanId;
    
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

#### 2.2.2 UserProfile实体类
```java
@Data
@TableName("user_profile")
public class UserProfile extends BaseEntity {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    @TableField("user_id")
    private String userId;
    
    @TableField("real_name")
    private String realName;
    
    @TableField("company_name")
    private String companyName;
    
    @TableField("company_address")
    private String companyAddress;
    
    @TableField("contact_person")
    private String contactPerson;
    
    @TableField("contact_phone")
    private String contactPhone;
    
    @TableField("industry")
    private String industry;
    
    @TableField("description")
    private String description;
}
```

### 2.3 DTO设计

#### 2.3.1 UserCreateRequest
```java
@Data
public class UserCreateRequest {
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    
    @NotBlank(message = "微信ID不能为空")
    private String wechatId;
    
    @NotBlank(message = "有赞ID不能为空")
    private String youzanId;
    
    @NotBlank(message = "用户名不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9_]{4,20}$", message = "用户名只能包含字母、数字、下划线，长度4-20位")
    private String username;
    
    @NotBlank(message = "密码不能为空")
    private String password;
    
    private String nickname;
    private String avatar;
    
    @Email(message = "邮箱格式不正确")
    private String email;
    
    @Min(value = 0, message = "性别值不正确")
    @Max(value = 2, message = "性别值不正确")
    private Integer gender;
    
    private LocalDate birthday;
    
    @NotNull(message = "用户类型不能为空")
    @Min(value = 1, message = "用户类型不正确")
    @Max(value = 2, message = "用户类型不正确")
    private Integer userType;
}
```

#### 2.3.2 UserUpdateRequest
```java
@Data
public class UserUpdateRequest {
    private String nickname;
    private String avatar;
    
    @Email(message = "邮箱格式不正确")
    private String email;
    
    @Min(value = 0, message = "性别值不正确")
    @Max(value = 2, message = "性别值不正确")
    private Integer gender;
    
    private LocalDate birthday;
    
    // 扩展信息字段
    private String realName;
    private String companyName;
    private String companyAddress;
    private String contactPerson;
    
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "联系电话格式不正确")
    private String contactPhone;
    
    private String industry;
    private String description;
}
```

#### 2.3.3 UserQueryRequest
```java
@Data
public class UserQueryRequest {
    private String username;
    private String phone;
    private String email;
    private Integer userType;
    private Integer status;
    
    @Min(value = 1, message = "页码不能小于1")
    private Integer page = 1;
    
    @Min(value = 1, message = "每页大小不能小于1")
    @Max(value = 100, message = "每页大小不能超过100")
    private Integer size = 10;
}
```

## 3. 接口设计

### 3.1 用户基础信息管理接口 {#31-用户基础信息管理接口}

#### 3.1.1 创建用户
- **接口路径**: `POST /user`
- **功能描述**: 创建新用户，支持管理员和发布者两种用户类型
- **Feign调用**: 否
- **请求头**:
  ```
  Content-Type: application/json
  Authorization: Bearer {token}
  ```
- **请求参数**:
  ```json
  {
    "phone": "13800138001",
    "wechatId": "wx_001",
    "youzanId": "yz_001",
    "username": "testuser001",
    "password": "password123",
    "nickname": "测试用户001",
    "email": "test001@example.com",
    "gender": 1,
    "birthday": "1990-01-01",
    "userType": 2
  }
  ```
- **响应示例**:
  ```json
  {
    "code": 200,
    "message": "创建成功",
    "data": {
      "userId": "1234567890abcdef",
      "username": "testuser001",
      "phone": "13800138001",
      "createTime": "2025-01-27T10:00:00"
    }
  }
  ```
- **错误码**:
  - `400`: 参数验证失败
  - `409`: 用户名、手机号、微信ID或有赞ID已存在
  - `403`: 无权限创建用户
  - `500`: 服务器内部错误
- **业务规则**:
  - 需要管理员权限
  - 用户名、手机号、微信ID、有赞ID必须唯一
  - 密码使用BCrypt加密存储
  - 用户类型必须为1或2

#### 3.1.2 获取用户信息
- **接口路径**: `GET /user/{userId}`
- **功能描述**: 根据用户ID获取用户详细信息
- **Feign调用**: 是
- **请求头**:
  ```
  Authorization: Bearer {token}
  ```
- **路径参数**:
  - `userId`: 用户ID
- **响应示例**:
  ```json
  {
    "code": 200,
    "message": "获取成功",
    "data": {
      "id": "1234567890abcdef",
      "phone": "13800138001",
      "wechatId": "wx_001",
      "youzanId": "yz_001",
      "username": "testuser001",
      "nickname": "测试用户001",
      "avatar": "https://example.com/avatar.jpg",
      "email": "test001@example.com",
      "gender": 1,
      "birthday": "1990-01-01",
      "status": 1,
      "userType": 2,
      "lastLoginTime": "2025-01-27T10:00:00",
      "createTime": "2025-01-27T10:00:00",
      "updateTime": "2025-01-27T10:00:00"
    }
  }
  ```
- **错误码**:
  - `401`: 无权限访问
  - `404`: 用户不存在
  - `500`: 服务器内部错误
- **业务规则**:
  - 只能查看自己的信息或管理员可查看所有用户
  - 敏感信息需要脱敏处理

#### 3.1.3 更新用户信息
- **接口路径**: `PUT /user/{userId}`
- **功能描述**: 更新用户的基础信息（昵称、头像、邮箱等）
- **Feign调用**: 否
- **请求头**:
  ```
  Content-Type: application/json
  Authorization: Bearer {token}
  ```
- **路径参数**:
  - `userId`: 用户ID
- **请求参数**:
  ```json
  {
    "nickname": "新昵称",
    "avatar": "https://example.com/avatar.jpg",
    "email": "newemail@example.com",
    "gender": 2,
    "birthday": "1990-02-02",
    "realName": "张三",
    "companyName": "测试公司",
    "contactPhone": "13900139001",
    "industry": "互联网"
  }
  ```
- **响应示例**:
  ```json
  {
    "code": 200,
    "message": "更新成功",
    "data": true
  }
  ```
- **错误码**:
  - `400`: 参数验证失败
  - `401`: 无权限修改
  - `404`: 用户不存在
  - `500`: 服务器内部错误
- **业务规则**:
  - 只能修改自己的信息或管理员可修改所有用户
  - 邮箱格式验证
  - 联系电话格式验证

#### 3.1.4 删除用户
- **接口路径**: `DELETE /user/{userId}`
- **功能描述**: 软删除指定用户
- **Feign调用**: 否
- **请求头**:
  ```
  Authorization: Bearer {token}
  ```
- **路径参数**:
  - `userId`: 用户ID
- **响应示例**:
  ```json
  {
    "code": 200,
    "message": "删除成功",
    "data": true
  }
  ```
- **错误码**:
  - `401`: 无权限删除
  - `404`: 用户不存在
  - `500`: 服务器内部错误
- **业务规则**:
  - 需要管理员权限
  - 软删除，设置is_deleted=1
  - 不能删除自己

#### 3.1.5 用户列表查询
- **接口路径**: `GET /user/list`
- **功能描述**: 分页查询用户列表，支持多条件筛选
- **Feign调用**: 否
- **请求头**:
  ```
  Authorization: Bearer {token}
  ```
- **请求参数**:
  ```
  username: string  // 用户名模糊查询（可选）
  phone: string     // 手机号模糊查询（可选）
  email: string     // 邮箱模糊查询（可选）
  userType: 2       // 用户类型筛选（可选）
  status: 1         // 状态筛选（可选）
  page: 1           // 页码（可选，默认1）
  size: 10          // 每页大小（可选，默认10）
  ```
- **响应示例**:
  ```json
  {
    "code": 200,
    "message": "查询成功",
    "data": {
      "total": 100,
      "pages": 10,
      "current": 1,
      "size": 10,
      "records": [
        {
          "id": "1234567890abcdef",
          "phone": "13800138001",
          "username": "testuser001",
          "nickname": "测试用户001",
          "avatar": "https://example.com/avatar.jpg",
          "email": "test001@example.com",
          "gender": 1,
          "birthday": "1990-01-01",
          "status": 1,
          "userType": 2,
          "lastLoginTime": "2025-01-27T10:00:00",
          "createTime": "2025-01-27T10:00:00"
        }
      ]
    }
  }
  ```
- **错误码**:
  - `401`: 无权限查询
  - `500`: 服务器内部错误
- **业务规则**:
  - 需要管理员权限
  - 支持分页查询
  - 支持多条件筛选
  - 敏感信息脱敏

#### 3.1.6 批量删除用户
- **接口路径**: `DELETE /user/batch`
- **功能描述**: 批量软删除多个用户
- **Feign调用**: 否
- **请求头**:
  ```
  Content-Type: application/json
  Authorization: Bearer {token}
  ```
- **请求参数**:
  ```json
  {
    "userIds": ["1234567890abcdef", "abcdef1234567890", "9876543210fedcba"]
  }
  ```
- **响应示例**:
  ```json
  {
    "code": 200,
    "message": "批量删除成功",
    "data": {
      "successCount": 3,
      "failCount": 0,
      "failedUsers": []
    }
  }
  ```
- **错误码**:
  - `400`: 参数验证失败
  - `401`: 无权限操作
  - `500`: 服务器内部错误
- **业务规则**:
  - 需要管理员权限
  - 不能删除自己
  - 软删除，设置is_deleted=1
  - 返回删除结果统计

### 3.2 用户扩展信息管理接口 {#32-用户扩展信息管理接口}

#### 3.2.1 获取用户扩展信息
- **接口路径**: `GET /profile/{userId}`
- **功能描述**: 获取用户的详细资料和公司信息
- **Feign调用**: 是
- **请求头**:
  ```
  Authorization: Bearer {token}
  ```
- **路径参数**:
  - `userId`: 用户ID
- **响应示例**:
  ```json
  {
    "code": 200,
    "message": "获取成功",
    "data": {
      "id": "profile_123456",
      "userId": "1234567890abcdef",
      "realName": "张三",
      "companyName": "测试公司",
      "companyAddress": "北京市朝阳区测试街道123号",
      "contactPerson": "李四",
      "contactPhone": "13900139001",
      "industry": "互联网",
      "description": "这是一段个人简介",
      "createTime": "2025-01-27T10:00:00",
      "updateTime": "2025-01-27T10:00:00"
    }
  }
  ```
- **错误码**:
  - `401`: 无权限访问
  - `404`: 扩展信息不存在
  - `500`: 服务器内部错误
- **业务规则**:
  - 只能查看自己的扩展信息或管理员可查看所有
  - 如果不存在返回404

#### 3.2.2 创建或更新用户扩展信息
- **接口路径**: `POST /profile/{userId}`
- **功能描述**: 创建或更新用户的扩展信息（真实姓名、公司信息等）
- **Feign调用**: 否
- **请求头**:
  ```
  Content-Type: application/json
  Authorization: Bearer {token}
  ```
- **路径参数**:
  - `userId`: 用户ID
- **请求参数**:
  ```json
  {
    "realName": "张三",
    "companyName": "测试公司",
    "companyAddress": "北京市朝阳区测试街道123号",
    "contactPerson": "李四",
    "contactPhone": "13900139001",
    "industry": "互联网",
    "description": "这是一段个人简介"
  }
  ```
- **响应示例**:
  ```json
  {
    "code": 200,
    "message": "操作成功",
    "data": true
  }
  ```
- **错误码**:
  - `400`: 参数验证失败
  - `401`: 无权限操作
  - `500`: 服务器内部错误
- **业务规则**:
  - 只能修改自己的扩展信息或管理员可修改所有
  - 如果不存在则创建，存在则更新
  - 联系电话格式验证

## 4. 功能特性

### 4.1 用户管理功能
- ✅ **用户创建与删除**
  - 管理员创建用户（支持批量）
  - 管理员删除用户（支持批量）
  - 用户信息验证和唯一性检查
  - 软删除机制
  
- ✅ **用户信息查询与更新**
  - 用户信息查询（支持分页和筛选）
  - 用户信息更新（权限控制）
  - 敏感信息脱敏处理
  - 数据验证和格式检查
  
- ✅ **用户状态管理**
  - 用户状态控制（启用/禁用）
  - 用户类型管理（管理员/普通用户）
  - 状态变更记录

### 4.2 用户扩展信息功能
- ✅ **扩展信息管理**
  - 用户详细资料管理
  - 企业信息管理
  - 联系信息管理
  - 个人简介管理

### 4.3 安全特性
- ✅ **认证授权**
  - JWT Token验证
  - 用户权限验证
  - 数据访问控制
  - 跨服务认证
  
- ✅ **数据安全**
  - 敏感信息脱敏
  - 输入参数验证
  - SQL注入防护
  - 数据完整性保护

## 5. 技术实现

### 5.1 技术栈
- **框架**: Spring Boot 3.x
- **数据库**: MySQL 8.0
- **ORM**: MyBatis-Plus
- **缓存**: Redis
- **认证**: JWT + Spring Security
- **文档**: Knife4j (Swagger)
- **配置中心**: Nacos
- **服务发现**: Nacos Discovery
- **服务调用**: OpenFeign

### 5.2 核心配置
- **数据库连接池**: HikariCP
- **缓存策略**: Redis + 本地缓存
- **事务管理**: Spring声明式事务
- **异常处理**: 全局异常处理器
- **跨域配置**: CORS配置

### 5.3 缓存策略

#### 5.3.1 缓存配置
```yaml
# Redis缓存配置
spring:
  redis:
    host: localhost
    port: 6379
    database: 2
    timeout: 3000ms
    lettuce:
      pool:
        max-active: 8
        max-idle: 8
        min-idle: 0
        max-wait: -1ms

# 缓存策略配置
cache:
  user:
    info:
      key-prefix: "user:info:"
      ttl: 1800  # 30分钟
    profile:
      key-prefix: "user:profile:"
      ttl: 3600  # 1小时
    list:
      key-prefix: "user:list:"
      ttl: 300   # 5分钟
```

#### 5.3.2 缓存策略详情
| 接口 | 缓存策略 | TTL | 缓存键 | 说明 |
|------|----------|-----|--------|------|
| **获取用户信息** | Cache-Aside | 30分钟 | `user:info:{userId}` | 高频读取，数据相对稳定 |
| **获取用户扩展信息** | Cache-Aside | 1小时 | `user:profile:{userId}` | 变化频率低，其他服务需要 |
| **用户列表查询** | Cache-Aside | 5分钟 | `user:list:{queryHash}` | 管理后台频繁查询 |
| **创建用户** | 写后缓存 | - | `user:info:{userId}` | 创建后立即缓存 |
| **更新用户信息** | 写后清除 | - | `user:info:{userId}` | 更新后清除缓存 |
| **删除用户** | 写后清除 | - | `user:info:{userId}` | 删除后清除缓存 |

#### 5.3.3 缓存更新策略
- **读取策略**: 先查缓存，缓存未命中则查库并写入缓存
- **写入策略**: 先更新数据库，成功后清除相关缓存
- **删除策略**: 先删除数据库，成功后清除相关缓存
- **缓存穿透**: 使用布隆过滤器或缓存空值
- **缓存雪崩**: 设置随机TTL，避免同时过期

### 5.4 项目结构
```
src/main/java/com/origin/user/
├── config/           # 配置类
├── controller/       # 控制器
├── dto/             # 数据传输对象
├── entity/          # 实体类
├── feign/           # Feign客户端
├── interceptor/     # 拦截器
├── mapper/          # 数据访问层
├── service/         # 业务逻辑层
│   └── impl/        # 业务逻辑实现
└── util/            # 工具类
```

### 5.6 依赖关系
- **service-base**: Spring Boot公共配置（MyBatis-Plus、Redis、Knife4j等配置）
- **service-common**: 公共组件和工具类（ResultData、BaseEntity、BusinessException等）
- **service-auth**: 认证服务（通过Feign调用）
- **service-gateway**: 网关服务（路由转发）

### 5.5 依赖管理

#### 5.5.1 基础依赖包
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

#### 5.5.2 核心依赖
```xml
<dependencies>
    <!-- Spring Boot Starter -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
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

#### 5.5.3 依赖包说明

##### **service-base** - 基础配置包
- **作用**: Spring Boot公共配置
- **包含内容**:
  - MyBatis-Plus配置
  - Redis配置
  - Knife4j配置
  - 全局异常处理器
  - 跨域配置
  - 事务配置
  - 其他Spring Boot自动配置

##### **service-common** - 公共工具包
- **作用**: 静态方法和工具类
- **包含内容**:
  - 统一响应格式 (ResultData)
  - 基础实体类 (BaseEntity)
  - 业务异常类 (BusinessException)
  - 错误码定义 (ErrorCode)
  - 请求追踪 (RequestTrace)
  - 其他通用工具类

#### 5.5.4 依赖排除配置
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

## 6. 后续优化计划

### 6.1 性能优化
- [ ] 用户信息缓存优化
- [ ] 数据库查询优化
- [ ] 分页查询性能提升

### 6.2 功能增强
- [ ] 用户头像上传功能
- [ ] 用户数据导入导出
- [ ] 用户行为分析
- [ ] 用户标签管理

### 6.3 安全加固
- [ ] 数据加密存储
- [ ] 操作审计日志
- [ ] 敏感信息脱敏
- [ ] 访问频率限制 
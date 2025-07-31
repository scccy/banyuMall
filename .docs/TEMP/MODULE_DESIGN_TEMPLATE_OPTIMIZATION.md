# 模块设计模板优化建议

## 🔍 当前模板分析

通过分析 `infra/moudleDocs/xxxxxx-design_templates.md`，我发现这是一个很好的基础模板，但有一些关键部分需要完善，以便我能更高效地进行代码生成。

## 🎯 优化建议

### 1. 接口设计部分需要详细化

**当前问题**: 接口设计部分大部分是空的，只有标题没有具体内容

**优化建议**: 每个接口都应该包含以下详细信息：

```markdown
#### 3.1.1 创建用户
- **接口路径**: `POST /user`
- **请求头**: `Content-Type: application/json`
- **请求参数**:
  ```json
  {
    "username": "string",        // 用户名（必填，4-20位字母数字下划线）
    "nickname": "string",        // 昵称（可选）
    "email": "string",           // 邮箱（可选，需验证格式）
    "phone": "string",           // 手机号（可选，需验证格式）
    "userType": 2,               // 用户类型（必填，1-最高权限，2-普通发布者）
    "realName": "string",        // 真实姓名（可选）
    "companyName": "string",     // 公司名称（可选）
    "description": "string"      // 个人简介（可选）
  }
  ```
- **响应结果**:
  ```json
  {
    "code": 200,
    "message": "创建成功",
    "data": {
      "userId": "string",
      "username": "string",
      "createTime": "2025-01-27T10:00:00"
    }
  }
  ```
- **错误码**:
  - `400`: 参数验证失败
  - `409`: 用户名已存在
  - `500`: 服务器内部错误
- **业务规则**:
  - 用户名必须唯一
  - 邮箱和手机号可选但必须唯一
  - 用户类型必须为1或2
```

### 2. 数据模型设计需要明确化

**当前问题**: 只是简单引用了SQL文件，没有明确说明实体类设计

**优化建议**: 添加详细的实体类设计说明：

```markdown
## 2. 数据模型设计

### 2.1 数据库表结构
参考: [user-schema.sql](../database/data/user/user-schema.sql)

### 2.2 实体类设计

#### 2.2.1 User实体类
```java
@Data
@TableName("sys_user")
public class User extends BaseEntity {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    @TableField("username")
    private String username;
    
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
    
    @TableField("user_type")
    private Integer userType;
    
    @TableField("status")
    private Integer status;
    
    @TableField("last_login_time")
    private LocalDateTime lastLoginTime;
}
```

#### 2.2.2 DTO类设计
```java
// 创建用户请求DTO
@Data
public class UserCreateRequest {
    @NotBlank(message = "用户名不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9_]{4,20}$", message = "用户名格式不正确")
    private String username;
    
    private String nickname;
    
    @Email(message = "邮箱格式不正确")
    private String email;
    
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    
    @NotNull(message = "用户类型不能为空")
    @Min(value = 1, message = "用户类型不正确")
    @Max(value = 2, message = "用户类型不正确")
    private Integer userType;
    
    // ... 其他字段
}
```
```

### 3. 功能特性需要具体化

**当前问题**: 功能特性部分描述不够具体

**优化建议**: 详细描述每个功能的具体实现要求：

```markdown
## 4. 功能特性

### 4.1 用户管理功能
- ✅ **用户注册与创建**
  - 支持用户名、邮箱、手机号注册
  - 用户名唯一性验证
  - 邮箱和手机号格式验证
  - 用户类型必填验证
  
- ✅ **用户信息查询与更新**
  - 根据用户ID查询用户信息
  - 支持部分字段更新
  - 更新时进行权限验证
  
- ✅ **用户状态管理**
  - 支持启用/禁用用户
  - 状态变更记录审计日志
  - 禁用用户后无法登录
  
- ✅ **用户列表分页查询**
  - 支持按用户名、邮箱、手机号模糊搜索
  - 支持按用户类型、状态筛选
  - 支持按创建时间排序
  - 分页参数：page(页码)、size(每页大小)
```

### 4. 技术实现需要具体化

**当前问题**: 技术栈和配置部分为空

**优化建议**: 明确技术实现细节：

```markdown
## 5. 技术实现

### 5.1 技术栈
- **框架**: Spring Boot 3.2.x
- **数据库**: MySQL 8.0
- **ORM**: MyBatis-Plus 3.5.x
- **缓存**: Redis 7.x
- **认证**: JWT Token
- **文档**: Knife4j (Swagger 3)
- **验证**: Jakarta Validation
- **日志**: Log4j2

### 5.2 核心配置
```yaml
# application.yml
spring:
  application:
    name: service-user
  datasource:
    url: jdbc:mysql://localhost:3306/banyu_mall
    username: root
    password: password
  redis:
    host: localhost
    port: 6379
    database: 1

server:
  port: 8082

# 认证服务配置
auth:
  service:
    url: http://localhost:8081
    timeout: 5000
```

### 5.3 缓存策略
- **用户信息缓存**: Redis Key: `user:info:{userId}`, TTL: 30分钟
- **用户配置缓存**: Redis Key: `user:config:{userId}`, TTL: 1小时
- **缓存更新策略**: 写入时更新缓存，删除时清除缓存
```

### 5. 测试用例需要具体化

**当前问题**: 测试用例部分为空

**优化建议**: 明确测试要求：

```markdown
## 7. 测试用例

### 7.1 单元测试
- **UserService测试**
  - 用户创建成功测试
  - 用户名重复测试
  - 用户信息更新测试
  - 用户状态变更测试
  
- **UserController测试**
  - 接口参数验证测试
  - 权限验证测试
  - 异常处理测试

### 7.2 集成测试
- **用户注册流程测试**
  - 正常注册流程
  - 异常情况处理
  - 数据一致性验证

### 7.3 API测试
- **Postman测试集合**
  - 所有接口的请求/响应测试
  - 错误场景测试
  - 性能测试
```

### 6. 部署配置需要具体化

**当前问题**: 部署配置部分为空

**优化建议**: 明确部署要求：

```markdown
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
        - id: user-service
          uri: lb://service-user
          predicates:
            - Path=/user/**
            - Path=/profile/**
            - Path=/config/**
          filters:
            - StripPrefix=0
            - name: RequestRateLimiter
              args:
                redis-rate-limiter.replenishRate: 10
                redis-rate-limiter.burstCapacity: 20
```

### 6.3 Docker配置
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/service-user.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "/app.jar"]
```
```

## 🚀 优化后的完整模板结构

```markdown
# [模块名称]设计文档

## 1. 模块概述
[模块功能描述、业务价值、核心职责]

## 2. 数据模型设计
### 2.1 数据库表结构
[SQL文件引用或表结构说明]

### 2.2 实体类设计
[详细的实体类、DTO类设计]

## 3. 接口设计
### 3.1 [功能模块1]接口
#### 3.1.1 [接口名称]
- **接口路径**: `[HTTP方法] [路径]`
- **请求头**: `[请求头要求]`
- **请求参数**: `[详细的JSON格式参数]`
- **响应结果**: `[详细的JSON格式响应]`
- **错误码**: `[错误码列表]`
- **业务规则**: `[业务逻辑要求]`

## 4. 功能特性
### 4.1 [功能模块1]
- ✅ **[具体功能1]**
  - [详细实现要求]
  - [业务规则]
- ✅ **[具体功能2]**
  - [详细实现要求]

## 5. 技术实现
### 5.1 技术栈
[具体的技术选型和版本]

### 5.2 核心配置
[详细的配置文件内容]

### 5.3 缓存策略
[缓存设计和使用策略]

## 6. 部署配置
### 6.1 应用配置
[环境配置文件]

### 6.2 网关路由配置
[网关路由规则]

### 6.3 Docker配置
[容器化配置]

## 7. 测试用例
### 7.1 单元测试
[具体的测试用例要求]

### 7.2 集成测试
[集成测试要求]

### 7.3 API测试
[API测试要求]
```

## 📋 总结

**关键优化点**:
1. **接口设计详细化** - 包含完整的请求/响应格式、错误码、业务规则
2. **数据模型具体化** - 明确的实体类和DTO设计
3. **功能特性具体化** - 详细的实现要求和业务规则
4. **技术实现明确化** - 具体的技术栈、配置、缓存策略
5. **测试用例具体化** - 明确的测试要求和覆盖范围
6. **部署配置完整化** - 环境配置、网关配置、容器配置

**预期效果**:
- 减少开发过程中的沟通成本
- 提高代码生成的质量和准确性
- 确保实现与设计的一致性
- 便于后续的维护和扩展

您觉得这些优化建议如何？我可以根据您的反馈进一步调整模板结构。 
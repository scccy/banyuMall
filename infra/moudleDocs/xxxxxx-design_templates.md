# {模块名称} 设计文档

> **文档位置**: `infra/moudleDocs/{模块名称}/design.md`
> **创建时间**: YYYY-MM-DD
> **作者**: scccy

## ⚠️ 核心注意事项

> **重要提醒**: 请在此处填写该模块的核心注意事项、关键约束、特殊要求等重要信息。

### 关键约束
- [ ] **约束1**: [请填写具体约束内容]
- [ ] **约束2**: [请填写具体约束内容]
- [ ] **约束3**: [请填写具体约束内容]

### 特殊要求
- [ ] **要求1**: [请填写特殊要求内容]
- [ ] **要求2**: [请填写特殊要求内容]
- [ ] **要求3**: [请填写特殊要求内容]

### 风险提示
- [ ] **风险1**: [请填写风险提示内容]
- [ ] **风险2**: [请填写风险提示内容]
- [ ] **风险3**: [请填写风险提示内容]

### 依赖关系
- [ ] **依赖1**: [请填写依赖关系内容]
- [ ] **依赖2**: [请填写依赖关系内容]
- [ ] **依赖3**: [请填写依赖关系内容]

---

## 1. 模块概述

{模块名称}模块是{系统名称}系统的{模块功能描述}模块，负责{核心功能1}、{核心功能2}和{核心功能3}。

**核心职责**:
1. {核心职责1}
2. {核心职责2}

### 1.1 接口功能列表

| 序号 | 接口名称 | 请求方法 | 接口路径 | 功能描述 | 职责对应 | 详细说明 |
|------|----------|----------|----------|----------|----------|----------|
| 1 | {接口名称1} | {请求方法1} | `{接口路径1}` | {功能描述1} | {职责对应1} | [查看详情](#31-{接口分类1}) |
| 2 | {接口名称2} | {请求方法2} | `{接口路径2}` | {功能描述2} | {职责对应2} | [查看详情](#31-{接口分类1}) |
| 3 | {接口名称3} | {请求方法3} | `{接口路径3}` | {功能描述3} | {职责对应3} | [查看详情](#31-{接口分类1}) |
| 4 | {接口名称4} | {请求方法4} | `{接口路径4}` | {功能描述4} | {职责对应4} | [查看详情](#32-{接口分类2}) |
| 5 | {接口名称5} | {请求方法5} | `{接口路径5}` | {功能描述5} | {职责对应5} | [查看详情](#32-{接口分类2}) |
| 6 | {接口名称6} | {请求方法6} | `{接口路径6}` | {功能描述6} | {职责对应6} | [查看详情](#33-{接口分类3}) |

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
参考: [{数据库脚本路径}](../database/data/{模块名}/{模块名}-schema.sql)

### 2.2 实体类设计

#### 2.2.1 {实体类1}实体类
```java
@Data
@TableName("{表名1}")
public class {实体类1} extends BaseEntity {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    @TableField("{字段名1}")
    private {数据类型1} {字段名1};
    
    @TableField("{字段名2}")
    private {数据类型2} {字段名2};
    
    // ... 其他字段
}
```

#### 2.2.2 {实体类2}实体类
```java
@Data
@TableName("{表名2}")
public class {实体类2} extends BaseEntity {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    @TableField("{字段名1}")
    private {数据类型1} {字段名1};
    
    @TableField("{字段名2}")
    private {数据类型2} {字段名2};
    
    // ... 其他字段
}
```

### 2.3 DTO设计

#### 2.3.1 {请求DTO1}
```java
@Data
public class {请求DTO1} {
    @NotBlank(message = "{字段名1}不能为空")
    private String {字段名1};
    
    @Email(message = "{字段名2}格式不正确")
    private String {字段名2};
    
    @NotNull(message = "{字段名3}不能为空")
    private Integer {字段名3};
    
    // ... 其他字段
}
```

#### 2.3.2 {请求DTO2}
```java
@Data
public class {请求DTO2} {
    @NotBlank(message = "{字段名1}不能为空")
    private String {字段名1};
    
    @Size(max = 100, message = "{字段名2}长度不能超过100")
    private String {字段名2};
    
    // ... 其他字段
}
```

#### 2.3.3 {查询DTO}
```java
@Data
public class {查询DTO} {
    private String {查询字段1};
    private String {查询字段2};
    private Integer {查询字段3};
    private Integer {查询字段4};
    
    @Min(value = 1, message = "页码不能小于1")
    private Integer page = 1;
    
    @Min(value = 1, message = "每页大小不能小于1")
    @Max(value = 100, message = "每页大小不能超过100")
    private Integer size = 10;
}
```

## 3. 接口设计

### 3.1 {接口分类1}接口 {#31-{接口分类1}}

#### 3.1.1 {接口名称1}
- **接口路径**: `POST /{接口路径1}`
- **功能描述**: {功能描述1}
- **请求头**:
  ```
  Content-Type: application/json
  Authorization: Bearer {token}
  ```
- **请求参数**:
  ```json
  {
    "{参数名1}": "{参数值1}",
    "{参数名2}": "{参数值2}",
    "{参数名3}": {参数值3}
  }
  ```
- **响应示例**:
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "{返回字段1}": "{返回值1}",
      "{返回字段2}": "{返回值2}"
    }
  }
  ```
- **错误码**:
  - `400`: 参数验证失败
  - `401`: 未授权访问
  - `403`: 权限不足
  - `500`: 服务器内部错误
- **业务规则**:
  - {业务规则1}
  - {业务规则2}

#### 3.1.2 {接口名称2}
- **接口路径**: `GET /{接口路径2}`
- **功能描述**: {功能描述2}
- **请求头**:
  ```
  Authorization: Bearer {token}
  ```
- **路径参数**:
  - `{参数名}`: {参数描述}
- **响应示例**:
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "{返回字段1}": "{返回值1}",
      "{返回字段2}": "{返回值2}"
    }
  }
  ```
- **错误码**:
  - `404`: 资源不存在
  - `401`: 未授权访问
- **业务规则**:
  - {业务规则1}

### 3.2 {接口分类2}接口 {#32-{接口分类2}}

#### 3.2.1 {接口名称3}
- **接口路径**: `POST /{接口路径3}`
- **功能描述**: {功能描述3}
- **请求头**:
  ```
  Content-Type: application/json
  Authorization: Bearer {token}
  ```
- **路径参数**:
  - `{参数名}`: {参数描述}
- **请求参数**:
  ```json
  {
    "{参数名1}": "{参数值1}",
    "{参数名2}": "{参数值2}"
  }
  ```
- **响应示例**:
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "{返回字段1}": "{返回值1}"
    }
  }
  ```
- **错误码**:
  - `400`: 参数验证失败
  - `404`: 资源不存在
- **业务规则**:
  - {业务规则1}

### 3.3 {接口分类3}接口 {#33-{接口分类3}}

#### 3.3.1 {接口名称4}
- **接口路径**: `GET /{接口路径4}`
- **功能描述**: {功能描述4}
- **请求头**:
  ```
  Authorization: Bearer {token}
  ```
- **路径参数**:
  - `{参数名}`: {参数描述}
- **响应示例**:
  ```json
  {
    "code": 200,
    "message": "success",
    "data": [
      {
        "{返回字段1}": "{返回值1}",
        "{返回字段2}": "{返回值2}"
      }
    ]
  }
  ```
- **错误码**:
  - `401`: 未授权访问
- **业务规则**:
  - {业务规则1}

## 4. 功能特性

### 4.1 {功能特性1}
- {功能描述1}
- {功能描述2}
- {功能描述3}

### 4.2 {功能特性2}
- {功能描述1}
- {功能描述2}
- {功能描述3}

### 4.3 {功能特性3}
- {功能描述1}
- {功能描述2}
- {功能描述3}

### 4.4 安全特性
- **权限控制**: 基于用户角色和权限的访问控制
- **数据验证**: 请求参数和服务端数据验证
- **审计日志**: 关键操作记录审计日志
- **数据脱敏**: 敏感信息展示时进行脱敏处理

## 5. 技术实现

### 5.1 技术栈
- **后端框架**: Spring Boot 3.x
- **数据库**: MySQL 8.0
- **ORM框架**: MyBatis-Plus
- **缓存**: Redis
- **认证授权**: JWT + Spring Security
- **API文档**: Knife4j (Swagger)
- **配置中心**: Nacos
- **服务发现**: Nacos Discovery
- **参数验证**: Jakarta Validation
- **日志框架**: Log4j2

### 5.2 核心配置
- **数据库连接池**: HikariCP
- **缓存策略**: Redis + 本地缓存
- **事务管理**: Spring声明式事务
- **异常处理**: 全局异常处理器
- **跨域配置**: CORS配置

### 5.3 缓存策略
- **用户信息缓存**: TTL 30分钟
- **配置信息缓存**: TTL 1小时
- **查询结果缓存**: TTL 10分钟
- **缓存更新策略**: 写入时更新缓存

### 5.4 项目结构
```
src/main/java/com/origin/{模块名}/
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

### 5.6 依赖关系
- **service-base**: Spring Boot公共配置（MyBatis-Plus、Redis、Knife4j等配置）
- **service-common**: 公共组件和工具类（ResultData、BaseEntity、BusinessException等）
- **service-auth**: 认证服务（通过Feign调用）
- **service-gateway**: 网关服务（路由转发）

## 6. 后续优化计划

### 6.1 性能优化
- [ ] {性能优化1}
- [ ] {性能优化2}
- [ ] {性能优化3}

### 6.2 功能增强
- [ ] {功能增强1}
- [ ] {功能增强2}
- [ ] {功能增强3}

### 6.3 安全加固
- [ ] {安全加固1}
- [ ] {安全加固2}
- [ ] {安全加固3}


# [模块名称] 模块设计文档

## 📋 模块概述

### 基本信息
- **模块名称**: [模块名称]
- **模块类型**: [微服务模块/核心模块/第三方服务模块]
- **主要职责**: [模块的主要功能描述]
- **技术栈**: [Spring Boot + 其他技术栈]
- **作者**: scccy
- **创建时间**: [YYYY-MM-DD]

### 设计理念
[描述模块的设计理念和核心思想]

## 🏗️ 架构设计

### 核心架构图
```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Controller    │    │     Service     │    │     Mapper      │
│                 │    │                 │    │                 │
│ [Controller]    │───▶│  [Service]      │───▶│  [Mapper]       │
│                 │    │                 │    │                 │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         │                       │                       │
         ▼                       ▼                       ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Interceptor   │    │      Util       │    │    Database     │
│                 │    │                 │    │                 │
│ [Interceptor]   │    │  [Util]         │    │    [Table]      │
│                 │    │                 │    │                 │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

### 业务模型
```
[描述模块的核心业务模型和流程]
```

## 📁 目录结构

```
[模块名称]/
├── src/main/java/com/origin/[模块包名]/
│   ├── controller/
│   │   └── [Controller].java              # 控制器
│   ├── service/
│   │   ├── [Service].java                 # 服务接口
│   │   └── impl/
│   │       └── [ServiceImpl].java         # 服务实现
│   ├── entity/
│   │   └── [Entity].java                  # 实体类
│   ├── mapper/
│   │   └── [Mapper].java                  # 数据访问层
│   ├── dto/
│   │   ├── [RequestDTO].java              # 请求DTO
│   │   └── [ResponseDTO].java             # 响应DTO
│   ├── feign/
│   │   ├── [FeignClient].java             # Feign客户端接口
│   │   └── [FeignClientFallback].java     # Feign降级处理
│   ├── util/
│   │   └── [Util].java                    # 工具类
│   ├── interceptor/
│   │   └── [Interceptor].java             # 拦截器
│   ├── exception/
│   │   └── [Exception].java               # 异常类
│   └── [ModuleName]Application.java        # 启动类
└── src/main/resources/
    ├── application.yml                     # 配置文件
    └── mapper/
        └── [Mapper].xml                    # MyBatis映射文件
```

## 🔧 核心组件

### 1. 控制器 ([Controller])

#### 主要接口
```java
@RestController
@RequestMapping("/service/[entity]")
public class [Controller] {
    
    // [接口描述]
    @PostMapping("/[action]")
    public ResultData<[ResponseDTO]> [methodName](@RequestBody [RequestDTO] request);
    
    // [接口描述]
    @GetMapping("/[action]")
    public ResultData<[ResponseDTO]> [methodName](@RequestParam String param);
    
    // [接口描述]
    @PutMapping("/[action]/{id}")
    public ResultData<String> [methodName](@PathVariable String id, @RequestBody [RequestDTO] request);
    
    // [接口描述]
    @DeleteMapping("/[action]/{id}")
    public ResultData<String> [methodName](@PathVariable String id);
}
```

#### 接口说明
| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| [接口名称] | [HTTP方法] | `/service/[entity]/[action]` | [接口描述] |
| [接口名称] | [HTTP方法] | `/service/[entity]/[action]` | [接口描述] |
| [接口名称] | [HTTP方法] | `/service/[entity]/[action]` | [接口描述] |
| [接口名称] | [HTTP方法] | `/service/[entity]/[action]` | [接口描述] |

### 2. 服务层 ([Service])

#### 核心方法
```java
public interface [Service] {
    
    // [方法描述]
    [ResponseDTO] [methodName]([RequestDTO] request);
    
    // [方法描述]
    boolean [methodName](String id);
    
    // [方法描述]
    List<[ResponseDTO]> [methodName]([RequestDTO] request);
    
    // [方法描述]
    void [methodName](String id, [RequestDTO] request);
}
```

#### 业务逻辑
```java
// [业务逻辑示例]
public [ResponseDTO] [methodName]([RequestDTO] request) {
    // 1. 参数验证
    // 2. 业务处理
    // 3. 数据持久化
    // 4. 返回结果
}
```

### 3. Feign客户端列表

#### 依赖的外部服务
| 服务名称 | Feign客户端 | 主要用途 | 接口路径 |
|----------|-------------|----------|----------|
| [服务名称] | [FeignClient] | [用途描述] | `/service/[entity]` |
| [服务名称] | [FeignClient] | [用途描述] | `/service/[entity]` |
| [服务名称] | [FeignClient] | [用途描述] | `/service/[entity]` |

#### Feign客户端接口
```java
@FeignClient(name = "[service-name]", fallback = [FeignClientFallback].class)
public interface [FeignClient] {
    
    // [接口描述]
    @PostMapping("/service/[entity]/[action]")
    ResultData<[ResponseDTO]> [methodName](@RequestBody [RequestDTO] request);
    
    // [接口描述]
    @GetMapping("/service/[entity]/[action]")
    ResultData<[ResponseDTO]> [methodName](@RequestParam String param);
    
    // [接口描述]
    @PutMapping("/service/[entity]/[action]/{id}")
    ResultData<String> [methodName](@PathVariable String id, @RequestBody [RequestDTO] request);
}
```

#### Feign降级处理
```java
@Component
public class [FeignClientFallback] implements [FeignClient] {
    
    @Override
    public ResultData<[ResponseDTO]> [methodName]([RequestDTO] request) {
        // 降级处理逻辑
        return ResultData.error(ErrorCode.SERVICE_UNAVAILABLE, "服务暂时不可用");
    }
    
    @Override
    public ResultData<[ResponseDTO]> [methodName](String param) {
        // 降级处理逻辑
        return ResultData.error(ErrorCode.SERVICE_UNAVAILABLE, "服务暂时不可用");
    }
}
```

### 4. 工具类 ([Util])

#### 核心功能
```java
@Component
public class [Util] {
    
    // [功能描述]
    public [ReturnType] [methodName]([ParameterType] param);
    
    // [功能描述]
    public boolean [methodName]([ParameterType] param);
    
    // [功能描述]
    public [ReturnType] [methodName]([ParameterType] param);
}
```

### 5. 拦截器 ([Interceptor])

#### 拦截逻辑
```java
@Component
public class [Interceptor] implements HandlerInterceptor {
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 1. [处理步骤1]
        // 2. [处理步骤2]
        // 3. [处理步骤3]
        // 4. [处理步骤4]
        
        return true;
    }
}
```

## 🗄️ 数据模型

### 实体类 ([Entity])
```java
@Data
@TableName("[table_name]")
public class [Entity] extends BaseEntity {
    
    @TableId(value = "[table_name]_id", type = IdType.ASSIGN_ID)
    private String [tableName]Id;            // [字段描述]
    
    private String [fieldName];              // [字段描述]
    private Integer [fieldName];             // [字段描述]
    private LocalDateTime [fieldName];       // [字段描述]
    private String [fieldName];              // [字段描述]
    private Integer [fieldName];             // [字段描述]
}
```

### DTO设计

#### 请求DTO ([RequestDTO])
```java
@Data
public class [RequestDTO] {
    private String [fieldName];              // [字段描述]
    private String [fieldName];              // [字段描述]
    private Integer [fieldName];             // [字段描述]
}
```

#### 响应DTO ([ResponseDTO])
```java
@Data
public class [ResponseDTO] {
    private String [fieldName];              // [字段描述]
    private String [fieldName];              // [字段描述]
    private Integer [fieldName];             // [字段描述]
    private LocalDateTime [fieldName];       // [字段描述]
}
```

## 🔐 安全设计

### 1. 认证授权
- **JWT验证**: 通过JWT拦截器验证用户身份
- **权限控制**: [描述权限控制策略]
- **数据隔离**: [描述数据隔离策略]

### 2. 数据安全
- **输入验证**: [描述输入验证策略]
- **SQL注入防护**: 使用MyBatis Plus参数化查询
- **敏感数据处理**: [描述敏感数据处理策略]

### 3. 接口安全
- **接口限流**: [描述限流策略]
- **异常处理**: 统一的异常处理机制
- **日志记录**: 关键操作的日志记录

## ⚙️ 配置管理

### 应用配置
```yaml
spring:
  application:
    name: [service-name]
  datasource:
    url: jdbc:mysql://localhost:3306/banyu
    username: ${MYSQL_USERNAME:root}
    password: ${MYSQL_PASSWORD:root}
  redis:
    host: ${REDIS_HOST:localhost}
    port: ${REDIS_PORT:6379}
    password: ${REDIS_PASSWORD:}
    database: ${REDIS_DATABASE:0}

# [模块特定配置]
[module]:
  [config-key]: ${[ENV_VAR]:default-value}
  [config-key]: ${[ENV_VAR]:default-value}

# MyBatis Plus配置
mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  global-config:
    db-config:
      id-type: assign_id
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0
```

### 环境配置
- **开发环境**: `application-dev.yml`
- **测试环境**: `application-test.yml`
- **生产环境**: `application-prod.yml`

## 🔄 业务流程

### 1. [业务流程1]
```
1. [步骤1描述]
   ↓
2. [步骤2描述]
   ↓
3. [步骤3描述]
   ↓
4. [步骤4描述]
   ↓
5. [步骤5描述]
```

### 2. [业务流程2]
```
1. [步骤1描述]
   ↓
2. [步骤2描述]
   ↓
3. [步骤3描述]
   ↓
4. [步骤4描述]
```

### 3. [业务流程3]
```
1. [步骤1描述]
   ↓
2. [步骤2描述]
   ↓
3. [步骤3描述]
   ↓
4. [步骤4描述]
```

## 📊 性能优化

### 1. 缓存策略
- **Redis缓存**: [描述Redis缓存策略]
- **本地缓存**: [描述本地缓存策略]
- **缓存过期**: [描述缓存过期策略]

### 2. 数据库优化
- **索引优化**: [描述索引优化策略]
- **查询优化**: [描述查询优化策略]
- **连接池**: [描述连接池配置]

### 3. 接口优化
- **异步处理**: [描述异步处理策略]
- **批量操作**: [描述批量操作策略]
- **分页查询**: [描述分页查询策略]

## 🧪 测试策略

### 1. 单元测试
- **Service层测试**: 测试业务逻辑
- **Util层测试**: 测试工具类功能
- **Mapper层测试**: 测试数据访问

### 2. 集成测试
- **API测试**: 测试REST接口
- **数据库测试**: 测试数据库操作
- **Feign测试**: 测试外部服务调用

### 3. 性能测试
- **并发测试**: 测试并发处理性能
- **压力测试**: 测试系统承载能力
- **稳定性测试**: 测试长期运行稳定性

## 📈 监控指标

### 1. 业务指标
- **[指标名称]**: [指标描述]
- **[指标名称]**: [指标描述]
- **[指标名称]**: [指标描述]

### 2. 技术指标
- **[指标名称]**: [指标描述]
- **[指标名称]**: [指标描述]
- **[指标名称]**: [指标描述]

### 3. 外部服务指标
- **[服务名称]调用成功率**: [指标描述]
- **[服务名称]响应时间**: [指标描述]
- **[服务名称]降级次数**: [指标描述]

## 🔮 扩展规划

### 1. 功能扩展
- **[功能1]**: [功能描述]
- **[功能2]**: [功能描述]
- **[功能3]**: [功能描述]

### 2. 性能优化
- **[优化1]**: [优化描述]
- **[优化2]**: [优化描述]
- **[优化3]**: [优化描述]

### 3. 架构升级
- **[升级1]**: [升级描述]
- **[升级2]**: [升级描述]
- **[升级3]**: [升级描述]

---

**文档版本**: v1.0  
**创建时间**: [YYYY-MM-DD]  
**维护人员**: scccy 
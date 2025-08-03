# Feign客户端统一规范

**ID**: LR-005-FEIGN-UNIFIED  
**Name**: Feign客户端统一规范  
**Status**: Active  
**创建时间**: 2025-01-27  
**合并来源**: LR-005 (Feign客户端配置规范) + LR-008 (Feign客户端架构规范)

## 规则名称: Feign客户端架构与配置统一规范

## 触发情景 (Context/Trigger)
当设计微服务架构时，需要明确区分服务提供方和消费方的职责，并正确配置Feign客户端以避免Bean名称冲突。

## 指令 (Directive)

### 1. 架构原则

#### 1.1 服务提供方职责
- **必须 (MUST)** 只提供REST API接口
- **必须 (MUST)** 不定义Feign客户端
- **必须 (MUST)** 不启用Feign功能
- **禁止 (MUST NOT)** 在启动类中使用`@EnableFeignClients`

#### 1.2 服务消费方职责
- **必须 (MUST)** 定义Feign客户端来调用其他服务
- **必须 (MUST)** 在启动类中启用Feign功能
- **必须 (MUST)** 指定Feign客户端扫描包范围
- **必须 (MUST)** 提供降级处理类

### 2. 配置规范

#### 2.1 启动类配置
```java
// ✅ 服务提供方启动类（如auth模块）
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.origin.auth.mapper")
public class ServiceAuthApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceAuthApplication.class, args);
    }
}

// ✅ 服务消费方启动类（如user模块）
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.origin.user.feign"})  // 指定扫描包
@MapperScan("com.origin.user.mapper")
public class ServiceUserApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceUserApplication.class, args);
    }
}
```

#### 2.2 Feign客户端配置
```java
// ✅ 正确配置
@FeignClient(
    name = "service-auth", 
    path = "/service/auth", 
    fallback = AuthFeignClientFallback.class
)
public interface AuthFeignClient {
    @PostMapping("/login")  // 不包含/service/auth前缀
    ResultData login(@RequestBody LoginRequest request);
    
    @GetMapping("/user/info")  // 不包含/service/auth前缀
    ResultData<SysUser> getUserInfo(@RequestParam("userId") String userId);
}

// ❌ 错误配置 - 会导致Bean名称冲突
@FeignClient(name = "service-auth", fallback = AuthFeignClientFallback.class)
public interface AuthFeignClient {
    @PostMapping("/service/auth/login")  // 包含重复前缀
    ResultData login(@RequestBody LoginRequest request);
}
```

### 3. 命名规范

#### 3.1 服务命名
- **服务提供方**: 只提供REST API，不定义Feign客户端
- **服务消费方**: 定义Feign客户端，命名格式为`{ServiceName}FeignClient`
- **降级处理类**: 命名格式为`{ServiceName}FeignClientFallback`

#### 3.2 配置命名
- **Bean名称**: 使用目标服务名称，如`service-auth`
- **接口名称**: 使用功能描述，如`AuthFeignClient`
- **路径配置**: 使用服务的基础路径，如`/service/auth`

### 4. 包结构规范
```
服务提供方（auth模块）:
├── controller/          # REST控制器
├── service/            # 业务服务
├── mapper/             # 数据访问层
└── dto/                # 数据传输对象

服务消费方（user模块）:
├── feign/              # Feign客户端
│   ├── AuthFeignClient.java
│   └── AuthFeignClientFallback.java
├── service/            # 业务服务
└── controller/         # REST控制器
```

### 5. 最佳实践

#### 5.1 架构设计
- **明确职责**: 服务提供方专注于API实现，消费方专注于服务调用
- **避免重复**: 每个Feign客户端只在一个地方定义
- **统一管理**: 所有Feign客户端都在消费方模块中管理
- **版本控制**: 通过API版本控制来管理接口变更

#### 5.2 配置优化
- **使用path属性**: 避免Bean名称冲突
- **指定扫描包**: 优化启动性能
- **提供降级处理**: 增强系统容错能力
- **统一命名**: 提高代码可读性

## 理由 (Justification)
此规则合并了以下两个规则的经验：

1. **LR-005**: 源于任务 `task_20250801_2308_feign_conflict_resolution.md`，解决了多个Feign客户端指向同一服务导致的Bean名称冲突问题。

2. **LR-008**: 源于任务 `task_20250127_auth_feign_client_cleanup.md`，明确了微服务架构中服务提供方和消费方的职责分工。

通过统一规范，避免了架构混乱和配置冲突，提高了系统的可维护性和稳定性。

## 性能基准
- **启动时间**: 目标 < 3秒（开发环境）
- **Bean冲突**: 目标 0个
- **Feign客户端**: 每个服务唯一Bean名称
- **架构清晰度**: 服务职责明确，无重复定义

## 常见问题与解决方案

### 问题1: Bean名称冲突
**症状**: 启动时报错"bean could not be registered"
**解决方案**: 使用`path`属性，避免在`@RequestMapping`中重复路径前缀

### 问题2: 服务提供方错误定义Feign客户端
**症状**: 服务提供方和消费方都定义了相同的Feign客户端
**解决方案**: 删除服务提供方的Feign客户端定义，只在消费方定义

### 问题3: 扫描范围过大
**症状**: 扫描到不相关的Feign客户端
**解决方案**: 在`@EnableFeignClients`中指定`basePackages`

### 问题4: 依赖冲突
**症状**: 出现"Invalid value type for attribute"错误
**解决方案**: 排除不需要的自动配置，优化启动性能

## 违反示例
```java
// ❌ 错误：服务提供方定义了Feign客户端
@FeignClient(name = "service-auth", path = "/service/auth")
public interface AuthFeignClient {
    @PostMapping("/login")
    ResultData login(@RequestBody LoginRequest request);
}

// ❌ 错误：路径重复配置
@FeignClient(name = "service-auth", fallback = AuthFeignClientFallback.class)
public interface AuthFeignClient {
    @PostMapping("/service/auth/login")  // 包含重复前缀
    ResultData login(@RequestBody LoginRequest request);
}
```

## 相关规则
- **PERF-001**: Spring Boot启动性能优化规则
- **LR-006**: 微服务依赖排除规则

## 版本历史
| 版本 | 日期 | 变更内容 |
|------|------|----------|
| 1.0.0 | 2025-01-27 | 合并LR-005和LR-008，创建统一规范 |
| 1.0.1 | 2025-01-27 | 优化架构示例和最佳实践 | 
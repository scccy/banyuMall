# Feign客户端架构规范

**ID**: LR-008  
**Name**: Feign客户端架构规范  
**Status**: Active  
**创建时间**: 2025-01-27

## 规则名称: 服务提供方不应定义Feign客户端，只有服务消费方才需要

## 触发情景 (Context/Trigger)
当设计微服务架构时，需要明确区分服务提供方和消费方的职责，避免重复定义Feign客户端。

## 指令 (Directive)

### 1. 服务提供方职责
- **必须 (MUST)** 只提供REST API接口
- **必须 (MUST)** 不定义Feign客户端
- **必须 (MUST)** 不启用Feign功能
- **禁止 (MUST NOT)** 在启动类中使用`@EnableFeignClients`

### 2. 服务消费方职责
- **必须 (MUST)** 定义Feign客户端来调用其他服务
- **必须 (MUST)** 在启动类中启用Feign功能
- **必须 (MUST)** 指定Feign客户端扫描包范围
- **必须 (MUST)** 提供降级处理类

### 3. 架构示例

#### 服务提供方（如auth模块）
```java
// ✅ 正确的服务提供方启动类
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.origin.auth.mapper")
public class ServiceAuthApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceAuthApplication.class, args);
    }
}

// ✅ 正确的REST控制器
@RestController
@RequestMapping("/service/auth")
public class AuthController {
    @PostMapping("/login")
    public ResultData login(@RequestBody LoginRequest request) {
        // 实现登录逻辑
    }
}
```

#### 服务消费方（如user模块）
```java
// ✅ 正确的服务消费方启动类
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.origin.user.feign"})
@MapperScan("com.origin.user.mapper")
public class ServiceUserApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceUserApplication.class, args);
    }
}

// ✅ 正确的Feign客户端
@FeignClient(
    name = "service-auth", 
    path = "/service/auth", 
    fallback = AuthFeignClientFallback.class
)
public interface AuthFeignClient {
    @PostMapping("/login")
    ResultData login(@RequestBody LoginRequest request);
}
```

### 4. 命名规范
- **服务提供方**: 只提供REST API，不定义Feign客户端
- **服务消费方**: 定义Feign客户端，命名格式为`{ServiceName}FeignClient`
- **降级处理类**: 命名格式为`{ServiceName}FeignClientFallback`

### 5. 包结构规范
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

## 理由 (Justification)
此规则源于任务 `task_20250127_auth_feign_client_cleanup.md`。在该任务中，发现auth模块（服务提供方）和user模块（服务消费方）都定义了相同的AuthFeignClient，违反了微服务架构的单一职责原则。

## 违反示例
```java
// ❌ 错误：服务提供方定义了Feign客户端
@FeignClient(name = "service-auth", path = "/service/auth")
public interface AuthFeignClient {
    @PostMapping("/login")
    ResultData login(@RequestBody LoginRequest request);
}
```

## 最佳实践
1. **明确职责**: 服务提供方专注于API实现，消费方专注于服务调用
2. **避免重复**: 每个Feign客户端只在一个地方定义
3. **统一管理**: 所有Feign客户端都在消费方模块中管理
4. **版本控制**: 通过API版本控制来管理接口变更 
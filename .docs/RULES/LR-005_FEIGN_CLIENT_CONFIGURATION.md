# Feign客户端配置规范

**ID**: LR-005  
**Name**: Feign客户端配置规范  
**Status**: Active  
**创建时间**: 2025-08-01  
**重命名时间**: 2025-08-02 (从LR-003重命名)

## 规则名称: Feign客户端必须使用path属性避免Bean名称冲突

## 触发情景 (Context/Trigger)
当多个Feign客户端指向同一目标服务时，必须使用path属性避免Bean名称冲突。

## 指令 (Directive)

### 1. 基础配置规范
- **必须 (MUST)** 使用`path`属性指定基础路径
- **必须 (MUST)** 在`@RequestMapping`中省略path中已包含的路径前缀
- **必须 (MUST)** 在启动类中指定Feign客户端扫描包范围
- **禁止 (MUST NOT)** 在多个Feign客户端中使用相同的Bean名称

### 2. 启动类配置
```java
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.origin.{模块名}.feign"})  // 指定扫描包
public class {模块名}Application {
    public static void main(String[] args) {
        SpringApplication.run({模块名}Application.class, args);
    }
}
```

### 3. Feign客户端配置示例
```java
// ✅ 正确配置
@FeignClient(
    name = "service-auth", 
    path = "/auth", 
    fallback = AuthFeignClientFallback.class
)
public interface AuthFeignClient {
    @PostMapping("/validate")  // 不包含/auth前缀
    ResultData<Boolean> validateToken(@RequestParam("token") String token);
    
    @GetMapping("/user/info")  // 不包含/auth前缀
    ResultData<SysUser> getUserInfo(@RequestParam("userId") String userId);
}

// ❌ 错误配置 - 会导致Bean名称冲突
@FeignClient(name = "service-auth", fallback = AuthFeignClientFallback.class)
public interface AuthFeignClient {
    @PostMapping("/auth/validate")  // 包含重复前缀
    ResultData<Boolean> validateToken(@RequestParam("token") String token);
}
```

### 4. 命名规范
- **Bean名称**: 使用目标服务名称，如`service-auth`
- **接口名称**: 使用功能描述，如`AuthFeignClient`
- **路径配置**: 使用服务的基础路径，如`/auth`

## 理由 (Justification)
此规则源于任务 `task_20250801_2308_feign_conflict_resolution.md`。在该任务中，多个Feign客户端指向同一服务导致Bean名称冲突，出现以下错误：

1. **Invalid value type for attribute 'factoryBeanObjectType': java.lang.String**
2. **The bean 'service-auth.FeignClientSpecification' could not be registered. A bean with that name has already been defined and overriding is disabled.**

通过使用`path`属性避免URL重复，确保每个Feign客户端有唯一的Bean名称，解决了启动冲突问题。

## 性能基准
- **启动时间**: 目标 < 3秒（开发环境）
- **Bean冲突**: 目标 0个
- **Feign客户端**: 每个服务唯一Bean名称

## 常见问题与解决方案

### 问题1: Bean名称冲突
**症状**: 启动时报错"bean could not be registered"
**解决方案**: 使用`path`属性，避免在`@RequestMapping`中重复路径前缀

### 问题2: 扫描范围过大
**症状**: 扫描到不相关的Feign客户端
**解决方案**: 在`@EnableFeignClients`中指定`basePackages`

### 问题3: 依赖冲突
**症状**: 出现"Invalid value type for attribute"错误
**解决方案**: 排除不需要的自动配置，优化启动性能

## 相关规则
- **PERF-001**: Spring Boot启动性能优化规则
- **LR-006**: 微服务依赖排除规则

## 版本历史
| 版本 | 日期 | 变更内容 |
|------|------|----------|
| 1.0.0 | 2025-08-01 | 初始版本，基于任务经验萃取 |
| 1.1.0 | 2025-08-02 | 重命名为LR-005，解决规则ID冲突 | 
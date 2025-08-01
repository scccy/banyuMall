# 路由一致性规范

**ID**: LR-007  
**Name**: 路由一致性规范  
**Status**: Active  
**创建时间**: 2025-08-01  
**重命名时间**: 2025-08-02 (从LR-004重命名)

## 规则名称: Feign客户端路由必须与Controller路由保持一致

## 触发情景 (Context/Trigger)
当创建或修改Feign客户端时，必须确保其路由路径与目标服务的Controller路由完全一致。

## 指令 (Directive)

### 1. 基础配置规范
- **必须 (MUST)** Feign客户端的path属性与Controller的@RequestMapping路径一致
- **必须 (MUST)** Feign客户端的@PostMapping/@GetMapping路径不包含重复的前缀
- **必须 (MUST)** 所有路由都以/开头
- **必须 (MUST)** 设计文档中的路由设计与实际代码实现一致
- **禁止 (MUST NOT)** 在Feign客户端中使用与Controller不一致的路径

### 2. 路由命名规范
- **service-auth模块**: `/service/auth/+接口`
- **service-user模块**: `/service/user/+接口`
- **third-party-oss模块**: `/tp/oss/+接口`
- **core-publisher模块**: `/core/publisher/+接口`

### 3. 配置示例
```java
// Controller配置
@RestController
@RequestMapping("/service/auth")
public class AuthController {
    @PostMapping("/login")
    public ResultData login(@RequestBody LoginRequest request) { ... }
}

// ✅ 正确的Feign客户端配置
@FeignClient(name = "service-auth", path = "/service/auth", fallback = AuthFeignClientFallback.class)
public interface AuthFeignClient {
    @PostMapping("/login")  // 不包含/service/auth前缀
    ResultData login(@RequestBody LoginRequest request);
}

// ❌ 错误的Feign客户端配置
@FeignClient(name = "service-auth", path = "/auth", fallback = AuthFeignClientFallback.class)
public interface AuthFeignClient {
    @PostMapping("/login")  // 路径不匹配Controller
    ResultData login(@RequestBody LoginRequest request);
}
```

### 4. 文档一致性要求
- **设计文档**: 模块设计.md中的路由配置必须与实际代码一致
- **API测试文档**: api-test.md中的接口路径必须与实际代码一致
- **Feign客户端文档**: 确保服务名称和路径配置正确

## 理由 (Justification)
此规则源于任务 `task_20250801_2316_route_consistency_check.md`。在该任务中，发现多个Feign客户端路由与Controller路由不一致，导致服务调用失败和启动错误。

**具体问题**:
1. **service-auth模块**: Feign客户端路径`/auth`与Controller路径`/service/auth`不一致
2. **service-user模块**: Feign客户端路径`/user`与Controller路径`/service/user`不一致
3. **third-party-oss模块**: Feign客户端路径`/api/v1/oss`与Controller路径`/tp/oss`不一致
4. **core-publisher模块**: 设计文档路径`/core-publisher`与实际代码`/core/publisher`不一致

## 验证清单
- [ ] Feign客户端path属性与Controller @RequestMapping一致
- [ ] Feign客户端方法路径不包含重复前缀
- [ ] 所有路由都以/开头
- [ ] 设计文档路由与实际代码一致
- [ ] API测试文档路由与实际代码一致
- [ ] yml配置文件根路径配置正确

## 常见问题与解决方案

### 问题1: 路径前缀重复
**症状**: Feign客户端方法路径包含与path属性重复的前缀
**解决方案**: 移除方法路径中的重复前缀

### 问题2: 服务名称不一致
**症状**: Feign客户端name属性与实际服务名称不匹配
**解决方案**: 使用实际的服务名称，如`aliyun-oss`而不是`third-party-oss`

### 问题3: 文档与实际代码不一致
**症状**: 设计文档中的路由配置与实际代码实现不同
**解决方案**: 同步更新设计文档和API测试文档

## 相关规则
- **LR-005**: Feign客户端配置规范
- **DEV-007**: 网关接口同步规则
- **LR-003**: 模块设计与文档标准化规则

## 版本历史
| 版本 | 日期 | 变更内容 |
|------|------|----------|
| 1.0.0 | 2025-08-01 | 初始版本，基于任务经验萃取 |
| 1.1.0 | 2025-08-02 | 重命名为LR-007，解决规则ID冲突 | 
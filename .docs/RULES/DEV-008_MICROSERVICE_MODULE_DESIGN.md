# 微服务模块设计规则

**ID**: DEV-008  
**Name**: 微服务模块设计规则  
**Status**: Active  
**创建时间**: 2025-07-31  

## 触发情景 (Context/Trigger)
当设计微服务架构的模块划分时，需要明确各模块的职责和依赖关系。

## 指令 (Directive)

### 1. 模块职责划分
- **必须 (MUST)** 按照层次结构划分模块职责
- **必须 (MUST)** 将Spring配置类放在基础层模块中
- **必须 (MUST)** 将通用工具类放在通用层模块中
- **禁止 (MUST NOT)** 在通用工具模块中放置Spring配置类

### 2. 模块层次结构
```
service-base (Spring基础层)
├── Spring Boot相关依赖
├── Spring配置类 (WebMvcConfig, RedisConfig, MyBatisPlusConfig等)
├── 基础注解和配置
├── 数据源相关配置
└── 异常处理类 (GlobalExceptionHandler, BusinessException, ErrorCode)

service-common (通用工具层)
├── 通用工具类 (util包)
├── 通用实体类 (entity包)
├── 通用DTO (dto包)
└── 通用返回结果 (ResultData)

service-xxx (业务服务层)
├── 业务逻辑
├── 业务实体
├── 业务服务
└── 业务控制器
```

### 3. 依赖关系规则
- **必须 (MUST)** Spring MVC业务服务依赖service-base和service-common
- **必须 (MUST)** WebFlux服务（如Gateway）只依赖service-common，不依赖service-base
- **必须 (MUST)** service-common不依赖service-base
- **必须 (MUST)** service-base包含所有Spring Boot基础依赖
- **禁止 (MUST NOT)** 在service-common中包含Spring配置相关依赖
- **禁止 (MUST NOT)** WebFlux服务依赖包含Spring MVC配置的模块

### 4. 技术栈分离原则
- **必须 (MUST)** 严格分离Spring MVC和Spring WebFlux的依赖
- **必须 (MUST)** Gateway服务使用WebFlux技术栈，不引入MVC依赖
- **必须 (MUST)** 业务服务使用MVC技术栈，依赖service-base
- **禁止 (MUST NOT)** 在同一服务中混合使用MVC和WebFlux

### 5. 配置类管理
- **必须 (MUST)** 将所有Spring MVC配置类放在service-base模块中
- **必须 (MUST)** 将WebFlux配置类放在对应的WebFlux服务模块中
- **必须 (MUST)** 配置类按功能分类组织
- **必须 (MUST)** 避免配置类之间的循环依赖
- **禁止 (MUST NOT)** 在业务服务中重复定义基础配置

### 6. 依赖排除策略
- **必须 (MUST)** 在不需要特定功能的模块中排除相关依赖
- **必须 (MUST)** 使用exclusion标签排除冲突的依赖
- **必须 (MUST)** 在Gateway服务中排除Spring Boot Web依赖
- **必须 (MUST)** 在WebFlux服务中排除MVC相关依赖

## 理由 (Justification)
此规则源于任务 `task_20250731_1201_module_refactor.md`。在该任务中，发现service-base模块为空而service-common模块包含Spring配置类，导致模块职责混乱，依赖关系不正确，影响了微服务架构的清晰性和可维护性。同时发现Gateway服务依赖service-base会导致MVC和WebFlux技术栈冲突。

## 最佳实践
1. **模块命名**: 使用清晰的命名约定，如service-base、service-common、service-auth等
2. **职责单一**: 每个模块只负责一个明确的职责
3. **依赖最小化**: 只包含模块真正需要的依赖
4. **配置集中**: 将Spring配置类集中管理在基础模块中
5. **版本管理**: 统一管理模块版本，避免版本冲突
6. **技术栈分离**: 严格区分MVC和WebFlux技术栈的依赖

## 检查清单
- [ ] 模块职责是否清晰明确
- [ ] 依赖关系是否正确
- [ ] Spring配置类是否在正确位置
- [ ] 是否排除了不必要的依赖
- [ ] 模块间是否有循环依赖
- [ ] WebFlux服务是否避免了MVC依赖
- [ ] 技术栈是否严格分离 
# 单一职责原则与类设计规则

**ID**: DEV-009  
**Name**: 单一职责原则与类设计规则  
**Status**: Active  
**创建时间**: 2025-07-31  

## 触发情景 (Context/Trigger)
当发现类承担过多职责，违反单一职责原则时。

## 指令 (Directive)

### 1. 职责识别
- **必须 (MUST)** 识别类的所有职责
- **必须 (MUST)** 判断职责是否相关
- **禁止 (MUST NOT)** 一个类承担多个不相关的职责

### 2. 拆分原则
- **必须 (MUST)** 按功能职责拆分类
- **必须 (MUST)** 使用组合优于继承
- **必须 (MUST)** 保持向后兼容性
- **禁止 (MUST NOT)** 过度拆分导致类过多

### 3. 设计模式
- **必须 (MUST)** 优先使用组合模式
- **必须 (MUST)** 提供统一的接口
- **必须 (MUST)** 保持API的一致性

### 4. 重构流程
- **必须 (MUST)** 遵循文档驱动开发流程
- **必须 (MUST)** 更新相关基线文档
- **必须 (MUST)** 进行经验萃取和规则沉淀

## 理由 (Justification)
此规则源于任务 `task_20250731_1201_resultdata_refactor_redo.md`。在该任务中，ResultData类承担了响应数据管理、链路追踪、API包装等多个职责，违反了单一职责原则，通过拆分和组合模式解决了问题。

## 最佳实践

### 1. 职责分析
- 明确每个类的单一职责
- 识别职责之间的依赖关系
- 评估职责的复杂度和变化频率

### 2. 组合设计
- 使用组合模式实现复杂功能
- 避免深度继承层次
- 保持组件间的松耦合

### 3. 接口统一
- 提供一致的API接口
- 保持方法命名的一致性
- 确保向后兼容性

### 4. 重构验证
- 验证拆分后的职责是否清晰
- 测试组合功能的正确性
- 确认API接口的一致性

## 检查清单

### 重构前检查
- [ ] 类职责是否单一明确
- [ ] 是否存在职责混杂
- [ ] 类的大小是否合理
- [ ] 方法数量是否过多

### 重构中检查
- [ ] 是否使用了组合模式
- [ ] API接口是否一致
- [ ] 是否保持了向后兼容性
- [ ] 拆分是否合理

### 重构后检查
- [ ] 职责是否清晰分离
- [ ] 组合功能是否正确
- [ ] 性能是否受到影响
- [ ] 文档是否同步更新

## 示例

### 重构前（违反单一职责原则）
```java
public class ResultData<T> {
    // 响应数据字段
    private Integer code;
    private String message;
    private T data;
    
    // 链路追踪字段（职责混杂）
    private String requestId;
    private Long userId;
    private String clientIp;
    private String userAgent;
    
    // 混合了响应管理和链路追踪的方法
    public static <T> ResultData<T> success(T data, String requestId, Long userId) {
        // 同时处理响应数据和链路追踪
    }
}
```

### 重构后（遵循单一职责原则）
```java
// 职责1：响应数据管理
public class ResultData<T> {
    private Integer code;
    private String message;
    private T data;
    
    public static <T> ResultData<T> success(T data) {
        // 只处理响应数据
    }
}

// 职责2：链路追踪管理
public class RequestTrace {
    private String requestId;
    private Long userId;
    private String clientIp;
    private String userAgent;
    
    public static RequestTrace create(String requestId, Long userId) {
        // 只处理链路追踪
    }
}

// 职责3：API响应包装（组合模式）
public class ApiResponse<T> {
    private ResultData<T> result;
    private RequestTrace trace;
    
    public static <T> ApiResponse<T> success(T data, RequestTrace trace) {
        // 组合响应数据和链路追踪
    }
}
```

## 相关规则
- **DEV-001**: 编码规范规则
- **DEV-008**: 微服务模块设计规则

## 更新历史
- **2025-07-31**: 初始创建，基于ResultData重构经验 
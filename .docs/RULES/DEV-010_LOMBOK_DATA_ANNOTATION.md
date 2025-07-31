# 开发规则：实体类使用 @Data 注解规范

**ID**: DEV-010  
**Name**: Lombok @Data 注解使用规范  
**Status**: Active  
**创建时间**: 2025-07-31  

## 触发情景 (Context/Trigger)
当创建或修改实体类、DTO类、VO类等数据传输对象时，需要定义类的结构和访问方法。

## 指令 (Directive)

### 1. 基本使用规范
- **必须 (MUST)** 在实体类、DTO类、VO类上使用 `@Data` 注解
- **必须 (MUST)** 配合 `@Accessors(chain = true)` 注解使用，支持链式调用
- **禁止 (MUST NOT)** 手动编写 getter/setter 方法，避免代码重复

### 2. 注解组合规范
```java
@Data
@Accessors(chain = true)
public class EntityClass {
    // 字段定义
}
```

### 3. 字段定义规范
- **必须 (MUST)** 为所有字段添加适当的注释说明
- **必须 (MUST)** 使用合适的访问修饰符（通常为 private）
- **建议 (SHOULD)** 为重要字段添加验证注解（如 `@NotBlank`、`@NotNull` 等）

### 4. 继承和组合规范
- **建议 (SHOULD)** 优先使用组合模式而非继承模式
- **必须 (MUST)** 当需要复用其他类的功能时，使用组合模式
- **禁止 (MUST NOT)** 在实体类中使用继承来复用功能

### 5. 特殊情况处理
- **必须 (MUST)** 当需要自定义 getter/setter 逻辑时，手动编写方法并添加 `@Override` 注解
- **必须 (MUST)** 当需要添加业务方法时，在类中直接定义，不受 @Data 影响

## 理由 (Justification)
此规则源于任务 `task_20250731_1529_lombok_data_annotation_rule.md`。在该任务中，发现 `LoginRequest` 类与 `RequestTrace` 类存在重复的链路追踪字段，通过使用 `@Data` 注解和组合模式，成功解决了代码重复问题，提高了代码的简洁性和可维护性。

## 最佳实践示例

### 正确的使用方式：
```java
@Data
@Accessors(chain = true)
public class LoginRequest {
    
    /**
     * 请求追踪信息
     */
    private RequestTrace trace = RequestTrace.create();
    
    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    private String username;
    
    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;
}
```

### 组合模式示例：
```java
@Data
@Accessors(chain = true)
public class UserProfile {
    
    /**
     * 请求追踪信息
     */
    private RequestTrace trace = RequestTrace.create();
    
    /**
     * 用户基本信息
     */
    private UserBasicInfo basicInfo;
    
    /**
     * 用户扩展信息
     */
    private UserExtendedInfo extendedInfo;
}
```

### 自定义方法示例：
```java
@Data
@Accessors(chain = true)
public class OrderRequest {
    
    private List<OrderItem> items;
    private BigDecimal totalAmount;
    
    /**
     * 自定义方法：计算订单总金额
     */
    public BigDecimal calculateTotalAmount() {
        return items.stream()
            .map(OrderItem::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    /**
     * 重写 setter 方法，添加业务逻辑
     */
    @Override
    public OrderRequest setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
        // 添加业务逻辑：验证金额
        if (totalAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("订单金额不能为负数");
        }
        return this;
    }
}
```

## 性能考虑
- **优势**: 减少样板代码，提高开发效率
- **注意**: 在编译时生成代码，不影响运行时性能
- **建议**: 在 IDE 中启用 Lombok 插件以获得最佳开发体验

## 相关规则
- DEV-009: 单一职责原则
- DEV-008: 微服务模块设计规范
- API-DOC-001: API 文档规范 
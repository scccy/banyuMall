# 规则名称: 分层架构设计原则 - Controller层职责边界

**ID**: LR-003  
**Name**: 分层架构设计原则  
**Status**: Active  
**创建时间**: 2025-08-02  

## 触发情景 (Context/Trigger)
当Controller层直接使用工具类、数据访问层组件或复杂的业务逻辑时。

## 指令 (Directive)

### 1. Controller层职责
- **必须 (MUST)** Controller层只负责HTTP请求的接收和响应
- **必须 (MUST)** Controller层不直接使用业务工具类（如JwtTokenManager、JwtUtil等）
- **必须 (MUST)** Controller层不直接操作数据库或缓存
- **必须 (MUST)** Controller层通过Service层处理业务逻辑
- **禁止 (MUST NOT)** 在Controller层编写复杂的业务逻辑
- **禁止 (MUST NOT)** Controller层处理序列化配置（FastJSON2配置在base包统一处理）

### 2. Service层职责
- **必须 (MUST)** Service层负责所有业务逻辑的处理
- **必须 (MUST)** Service层可以调用工具类、数据访问层等
- **必须 (MUST)** Service层处理事务管理
- **必须 (MUST)** Service层处理异常转换和业务异常
- **必须 (MUST)** Service层接口定义清晰，职责单一

### 3. 依赖注入原则
- **必须 (MUST)** Controller层只注入Service接口
- **必须 (MUST)** Service层注入所需的工具类和数据访问层
- **禁止 (MUST NOT)** Controller层直接注入工具类或Mapper
- **禁止 (MUST NOT)** 跨层直接调用，必须通过接口调用

### 4. 异常处理原则
- **必须 (MUST)** Service层专注于核心业务逻辑，抛出业务异常
- **必须 (MUST)** base包处理基础异常（参数校验、运行时异常等）
- **必须 (MUST)** 每个微服务模块创建自己的@RestControllerAdvice处理业务异常
- **必须 (MUST)** Controller层不处理异常，专注于请求响应
- **必须 (MUST)** 异常信息要明确，便于调试和用户理解
- **必须 (MUST)** Service层异常继承BusinessException或RuntimeException
- **禁止 (MUST NOT)** 在base包中添加特定业务的异常处理逻辑

### 5. 数据传输原则
- **必须 (MUST)** Controller层使用DTO接收和返回数据
- **必须 (MUST)** Service层使用Entity进行业务处理
- **必须 (MUST)** 避免在Controller层直接操作Entity对象

## 理由 (Justification)
此规则源于任务 `task_20250801_2340_cache_configuration_fix.md`。在该任务中，发现AuthController直接使用JwtTokenManager，违反了分层架构原则。Controller层应该只负责HTTP请求处理，业务逻辑应该在Service层处理。这样可以提高代码的可维护性、可测试性和可扩展性。

## 技术细节

### 正确的架构示例
```java
// Controller层 - 只负责HTTP处理
@RestController
public class AuthController {
    private final AuthService authService; // 只注入Service
    
    @PostMapping("/logout")
    public ResultData<String> logout(HttpServletRequest request) {
        String token = extractToken(request);
        authService.logout(token); // 调用Service层，异常由@RestControllerAdvice处理
        return ResultData.ok("登出成功");
    }
}

// Service层 - 专注于核心业务逻辑
@Service
public class AuthServiceImpl implements AuthService {
    private final JwtTokenManager jwtTokenManager; // 工具类在Service层使用
    
    @Override
    public void logout(String token) {
        // 核心业务逻辑处理
        String userId = jwtUtil.getUserIdFromToken(token);
        jwtTokenManager.removeUserToken(userId);
        jwtTokenManager.addToBlacklist(token, expirationTime);
    }
}

// base包异常处理 - 处理基础异常
@RestControllerAdvice
public class BaseExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResultData<Object> handleValidationException(MethodArgumentNotValidException e) {
        return ResultData.fail("参数校验失败", e.getMessage());
    }
    
    @ExceptionHandler(RuntimeException.class)
    public ResultData<Object> handleRuntimeException(RuntimeException e) {
        return ResultData.fail("系统异常", e.getMessage());
    }
}

// 微服务模块异常处理 - 处理业务异常
@RestControllerAdvice
public class AuthExceptionHandler {
    @ExceptionHandler(AuthException.class)
    public ResultData<Object> handleAuthException(AuthException e) {
        return ResultData.fail(e.getCode(), e.getMessage());
    }
    
    @ExceptionHandler(UserNotFoundException.class)
    public ResultData<Object> handleUserNotFoundException(UserNotFoundException e) {
        return ResultData.fail("用户不存在", e.getMessage());
    }
}
```

### 错误的架构示例
```java
// ❌ 错误：Controller层直接使用工具类
@RestController
public class AuthController {
    private final JwtTokenManager jwtTokenManager; // 不应该直接注入
    
    @PostMapping("/logout")
    public ResultData<String> logout(HttpServletRequest request) {
        // ❌ 业务逻辑在Controller层
        jwtTokenManager.removeUserToken(userId);
        return ResultData.ok("登出成功");
    }
}
```

## 检查清单
- [ ] Controller层是否只注入了Service接口？
- [ ] Controller层是否没有直接使用业务工具类？
- [ ] 业务逻辑是否都在Service层处理？
- [ ] Service层是否专注于核心业务逻辑？
- [ ] base包是否处理基础异常？
- [ ] 每个模块是否有自己的@RestControllerAdvice？
- [ ] 数据传输是否使用了DTO？
- [ ] 缓存操作是否在Service层进行？ 
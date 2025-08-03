# DTO统一管理规范

**ID**: LR-009  
**Name**: DTO统一管理规范  
**Status**: Active  
**创建时间**: 2025-01-27

## 规则名称: 跨模块使用的DTO必须统一到common模块管理

## 触发情景 (Context/Trigger)
当多个模块需要使用相同的DTO进行数据传输时，必须将DTO统一到common模块管理，避免重复定义和循环依赖。

## 指令 (Directive)

### 1. DTO分类管理
- **模块内部DTO**: 仅在单个模块内部使用的DTO，保留在模块内部
- **跨模块DTO**: 多个模块需要使用的DTO，必须移动到common模块
- **API接口DTO**: 对外API接口的请求响应DTO，建议放在common模块

### 2. 统一管理原则
- **必须 (MUST)** 将跨模块使用的DTO放在common模块
- **必须 (MUST)** 避免模块间的循环依赖
- **必须 (MUST)** 保持DTO的向后兼容性
- **禁止 (MUST NOT)** 在多个模块中重复定义相同的DTO

### 3. 常见跨模块DTO类型

#### 3.1 认证相关DTO
```java
// ✅ 正确：放在common模块
package com.origin.common.dto;

public class LoginRequest {
    private String username;
    private String password;
    // ...
}

public class LoginResponse {
    private String userId;
    private String token;
    // ...
}
```

#### 3.2 密码管理DTO
```java
// ✅ 正确：放在common模块
package com.origin.common.dto;

public class PasswordEncryptRequest {
    private String username;
    private String password;
}

public class PasswordEncryptResponse {
    private String encryptedPassword;
}
```

#### 3.3 文件上传DTO
```java
// ✅ 正确：放在common模块
package com.origin.common.dto;

public class FileUploadRequest {
    private MultipartFile file;
    private String businessType;
    // ...
}

public class FileUploadResponse {
    private String accessUrl;
    private String fileId;
    // ...
}
```

### 4. 模块内部DTO示例

#### 4.1 用户模块内部DTO
```java
// ✅ 正确：仅在user模块内部使用
package com.origin.user.dto;

public class UserCreateRequest {
    private String username;
    private String phone;
    // ...
}

public class UserUpdateRequest {
    private String nickname;
    private String email;
    // ...
}
```

#### 4.2 认证模块内部DTO
```java
// ✅ 正确：仅在auth模块内部使用
package com.origin.auth.dto;

public class UserInfoResponse {
    private String userId;
    private String username;
    // ...
}
```

### 5. 迁移流程

#### 5.1 识别跨模块DTO
1. **分析使用场景**: 检查DTO在哪些模块中被使用
2. **识别依赖关系**: 确认是否存在循环依赖
3. **评估影响范围**: 分析迁移对现有代码的影响

#### 5.2 迁移步骤
1. **创建common模块DTO**: 在common模块中创建新的DTO
2. **更新import语句**: 修改所有使用该DTO的模块的import
3. **删除原DTO**: 删除原模块中的重复DTO
4. **测试验证**: 确保所有功能正常工作

#### 5.3 迁移示例
```bash
# 迁移前
service-auth/dto/LoginRequest.java
service-auth/dto/LoginResponse.java

# 迁移后
service-common/dto/LoginRequest.java
service-common/dto/LoginResponse.java

# 更新import
- import com.origin.auth.dto.LoginRequest;
+ import com.origin.common.dto.LoginRequest;
```

### 6. 包结构规范
```
service-common/
└── src/main/java/com/origin/common/dto/
    ├── LoginRequest.java          # 登录请求
    ├── LoginResponse.java         # 登录响应
    ├── PasswordEncryptRequest.java # 密码加密请求
    ├── PasswordEncryptResponse.java # 密码加密响应
    ├── FileUploadRequest.java     # 文件上传请求
    ├── FileUploadResponse.java    # 文件上传响应
    ├── ResultData.java           # 统一响应格式
    └── RequestTrace.java         # 请求追踪
```

## 理由 (Justification)
此规则源于任务 `task_20250127_login_dto_unification.md`。在该任务中，发现LoginRequest和LoginResponse在auth模块和user模块中都有使用，存在潜在的循环依赖风险。通过将DTO统一到common模块，实现了更好的架构设计和代码复用。

## 违反示例
```java
// ❌ 错误：在多个模块中重复定义
// service-auth/dto/LoginRequest.java
public class LoginRequest {
    private String username;
    private String password;
}

// service-user/dto/LoginRequest.java (重复定义)
public class LoginRequest {
    private String username;
    private String password;
}
```

## 最佳实践
1. **统一管理**: 所有跨模块DTO都在common模块管理
2. **版本控制**: 保持DTO的向后兼容性
3. **文档维护**: 及时更新DTO的使用说明
4. **测试覆盖**: 确保DTO变更后的功能测试
5. **依赖管理**: 避免模块间的循环依赖 
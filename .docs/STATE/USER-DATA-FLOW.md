# 用户服务数据流向与流程图

## 概述
本文档描述service-user模块的数据流向、表关系、Java类映射关系以及业务流程。

## 数据表关系图

### 表结构关系
```mermaid
erDiagram
    sys_user ||--o{ user_profile : "1:1"
    sys_user ||--o{ user_config : "1:N"
    
    sys_user {
        string id PK
        string username UK
        string password
        string nickname
        string avatar
        string email
        string phone
        int gender
        date birthday
        int status
        int user_type
        datetime last_login_time
        string create_by
        datetime create_time
        string update_by
        datetime update_time
        int is_deleted
    }
    
    user_profile {
        string id PK
        string user_id FK
        string real_name
        string company_name
        string company_address
        string contact_person
        string contact_phone
        string industry
        text description
        string create_by
        datetime create_time
        string update_by
        datetime update_time
        int is_deleted
    }
    
    user_config {
        string id PK
        string user_id FK
        string config_key
        text config_value
        string config_type
        string create_by
        datetime create_time
        string update_by
        datetime update_time
        int is_deleted
    }
```

## Java类映射关系

### 实体类映射
```mermaid
classDiagram
    BaseEntity <|-- SysUser
    BaseEntity <|-- UserProfile
    BaseEntity <|-- UserConfig
    
    class BaseEntity {
        +String createBy
        +LocalDateTime createTime
        +String updateBy
        +LocalDateTime updateTime
        +Integer isDeleted
    }
    
    class SysUser {
        +String id
        +String username
        +String password
        +String nickname
        +String avatar
        +String email
        +String phone
        +Integer gender
        +LocalDate birthday
        +Integer status
        +Integer userType
        +LocalDateTime lastLoginTime
    }
    
    class UserProfile {
        +String id
        +String userId
        +String realName
        +String companyName
        +String companyAddress
        +String contactPerson
        +String contactPhone
        +String industry
        +String description
    }
    
    class UserConfig {
        +String id
        +String userId
        +String configKey
        +String configValue
        +String configType
    }
```

### 分层架构映射
```mermaid
classDiagram
    Controller --> Service
    Service --> Mapper
    Mapper --> Entity
    
    class UserProfileController {
        +getUserProfile(userId)
        +createOrUpdateProfile(userId, request)
    }
    
    class UserConfigController {
        +getUserConfigs(userId)
        +getUserConfigMap(userId)
        +getUserConfigValue(userId, configKey)
        +setUserConfig(userId, configKey, configValue)
        +setUserConfigs(userId, configMap)
        +deleteUserConfig(userId, configKey)
    }
    
    class UserProfileService {
        +getByUserId(userId)
        +createOrUpdateProfile(userId, profile)
    }
    
    class UserConfigService {
        +getByUserId(userId)
        +getConfigMap(userId)
        +getConfigValue(userId, configKey)
        +setConfig(userId, configKey, configValue, configType)
        +setConfigs(userId, configMap)
    }
    
    class UserProfileMapper {
        +selectByUserId(userId)
        +insert(profile)
        +updateById(profile)
    }
    
    class UserConfigMapper {
        +selectByUserId(userId)
        +selectByUserIdAndKey(userId, configKey)
        +insert(config)
        +updateById(config)
        +deleteById(id)
    }
```

## 数据流向图

### 用户扩展信息管理流程
```mermaid
sequenceDiagram
    participant Client as 客户端
    participant Controller as UserProfileController
    participant Service as UserProfileService
    participant Mapper as UserProfileMapper
    participant DB as 数据库
    
    Note over Client,DB: 获取用户扩展信息流程
    Client->>Controller: GET /user/profile/{userId}
    Controller->>Service: getByUserId(userId)
    Service->>Mapper: selectByUserId(userId)
    Mapper->>DB: SELECT * FROM user_profile WHERE user_id = ?
    DB-->>Mapper: 返回用户扩展信息
    Mapper-->>Service: 返回UserProfile对象
    Service-->>Controller: 返回UserProfile对象
    Controller-->>Client: 返回ResultData<UserProfile>
    
    Note over Client,DB: 创建或更新用户扩展信息流程
    Client->>Controller: POST /user/profile/{userId}
    Controller->>Service: createOrUpdateProfile(userId, profile)
    Service->>Mapper: selectByUserId(userId)
    Mapper->>DB: SELECT * FROM user_profile WHERE user_id = ?
    DB-->>Mapper: 返回现有信息（如果有）
    
    alt 存在扩展信息
        Service->>Mapper: updateById(profile)
        Mapper->>DB: UPDATE user_profile SET ... WHERE id = ?
    else 不存在扩展信息
        Service->>Mapper: save(profile)
        Mapper->>DB: INSERT INTO user_profile (...)
    end
    
    DB-->>Mapper: 操作结果
    Mapper-->>Service: 返回操作结果
    Service-->>Controller: 返回操作结果
    Controller-->>Client: 返回ResultData<Boolean>
```

### 用户配置管理流程
```mermaid
sequenceDiagram
    participant Client as 客户端
    participant Controller as UserConfigController
    participant Service as UserConfigService
    participant Mapper as UserConfigMapper
    participant DB as 数据库
    
    Note over Client,DB: 获取用户配置流程
    Client->>Controller: GET /user/config/{userId}
    Controller->>Service: getByUserId(userId)
    Service->>Mapper: selectByUserId(userId)
    Mapper->>DB: SELECT * FROM user_config WHERE user_id = ?
    DB-->>Mapper: 返回配置列表
    Mapper-->>Service: 返回List<UserConfig>
    Service-->>Controller: 返回List<UserConfig>
    Controller-->>Client: 返回ResultData<List<UserConfig>>
    
    Note over Client,DB: 设置用户配置流程
    Client->>Controller: POST /user/config/{userId}/{configKey}
    Controller->>Service: setConfig(userId, configKey, configValue, configType)
    Service->>Mapper: selectByUserIdAndKey(userId, configKey)
    Mapper->>DB: SELECT * FROM user_config WHERE user_id = ? AND config_key = ?
    DB-->>Mapper: 返回现有配置（如果有）
    
    alt 配置已存在
        Service->>Mapper: updateById(config)
        Mapper->>DB: UPDATE user_config SET config_value = ? WHERE id = ?
    else 配置不存在
        Service->>Mapper: save(newConfig)
        Mapper->>DB: INSERT INTO user_config (...)
    end
    
    DB-->>Mapper: 操作结果
    Mapper-->>Service: 返回操作结果
    Service-->>Controller: 返回操作结果
    Controller-->>Client: 返回ResultData<Boolean>
```

## 业务流程图

### 用户信息管理整体流程
```mermaid
flowchart TD
    A[客户端请求] --> B{请求类型}
    
    B -->|扩展信息| C[UserProfileController]
    B -->|配置信息| D[UserConfigController]
    
    C --> E{操作类型}
    E -->|查询| F[getUserProfile]
    E -->|创建/更新| G[createOrUpdateProfile]
    
    D --> H{操作类型}
    H -->|查询| I[getUserConfigs/getUserConfigMap]
    H -->|设置| J[setUserConfig/setUserConfigs]
    H -->|删除| K[deleteUserConfig]
    
    F --> L[UserProfileService.getByUserId]
    G --> M[UserProfileService.createOrUpdateProfile]
    
    I --> N[UserConfigService.getByUserId/getConfigMap]
    J --> O[UserConfigService.setConfig/setConfigs]
    K --> P[UserConfigService.removeById]
    
    L --> Q[UserProfileMapper.selectByUserId]
    M --> R{是否存在}
    R -->|是| S[UserProfileMapper.updateById]
    R -->|否| T[UserProfileMapper.insert]
    
    N --> U[UserConfigMapper.selectByUserId]
    O --> V{配置是否存在}
    V -->|是| W[UserConfigMapper.updateById]
    V -->|否| X[UserConfigMapper.insert]
    
    P --> Y[UserConfigMapper.deleteById]
    
    Q --> Z[数据库查询]
    S --> Z
    T --> Z
    U --> Z
    W --> Z
    X --> Z
    Y --> Z
    
    Z --> AA[返回结果]
    AA --> BB[ResultData包装]
    BB --> CC[返回客户端]
```

### 数据验证和错误处理流程
```mermaid
flowchart TD
    A[接收请求] --> B[参数验证]
    B --> C{验证通过?}
    
    C -->|否| D[返回参数错误]
    C -->|是| E[业务逻辑处理]
    
    E --> F{业务处理成功?}
    F -->|否| G[返回业务错误]
    F -->|是| H[数据库操作]
    
    H --> I{数据库操作成功?}
    I -->|否| J[返回数据库错误]
    I -->|是| K[返回成功结果]
    
    D --> L[统一异常处理]
    G --> L
    J --> L
    K --> L
    
    L --> M[ResultData包装]
    M --> N[返回客户端]
```

## 关键数据流转说明

### 1. 用户扩展信息流转
1. **查询流程**: 客户端 → Controller → Service → Mapper → 数据库 → 返回UserProfile对象
2. **更新流程**: 客户端 → Controller → Service → 检查是否存在 → 更新/创建 → 数据库操作

### 2. 用户配置流转
1. **查询流程**: 客户端 → Controller → Service → Mapper → 数据库 → 返回配置列表/Map
2. **设置流程**: 客户端 → Controller → Service → 检查配置是否存在 → 更新/创建 → 数据库操作

### 3. 数据一致性保证
- 使用事务管理确保数据一致性
- 逻辑删除保证数据完整性
- 公共字段自动填充（创建人、更新人等）

### 4. 性能优化
- 使用MyBatis-Plus的批量操作
- 配置缓存机制
- 数据库索引优化 
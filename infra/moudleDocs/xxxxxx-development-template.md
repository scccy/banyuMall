# 模块开发文档模板

## 文档说明
本文档根据设计模板生成，用于指导具体的代码实现。包含所需文件列表、类方法说明、数据流图和实现逻辑。

## 1. 所需文件列表

### 1.1 Controller层文件
| 文件名 | 路径 | 说明 |
|--------|------|------|
| {ModuleName}Controller.java | src/main/java/com/origin/{module}/controller/ | 控制器类，处理HTTP请求 |
| {ModuleName}ConfigController.java | src/main/java/com/origin/{module}/controller/ | 配置相关控制器（如需要） |

### 1.2 Service层文件
| 文件名 | 路径 | 说明 |
|--------|------|------|
| {ModuleName}Service.java | src/main/java/com/origin/{module}/service/ | 服务接口 |
| {ModuleName}ServiceImpl.java | src/main/java/com/origin/{module}/service/impl/ | 服务实现类 |

### 1.3 Mapper层文件
| 文件名 | 路径 | 说明 |
|--------|------|------|
| {ModuleName}Mapper.java | src/main/java/com/origin/{module}/mapper/ | MyBatis-Plus Mapper接口 |
| {ModuleName}Mapper.xml | src/main/resources/mapper/ | MyBatis XML映射文件（如需要） |

### 1.4 Entity层文件
| 文件名 | 路径 | 说明 |
|--------|------|------|
| {ModuleName}.java | src/main/java/com/origin/{module}/entity/ | 实体类 |
| {ModuleName}Profile.java | src/main/java/com/origin/{module}/entity/ | 扩展信息实体类（如需要） |

### 1.5 DTO层文件
| 文件名 | 路径 | 说明 |
|--------|------|------|
| {ModuleName}CreateRequest.java | src/main/java/com/origin/{module}/dto/ | 创建请求DTO |
| {ModuleName}UpdateRequest.java | src/main/java/com/origin/{module}/dto/ | 更新请求DTO |
| {ModuleName}QueryRequest.java | src/main/java/com/origin/{module}/dto/ | 查询请求DTO |

### 1.6 Feign客户端文件
| 文件名 | 路径 | 说明 |
|--------|------|------|
| {ModuleName}FeignClient.java | src/main/java/com/origin/{module}/feign/ | Feign客户端接口 |
| {ModuleName}FeignClientFallback.java | src/main/java/com/origin/{module}/feign/ | Feign降级处理类 |

## 2. 类方法说明

### 2.1 {ModuleName}Controller类
| 方法名 | 参数 | 返回值 | 功能说明 |
|--------|------|--------|----------|
| create{ModuleName}() | {ModuleName}CreateRequest, HttpServletRequest | ResultData<{ModuleName}> | 创建{模块}，验证参数，调用Service层创建逻辑 |
| get{ModuleName}Info() | String id, HttpServletRequest | ResultData<{ModuleName}> | 根据ID获取{模块}信息，调用Service层查询逻辑 |
| update{ModuleName}() | String id, {ModuleName}UpdateRequest, HttpServletRequest | ResultData<{ModuleName}> | 更新{模块}信息，验证参数，调用Service层更新逻辑 |
| delete{ModuleName}() | String id, HttpServletRequest | ResultData<String> | 删除{模块}，调用Service层软删除逻辑 |
| get{ModuleName}List() | {ModuleName}QueryRequest, HttpServletRequest | ResultData<IPage<{ModuleName}>> | 分页查询{模块}列表，支持多条件筛选 |
| batchDelete{ModuleName}s() | List<String> ids, HttpServletRequest | ResultData<String> | 批量删除{模块}，调用Service层批量删除逻辑 |

### 2.2 {ModuleName}Service接口
| 方法名 | 参数 | 返回值 | 功能说明 |
|--------|------|--------|----------|
| create{ModuleName}() | {ModuleName}CreateRequest | {ModuleName} | 创建{模块}业务逻辑，包含参数验证、数据转换、保存操作 |
| get{ModuleName}ById() | String id | {ModuleName} | 根据ID查询{模块}，包含缓存处理 |
| update{ModuleName}() | String id, {ModuleName}UpdateRequest | {ModuleName} | 更新{模块}业务逻辑，包含数据验证、更新操作 |
| delete{ModuleName}() | String id | boolean | 软删除{模块}，设置删除标记 |
| get{ModuleName}Page() | {ModuleName}QueryRequest | IPage<{ModuleName}> | 分页查询{模块}，支持多条件筛选和排序 |
| batchDelete{ModuleName}s() | List<String> ids | int | 批量删除{模块}，返回成功删除数量 |

### 2.3 {ModuleName}ServiceImpl实现类
| 方法名 | 参数 | 返回值 | 功能说明 |
|--------|------|--------|----------|
| create{ModuleName}() | {ModuleName}CreateRequest | {ModuleName} | 实现创建逻辑：参数验证→数据转换→保存→返回结果 |
| get{ModuleName}ById() | String id | {ModuleName} | 实现查询逻辑：缓存检查→数据库查询→缓存更新→返回结果 |
| update{ModuleName}() | String id, {ModuleName}UpdateRequest | {ModuleName} | 实现更新逻辑：数据验证→查询现有数据→更新字段→保存→返回结果 |
| delete{ModuleName}() | String id | boolean | 实现删除逻辑：查询数据→设置删除标记→保存→清理缓存→返回结果 |
| get{ModuleName}Page() | {ModuleName}QueryRequest | IPage<{ModuleName}> | 实现分页查询：构建查询条件→执行分页查询→返回结果 |
| batchDelete{ModuleName}s() | List<String> ids | int | 实现批量删除：遍历ID列表→执行删除操作→统计成功数量→返回结果 |

### 2.4 {ModuleName}Mapper接口
| 方法名 | 参数 | 返回值 | 功能说明 |
|--------|------|--------|----------|
| selectById() | String id | {ModuleName} | 根据ID查询单条记录 |
| selectPage() | IPage<{ModuleName}>, Wrapper<{ModuleName}> | IPage<{ModuleName}> | 分页查询记录 |
| insert() | {ModuleName} | int | 插入新记录 |
| updateById() | {ModuleName} | int | 根据ID更新记录 |
| deleteById() | String id | int | 根据ID删除记录 |
| deleteBatchIds() | Collection<String> ids | int | 批量删除记录 |

## 3. 数据流图

### 3.1 创建{模块}数据流
```
HTTP请求 → Controller.create{ModuleName}() 
    ↓
参数验证 (UserCreateRequest)
    ↓
Service.create{ModuleName}()
    ↓
业务逻辑处理 (参数验证、数据转换)
    ↓
Mapper.insert()
    ↓
数据库操作
    ↓
返回结果 (ResultData<{ModuleName}>)
```

### 3.2 查询{模块}数据流
```
HTTP请求 → Controller.get{ModuleName}Info()
    ↓
参数验证 (ID)
    ↓
Service.get{ModuleName}ById()
    ↓
缓存检查 (Redis)
    ↓
Mapper.selectById() (如果缓存未命中)
    ↓
数据库查询
    ↓
缓存更新
    ↓
返回结果 (ResultData<{ModuleName}>)
```

### 3.3 更新{模块}数据流
```
HTTP请求 → Controller.update{ModuleName}()
    ↓
参数验证 (ID + UserUpdateRequest)
    ↓
Service.update{ModuleName}()
    ↓
业务逻辑处理 (数据验证、查询现有数据)
    ↓
Mapper.updateById()
    ↓
数据库更新
    ↓
缓存失效
    ↓
返回结果 (ResultData<{ModuleName}>)
```

### 3.4 删除{模块}数据流
```
HTTP请求 → Controller.delete{ModuleName}()
    ↓
参数验证 (ID)
    ↓
Service.delete{ModuleName}()
    ↓
业务逻辑处理 (查询数据、设置删除标记)
    ↓
Mapper.updateById()
    ↓
数据库软删除
    ↓
缓存失效
    ↓
返回结果 (ResultData<String>)
```

## 4. 业务逻辑流程

### 4.1 创建{模块}业务逻辑
1. **参数验证阶段**
   - 验证必填字段是否为空
   - 验证字段格式是否正确（手机号、邮箱等）
   - 验证业务规则（如用户名唯一性）

2. **数据转换阶段**
   - 将DTO转换为Entity
   - 设置默认值（创建时间、状态等）
   - 密码加密处理（如需要）

3. **数据保存阶段**
   - 检查数据唯一性
   - 执行数据库插入操作
   - 处理插入异常

4. **结果返回阶段**
   - 构建返回结果
   - 记录操作日志
   - 返回成功响应

### 4.2 查询{模块}业务逻辑
1. **缓存检查阶段**
   - 检查Redis缓存是否存在
   - 如果存在，直接返回缓存数据

2. **数据库查询阶段**
   - 构建查询条件
   - 执行数据库查询
   - 处理查询异常

3. **缓存更新阶段**
   - 将查询结果存入缓存
   - 设置缓存过期时间

4. **结果返回阶段**
   - 构建返回结果
   - 返回查询数据

### 4.3 更新{模块}业务逻辑
1. **数据验证阶段**
   - 验证更新参数
   - 检查字段格式

2. **数据查询阶段**
   - 查询现有数据
   - 验证数据是否存在

3. **数据更新阶段**
   - 合并更新字段
   - 执行数据库更新
   - 处理更新异常

4. **缓存处理阶段**
   - 清除相关缓存
   - 更新缓存数据

5. **结果返回阶段**
   - 构建返回结果
   - 记录操作日志

### 4.4 删除{模块}业务逻辑
1. **数据验证阶段**
   - 验证删除权限
   - 检查数据是否存在

2. **软删除处理**
   - 设置删除标记
   - 更新删除时间
   - 执行数据库更新

3. **缓存清理阶段**
   - 清除相关缓存
   - 清理关联数据缓存

4. **结果返回阶段**
   - 构建返回结果
   - 记录删除日志

## 5. 异常处理机制

### 5.1 参数验证异常
- **触发条件**: 请求参数格式错误或必填字段为空
- **处理方式**: 返回400错误码和详细错误信息
- **实现位置**: Controller层的@Valid注解和Service层的业务验证

### 5.2 业务逻辑异常
- **触发条件**: 业务规则验证失败（如用户名已存在）
- **处理方式**: 抛出BusinessException，返回业务错误码
- **实现位置**: Service层的业务逻辑处理

### 5.3 数据库异常
- **触发条件**: 数据库操作失败（连接异常、SQL错误等）
- **处理方式**: 记录错误日志，返回500错误码
- **实现位置**: Mapper层和Service层的异常捕获

### 5.4 缓存异常
- **触发条件**: Redis连接失败或操作异常
- **处理方式**: 降级到数据库查询，记录警告日志
- **实现位置**: Service层的缓存操作

## 6. 性能优化策略

### 6.1 缓存策略
- **查询缓存**: 使用Redis缓存热点数据，减少数据库查询
- **缓存失效**: 数据更新时及时清除相关缓存
- **缓存预热**: 系统启动时预加载重要数据

### 6.2 数据库优化
- **索引优化**: 为查询字段建立合适的索引
- **分页查询**: 使用MyBatis-Plus的分页插件
- **批量操作**: 批量删除使用批量SQL操作

### 6.3 接口优化
- **参数验证**: 在Controller层进行参数验证，避免无效请求
- **异步处理**: 对于耗时操作使用异步处理
- **结果缓存**: 对查询结果进行适当缓存

## 7. 安全考虑

### 7.1 输入验证
- **参数验证**: 对所有输入参数进行严格验证
- **SQL注入防护**: 使用MyBatis-Plus的参数化查询
- **XSS防护**: 对用户输入进行HTML转义

### 7.2 权限控制
- **接口权限**: 在Controller层进行权限验证
- **数据权限**: 在Service层进行数据访问权限控制
- **操作日志**: 记录所有重要操作的日志

### 7.3 数据安全
- **敏感数据加密**: 对密码等敏感数据进行加密存储
- **数据脱敏**: 在返回结果中对敏感信息进行脱敏处理
- **访问控制**: 限制数据库访问权限

## 8. 测试策略

### 8.1 单元测试
- **Service层测试**: 测试业务逻辑的正确性
- **Controller层测试**: 测试接口的输入输出
- **Mapper层测试**: 测试数据库操作的正确性

### 8.2 集成测试
- **接口测试**: 使用curl命令测试接口功能
- **数据库测试**: 测试数据库操作和事务处理
- **缓存测试**: 测试缓存机制的正确性

### 8.3 性能测试
- **并发测试**: 测试接口的并发处理能力
- **压力测试**: 测试系统在高负载下的表现
- **缓存测试**: 测试缓存对性能的提升效果 
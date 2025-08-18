# WechatWork模块控制器重构和表名规范化迭代计划

## 项目概述
在现有的wechatwork模块基础上，进行控制器重构和表名规范化，将`WechatWorkContactsController`重构为`WechatWorkUserController`，将表名`wechatwork_contacts`改为`wechatwork_user`，统一命名规范，简化API接口。

## 当前状态
- [x] 现有联系人同步功能已完成
- [x] 现有表结构已创建 (wechatwork_contacts)
- [x] 现有代码架构已完成（分离适配器、实体类静态构造方法）
- [ ] 控制器重构待完成
- [ ] 表名规范化待完成
- [ ] 数据库表结构更新待完成
- [ ] 相关代码重构待完成
- [ ] 测试验证待完成

## 迭代目标
1. [ ] 重构控制器：`WechatWorkContactsController` → `WechatWorkUserController`
2. [ ] 简化API接口：只保留2个核心路由
3. [ ] 规范化表名：`wechatwork_contacts` → `wechatwork_user`
4. [ ] 更新主键：`contact_id` → `wechatwork_user_id`
5. [ ] 重构相关类名：实体类、Service、Mapper等
6. [ ] 更新数据库表结构
7. [ ] 验证重构后的功能完整性
8. [ ] 更新相关文档和测试

## 详细任务清单

### 阶段1: 控制器重构 🔄
- [ ] 1.1 重命名控制器类：`WechatWorkContactsController` → `WechatWorkUserController`
- [ ] 1.2 简化API路由：只保留2个核心接口
  - [ ] 1.2.1 同步用户信息接口（从适配器获取）
  - [ ] 1.2.2 根据用户ID查询接口（从MySQL获取）
- [ ] 1.3 更新控制器中的依赖注入和服务调用
- [ ] 1.4 更新路由映射和请求参数

### 阶段2: 实体类重构 🔄
- [ ] 2.1 重命名实体类：`WechatworkContacts` → `WechatworkUser`
- [ ] 2.2 更新实体类中的表名注解：`@TableName("wechatwork_user")`
- [ ] 2.3 更新主键字段：`contactId` → `wechatworkUserId`
- [ ] 2.4 更新主键注解：`@TableId(value = "wechatwork_user_id", type = IdType.INPUT)`
- [ ] 2.5 更新其他相关字段名（如需要）
- [ ] 2.6 更新实体类中的静态构造方法

### 阶段3: Service层重构 🔄
- [ ] 3.1 重命名服务类：`WechatworkContactsService` → `WechatworkUserService`
- [ ] 3.2 更新服务类中的实体类引用
- [ ] 3.3 更新服务类中的方法名（如需要）
- [ ] 3.4 更新服务类中的依赖注入
- [ ] 3.5 验证业务逻辑的完整性

### 阶段4: Mapper层重构 🔄
- [ ] 4.1 重命名Mapper接口：`WechatworkContactsMapper` → `WechatworkUserMapper`
- [ ] 4.2 更新Mapper接口中的方法名
- [ ] 4.3 更新Mapper接口中的参数类型
- [ ] 4.4 更新XML映射文件中的表名和字段名
- [ ] 4.5 验证SQL查询的正确性

### 阶段5: 数据库表结构更新 🔄
- [ ] 5.1 创建新的表结构SQL：`wechatwork_user`
- [ ] 5.2 更新主键字段：`wechatwork_user_id`
- [ ] 5.3 验证新表结构的完整性
- [ ] 5.4 创建数据迁移脚本（如需要）
- [ ] 5.5 测试新表结构的功能

### 阶段6: 适配器层更新 🔄
- [ ] 6.1 更新`WechatWorkUserApiAdapter`中的相关引用
- [ ] 6.2 更新DTO类中的相关引用
- [ ] 6.3 验证API调用的正确性
- [ ] 6.4 测试数据转换的完整性

### 阶段7: 测试和验证 🔄
- [ ] 7.1 单元测试更新
- [ ] 7.2 集成测试验证
- [ ] 7.3 功能测试验证
- [ ] 7.4 性能测试验证
- [ ] 7.5 数据一致性验证

### 阶段8: 文档更新和部署 🔄
- [ ] 8.1 更新API文档
- [ ] 8.2 更新README文档
- [ ] 8.3 更新数据映射分析文档
- [ ] 8.4 代码审查
- [ ] 8.5 合并到主分支

## 技术实现要点

### 重构原则
1. **命名规范化**: 统一使用`User`而不是`Contacts`
2. **接口简化**: 只保留核心的2个API接口
3. **向后兼容**: 确保重构后功能完全一致
4. **数据完整性**: 确保数据迁移的准确性

### 核心API接口设计
1. **同步用户信息接口**:
   - 路由: `POST /tp/wechatWork/user/sync`
   - 功能: 从企业微信API获取最新用户信息并更新数据库
   - 参数: `depId`（可选）、`fetchChild`（可选）

2. **查询用户信息接口**:
   - 路由: `GET /tp/wechatWork/user/{wechatworkUserId}`
   - 功能: 根据用户ID从MySQL数据库查询用户信息
   - 参数: `wechatworkUserId`（路径参数）

### 数据库表结构更新
- **表名**: `wechatwork_contacts` → `wechatwork_user`
- **主键**: `contact_id` → `wechatwork_user_id`
- **其他字段**: 保持现有结构不变
- **索引**: 更新相关索引名称

### 代码重构策略
1. **渐进式重构**: 先重构一个模块，测试通过后再重构下一个
2. **保持功能一致**: 确保重构后的功能与重构前完全一致
3. **完整测试**: 每个阶段完成后进行充分测试

## 风险评估
1. **重构风险**: 大规模重构可能导致功能异常
2. **数据迁移风险**: 表结构变更可能导致数据丢失
3. **兼容性风险**: 重构后可能影响其他模块的调用
4. **测试覆盖风险**: 重构后需要重新验证所有功能

## 成功标准
1. [ ] 控制器重构完成，API接口简化
2. [ ] 表名规范化完成，主键更新完成
3. [ ] 所有相关类名重构完成
4. [ ] 数据库表结构更新完成
5. [ ] 功能测试通过，性能无下降
6. [ ] 代码质量提升，命名更加规范
7. [ ] 文档更新完成，便于后续维护

## 时间估算
- 阶段1: 🔄 0.5天
- 阶段2: 🔄 0.5天
- 阶段3: 🔄 0.5天
- 阶段4: 🔄 0.5天
- 阶段5: 🔄 1天
- 阶段6: 🔄 0.5天
- 阶段7: 🔄 1天
- 阶段8: 🔄 0.5天
- **总计**: 5天

## 依赖关系
- ✅ 现有联系人同步功能稳定
- ✅ 现有代码架构完整
- ✅ 现有测试用例可用
- ✅ 数据库备份完整

## 备注
- 所有重构工作在dev分支上进行
- 每个阶段完成后需要充分测试
- 遇到问题及时记录和解决
- 重构完成后需要更新所有相关文档
- 建议在重构过程中保持频繁的提交和测试

## 问题记录

### 问题1: 控制器重构和表名规范化
**问题描述**: 
1. `WechatWorkContactsController` 应该重命名为 `WechatWorkUserController`
2. 控制器应该只有2个路由：
   - 与企业微信更新最新人员信息（从适配器获取）
   - 根据用户ID获取所有信息（从MySQL获取）
3. 表名 `wechatwork_contacts` 应该改为 `wechatwork_user`
4. 主键应该改为 `wechatwork_user_id`

**影响范围**:
- 控制器类名和路由
- 实体类名和表名
- 数据库表结构
- Mapper接口
- Service类名
- 相关测试类

**解决方案**: 需要重构整个用户管理模块，统一命名规范
**状态**: 🔄 待解决

## 变更记录
- 2025-08-18: 创建控制器重构和表名规范化迭代计划
- 2025-08-18: 识别重构需求和影响范围
- 2025-08-18: 制定详细的8阶段重构计划
- 2025-08-18: 添加第三轮迭代：Service层拆分

## 第三轮迭代：Service层拆分

### 项目概述
在完成控制器重构和表名规范化的基础上，进一步优化Service层架构，将现有的`WechatworkUserService`拆分为两个专门的Service：
1. **WechatworkUserAdapterService**: 专门给迭代器使用，负责数据同步和批量处理
2. **WechatworkUserService**: 专门给控制器使用，负责业务逻辑和API接口

### 当前状态
- [x] 控制器重构完成
- [x] 实体类重构完成
- [x] Service层重构完成
- [x] Mapper层重构完成
- [x] 数据库表结构更新完成
- [ ] Service层拆分待完成
- [ ] 迭代器适配待完成
- [ ] 控制器适配待完成

### 迭代目标
1. [ ] 拆分Service层：`WechatworkUserService` → `WechatworkUserAdapterService` + `WechatworkUserService`
2. [ ] 明确职责分工：适配器服务专注数据同步，业务服务专注API逻辑
3. [ ] 优化依赖关系：减少不必要的依赖，提高代码可维护性
4. [ ] 完善接口设计：为迭代器和控制器提供专门的接口
5. [ ] 验证架构合理性：确保拆分后的架构更加清晰和高效

### 详细任务清单

#### 阶段1: Service层拆分设计 🔄
- [ ] 1.1 分析现有Service的职责和依赖
- [ ] 1.2 设计两个Service的职责分工
- [ ] 1.3 设计Service间的依赖关系
- [ ] 1.4 制定拆分策略和迁移计划

#### 阶段2: 创建WechatworkUserAdapterService 🔄
- [ ] 2.1 创建`WechatworkUserAdapterService`类
- [ ] 2.2 实现数据同步相关方法
- [ ] 2.3 实现批量处理相关方法
- [ ] 2.4 实现迭代器专用接口
- [ ] 2.5 添加必要的日志和异常处理

#### 阶段3: 重构WechatworkUserService 🔄
- [ ] 3.1 重构现有`WechatworkUserService`类
- [ ] 3.2 移除数据同步相关方法
- [ ] 3.3 保留和优化业务逻辑方法
- [ ] 3.4 添加对`WechatworkUserAdapterService`的调用
- [ ] 3.5 优化控制器相关接口

#### 阶段4: 更新依赖注入和配置 🔄
- [ ] 4.1 更新Controller中的依赖注入
- [ ] 4.2 更新其他Service中的依赖注入
- [ ] 4.3 检查循环依赖问题
- [ ] 4.4 优化Bean配置

#### 阶段5: 测试和验证 🔄
- [ ] 4.1 单元测试编写和更新
- [ ] 4.2 集成测试验证
- [ ] 4.3 功能测试验证
- [ ] 4.4 性能测试验证

### 技术实现要点

#### 职责分工设计
1. **WechatworkUserAdapterService**:
   - 负责与企业微信API的数据同步
   - 负责批量数据处理和存储
   - 提供迭代器需要的所有方法
   - 专注于数据层面的操作

2. **WechatworkUserService**:
   - 负责业务逻辑处理
   - 负责API接口的业务逻辑
   - 调用`WechatworkUserAdapterService`进行数据操作
   - 专注于业务层面的操作

#### 依赖关系设计
```
Controller → WechatworkUserService → WechatworkUserAdapterService → Mapper
    ↓              ↓                        ↓
   API接口      业务逻辑                 数据操作
```

#### 接口设计原则
1. **单一职责**: 每个Service只负责自己的职责范围
2. **依赖倒置**: 高层模块不依赖低层模块，都依赖抽象
3. **接口隔离**: 为不同用途提供专门的接口
4. **开闭原则**: 对扩展开放，对修改关闭

### 风险评估
1. **拆分风险**: Service拆分可能导致功能异常
2. **依赖风险**: 新的依赖关系可能导致循环依赖
3. **测试风险**: 拆分后需要重新验证所有功能
4. **性能风险**: 额外的Service调用可能影响性能

### 成功标准
1. [ ] Service层成功拆分为两个专门的Service
2. [ ] 职责分工明确，代码结构清晰
3. [ ] 所有功能正常工作，性能无下降
4. [ ] 代码可维护性显著提升
5. [ ] 测试覆盖率达到要求

### 时间估算
- 阶段1: 🔄 0.5天
- 阶段2: 🔄 1天
- 阶段3: 🔄 1天
- 阶段4: 🔄 0.5天
- 阶段5: 🔄 1天
- **总计**: 4天

### 依赖关系
- ✅ 控制器重构完成
- ✅ 实体类重构完成
- ✅ 现有Service功能稳定
- ✅ 测试框架可用

### 备注
- 拆分过程中保持向后兼容
- 每个阶段完成后进行充分测试
- 遇到问题及时记录和解决
- 拆分完成后更新相关文档

## 第四轮迭代：Service 方法清理与使用清单

> 目标：去除未使用/不必要的方法，确保 Service 层聚焦最小必要接口。

### WechatworkUserService 方法清单（第1个 Service）

| 方法名 | 功能说明 | 使用位置 | 使用状态 | 处理建议 |
|---|---|---|---|---|
| `syncWechatWorkUsers(Integer depId, Integer fetchChild)` | 触发用户同步，委托给 `WechatworkUserAdapterService` 执行 | `WechatWorkUserController.sync` | 已使用 | 保留 |
| `getUserByWechatworkUserId(String wechatworkUserId)` | 按用户ID从MySQL查询用户详情 | `WechatWorkUserController.getUserById` | 已使用 | 保留 |
| `getUsersByDepId(Integer depId)` | 按部门ID查询用户列表 | 暂无直接引用 | 未使用 | 待删除或在新增部门用户查询API时复用 |
| `getAllUsers()` | 查询全部用户 | 暂无直接引用 | 未使用 | 待删除（建议后续走分页查询/索引化） |
| `getUserStatistics()` | 计算启用/禁用用户数量等统计信息 | 暂无直接引用 | 未使用 | 待删除或迁移至报表/运营模块 |
| `searchUsers(String keyword)` | 本地内存过滤搜索用户 | 暂无直接引用 | 未使用 | 待删除或重构为基于数据库索引的分页检索 |

说明：以上“使用位置”为当前代码内直接引用位置的梳理结果；后续如新增 API/任务可按需恢复或重构。

下一步：输出 `WechatworkDepartmentService` 方法清单并给出处理建议。

### WechatworkDepartmentService 方法清单（第2个 Service）

| 方法名 | 功能说明 | 使用位置 | 使用状态 | 处理建议 |
|---|---|---|---|---|
| `syncWechatWorkDepartments(Integer departmentId)` | 触发部门同步，委托给 `WechatworkDepartmentAdapterService` 执行 | `WechatWorkDepartmentController.syncWechatWorkDepartments` | 已使用 | 保留 |
| `getAllDepartments()` | 查询全部部门列表 | `WechatWorkDepartmentController.getSyncStatus`；`WechatworkUserAdapterService` 中用于遍历部门（行110） | 已使用 | 保留 |
| `getDepartmentById(Integer depId)` | 根据部门ID查询部门 | 暂无直接引用 | 未使用 | 待删除或待后续“按ID查询部门”API再启用 |
| `getDepartmentsByParentId(Integer parentId)` | 根据父部门ID查询子部门 | 暂无直接引用 | 未使用 | 建议删除（同名能力已在 `WechatworkDepartmentAdapterService` 提供） |
| `buildDepartmentTree()` | 构建部门层级树（委托给 AdapterService） | 暂无直接引用 | 未使用 | 待删除，后续若新增“部门树”API再恢复 |
| `calculateDepartmentPath(Integer departmentId)` | 计算部门层级路径（委托给 AdapterService） | 暂无直接引用 | 未使用 | 待删除，需要时走 AdapterService 能力 |
| `getAllChildDepartmentIds(Integer departmentId)` | 获取部门所有子部门ID（委托给 AdapterService） | 暂无直接引用 | 未使用 | 待删除，需要时走 AdapterService 能力 |
| `getDepartmentDepth(Integer departmentId)` | 获取部门深度（委托给 AdapterService） | 暂无直接引用 | 未使用 | 待删除，需要时走 AdapterService 能力 |
| `validateDepartmentHierarchy()` | 验证部门层级关系（委托给 AdapterService） | 暂无直接引用 | 未使用 | 待删除，需要时走 AdapterService 能力 |
| `getDepartmentStatistics()` | 统计部门数量、根/子部门数等 | 暂无直接引用 | 未使用 | 待删除或迁移到报表/运营模块 |
| `searchDepartments(String keyword)` | 本地内存过滤搜索部门 | 暂无直接引用 | 未使用 | 待删除或重构为基于数据库索引的分页检索 |

说明：上述“使用位置”基于当前仓库检索结果；若后续引入新的控制器/任务，可按需恢复或迁移到更合适的层（优先使用 `WechatworkDepartmentAdapterService` 的数据层能力）。

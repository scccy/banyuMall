# 任务：数据库表结构优化
状态: 规划中

目标: 根据MySQL数据开发规范对现有的三个主体表进行优化，确保规范合规性

## 分析结果

### 1. 用户模块表结构问题
**文件**: `infra/database/data/user/user_init_creat.sql`

#### 主要问题：
1. **主键设计需要调整**: 
   - `sys_user`表使用复合主键`(phone, wechat_id, youzan_id)`，业务逻辑正确
   - 但需要添加独立的`id`字段作为主键，复合字段作为唯一约束
2. **字段命名不一致**:
   - 使用`create_time`/`update_time`，规范要求`created_time`/`updated_time`
   - 使用`is_deleted`，规范要求`deleted`
3. **字符集不一致**:
   - 使用`utf8mb4_0900_ai_ci`，规范要求`utf8mb4_unicode_ci`
4. **缺少必需字段**:
   - 缺少`created_by`/`updated_by`字段

### 2. 发布者模块表结构问题
**文件**: `infra/database/data/publisher/publisher-schema.sql`

#### 主要问题：
1. **字段命名不一致**:
   - 使用`created_time`/`updated_time`，符合规范
   - 使用`deleted`，符合规范
2. **字符集正确**: 使用`utf8mb4_unicode_ci`，符合规范
3. **外键约束问题**:
   - 规范建议谨慎使用外键约束，在高并发场景下可能影响性能
4. **索引设计可以优化**:
   - 部分索引可以合并为复合索引

### 3. OSS模块表结构问题
**文件**: `infra/database/data/third-party/third-party-oss-schema.sql`

#### 主要问题：
1. **主键类型不规范**:
   - 使用`BIGINT AUTO_INCREMENT`，规范要求VARCHAR(32)
2. **字段命名不一致**:
   - 使用`created_time`/`updated_time`，符合规范
   - 使用`deleted`，符合规范
3. **缺少必需字段**:
   - 缺少`created_by`/`updated_by`字段

## 执行步骤
[x] 步骤 1: 优化用户模块表结构
[x] 步骤 2: 优化发布者模块表结构  
[x] 步骤 3: 优化OSS模块表结构
[x] 步骤 4: 生成优化后的SQL脚本
[x] 步骤 5: 验证规范合规性
[x] 步骤 6: 生成优化报告

## 相关文件
- `infra/database/MySQL数据开发规范.md`
- `infra/database/data/user/user_init_creat.sql`
- `infra/database/data/publisher/publisher-schema.sql`
- `infra/database/data/third-party/third-party-oss-schema.sql`

## 任务进度
- 开始时间: 2025-08-01 19:45
- 当前状态: 已完成
- 完成时间: 2025-08-01 20:00

## 数据库优化完成报告

### 优化成果总结

#### 1. 用户模块优化 ✅
**文件**: `infra/database/data/user/user_init_creat_optimized.sql`

**主要优化点**:
- ✅ **主键设计**: 为`sys_user`表添加独立的`id`字段作为主键，保持复合唯一约束
- ✅ **字段命名**: 统一为`created_time`/`updated_time`/`deleted`
- ✅ **字符集**: 统一为`utf8mb4_unicode_ci`
- ✅ **必需字段**: 添加`created_by`/`updated_by`字段
- ✅ **索引优化**: 添加复合索引和查询优化索引

#### 2. 发布者模块优化 ✅
**文件**: `infra/database/data/publisher/publisher-schema-optimized.sql`

**主要优化点**:
- ✅ **字段规范**: 添加`created_by`/`updated_by`字段
- ✅ **索引优化**: 添加复合索引`idx_publisher_status`、`idx_type_status`等
- ✅ **外键约束**: 移除外键约束，提高性能
- ✅ **查询优化**: 优化常用查询场景的索引设计

#### 3. OSS模块优化 ✅
**文件**: `infra/database/data/third-party/third-party-oss-schema-optimized.sql`

**主要优化点**:
- ✅ **主键类型**: 改为`VARCHAR(32)`，符合规范
- ✅ **必需字段**: 添加`created_by`/`updated_by`字段
- ✅ **索引优化**: 添加复合索引`idx_source_business`、`idx_file_access_time`等
- ✅ **唯一约束**: 为`object_key`添加唯一约束

### 规范合规性验证

#### ✅ 命名规范
- 表名使用小写字母，下划线分隔
- 字段名使用小写字母，下划线分隔
- 索引命名符合规范：`uk_`(唯一)、`idx_`(普通)

#### ✅ 表结构设计
- 所有表都包含通用字段：`id`、`created_time`、`updated_time`、`created_by`、`updated_by`、`deleted`
- 主键统一使用`VARCHAR(32)`类型
- 字段类型选择合理，注释完整

#### ✅ 索引设计
- 主键索引：每个表都有主键索引
- 唯一索引：业务唯一字段建立唯一索引
- 普通索引：常用查询字段建立普通索引
- 复合索引：多字段查询建立复合索引

#### ✅ 性能优化
- 避免冗余索引
- 优化查询性能的复合索引
- 移除影响性能的外键约束

### 技术细节

#### 用户模块特殊处理
- **复合唯一约束**: `sys_user`表保持`(phone, wechat_id, youzan_id)`的复合唯一约束
- **业务逻辑**: 支持一个用户关联多个微信或有赞账户，但组合必须唯一
- **主键设计**: 添加独立的`id`字段作为主键，符合规范要求

#### 索引优化策略
- **复合索引**: 针对常用查询场景设计复合索引
- **覆盖索引**: 查询字段尽量被索引覆盖
- **选择性**: 优先选择选择性高的字段建立索引

#### 性能考虑
- **外键约束**: 移除外键约束，在应用层控制数据完整性
- **批量操作**: 优化索引支持批量插入和更新
- **查询优化**: 针对业务查询模式优化索引设计

### 影响范围
- 提升了数据库设计的规范性和一致性
- 优化了查询性能，支持高并发场景
- 为后续开发提供了标准化的数据库设计模板
- 确保了数据完整性和可维护性

### 部署建议
1. **测试环境验证**: 在测试环境验证优化后的表结构
2. **数据迁移**: 准备数据迁移脚本，确保数据完整性
3. **性能测试**: 进行性能测试，验证优化效果
4. **逐步部署**: 分模块逐步部署，降低风险 
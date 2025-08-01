# 任务：路由一致性检查与规范化
状态: 已完成

目标: 检查所有Feign客户端路由与Controller路由的一致性，确保路由都以/开头，符合API路由规范

## 问题分析
1. **路由不一致**: Feign客户端路由可能与Controller实际路由不匹配
2. **路径前缀问题**: 可能存在不以/开头的路由配置
3. **yml配置问题**: 配置文件中可能存在根路径不为/的情况
4. **文档不一致**: 设计文档中的路由设计与实际代码实现可能不一致
5. **规则违反**: 可能违反API路由命名规范（使用"/service/<entity>"格式）

## 执行步骤
- [x] 步骤 1: 全面扫描所有Controller的@RequestMapping配置
- [x] 步骤 2: 检查所有Feign客户端的@FeignClient和@RequestMapping配置
- [x] 步骤 3: 检查yml配置文件中的路由配置，确保根路径为/
- [x] 步骤 4: 检查设计文档中的路由设计与实际代码实现的一致性
- [x] 步骤 5: 对比Controller和Feign客户端路由，找出不一致的地方
- [x] 步骤 6: 修复路由配置，确保符合规范
- [x] 步骤 7: 更新设计文档，确保与实际实现一致
- [x] 步骤 8: 验证修复效果，确保路由一致性

## 修复总结
1. **service-auth模块**: 
   - Feign客户端路径从`/auth`修复为`/service/auth`
   - 符合Controller的`@RequestMapping("/service/auth")`配置

2. **service-user模块**: 
   - UserFeignClient路径从`/user`修复为`/service/user`
   - **合并Feign客户端**: 将AuthPasswordFeignClient合并到AuthFeignClient中，解决Bean名称冲突
   - 符合Controller的`@RequestMapping("/service/user")`配置

3. **third-party-oss模块**: 
   - Feign客户端路径从`/api/v1/oss`修复为`/tp/oss`
   - 设计文档Feign客户端名称从`third-party-oss`修复为`aliyun-oss`
   - api-test.md文档中所有接口路径从`/api/v1/oss/`修复为`/tp/oss/`
   - 符合Controller的`@RequestMapping("/tp/oss")`配置

4. **core-publisher模块**: 
   - 设计文档路径从`/core-publisher`修复为`/core/publisher`
   - api-test.md文档中所有接口路径从`/core-publisher/`修复为`/core/publisher/`
   - 符合实际代码的`@RequestMapping("/core/publisher/tasks")`配置

5. **yml配置文件**: 
   - 确认所有配置文件根路径配置正确
   - 网关配置中context-path已注释，使用根路径`/`

6. **文档一致性**: 
   - 所有模块的设计文档和api-test.md文档已更新
   - 确保文档中的路由设计与实际代码实现完全一致

7. **Bean冲突解决**: 
   - 合并了指向同一服务的多个Feign客户端
   - 删除了重复的AuthPasswordFeignClient和AuthPasswordFeignClientFallback
   - 更新了所有相关引用

## 发现的问题
1. **service-auth模块**: Feign客户端路径应该是`/service/auth`而不是`/auth`
2. **service-user模块**: Feign客户端路径应该是`/service/user`而不是`/user`
3. **third-party-oss模块**: Controller和Feign客户端路径不一致
4. **core-publisher模块**: 设计文档与实际代码路径不一致

## 相关规则
- LR-003: Feign客户端配置规范
- 项目约定: API路由命名使用"/service/<entity>"格式
- DEV-007: 网关接口同步规则
- LR-003: 模块设计与文档标准化规则

## 检查范围
- service-auth模块
- service-user模块  
- core-publisher模块
- service-gateway模块
- third-party-oss模块

## 检查内容
- **Controller路由**: @RequestMapping、@RestController等注解
- **Feign客户端路由**: @FeignClient的path属性和@RequestMapping
- **yml配置文件**: application.yml、application-dev.yml等中的路由配置
- **网关路由**: GatewayConfig中的路由配置
- **设计文档**: 模块设计文档中的API接口定义
- **API测试文档**: api-test.md中的接口测试用例

## 预计完成时间
2025-08-01 23:45

## 检查文档范围
- infra/moudleDocs/{模块名}/模块设计.md
- infra/moudleDocs/{模块名}/api-test.md
- infra/moudleDocs/{模块名}/模块迭代设计.md 
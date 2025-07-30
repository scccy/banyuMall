# 任务：JWT工具类重构与架构优化
状态: 进行中
创建时间: 2025-07-30 14:02

## 任务背景
用户指出工具类应该是静态应用，AuthController不应该包含Feign客户端，需要重构架构。

## 目标
1. 将JwtUtil和TokenBlacklistUtil改为静态工具类
2. 移除AuthController中的Feign客户端依赖
3. 简化微服务架构，避免不必要的跨服务调用

## 执行步骤
- [x] 将JwtUtil改为静态工具类，移除@Component注解
- [x] 将JwtUtil的配置属性改为静态常量
- [x] 将TokenBlacklistUtil改为静态工具类
- [x] 创建TokenBlacklistConfig配置类初始化Redis模板
- [x] 更新AutoConfiguration.imports文件
- [x] 修改AuthController使用静态工具类
- [x] 修改SysUserServiceImpl使用静态工具类
- [x] 删除UserFeignClient，简化架构
- [ ] 启动应用程序验证功能
- [ ] 更新相关基线文档

## 技术决策
1. **静态工具类**: JwtUtil和TokenBlacklistUtil改为静态方法，避免依赖注入问题
2. **配置简化**: JWT配置直接写死，不再使用@Value动态获取
3. **架构简化**: 移除AuthController中的Feign客户端，避免微服务间复杂依赖

## 遇到的问题
- 初始依赖注入问题已通过静态化解决
- Feign客户端问题已通过删除简化

## 下一步
启动应用程序验证所有功能正常工作 
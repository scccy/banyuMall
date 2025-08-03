# DEV-015: MyBatis-Plus分页查询规则

**ID**: DEV-015  
**Name**: MyBatis-Plus分页查询规则  
**Status**: Active  
**创建时间**: 2025-01-27  

## 触发情景 (Context/Trigger)
当需要进行分页查询、列表查询或大数据量查询时。

## 指令 (Directive)

### 1. 分页查询原则
- **必须 (MUST)** 使用MyBatis-Plus的IPage进行分页查询
- **必须 (MUST)** 使用PageResult作为统一的分页响应格式
- **禁止 (MUST NOT)** 使用传统的手动分页方式
- **禁止 (MUST NOT)** 一次性查询大量数据

### 2. MyBatis-Plus分页配置
- **必须 (MUST)** 在base模块中配置分页插件
- **必须 (MUST)** 使用MySQL数据库类型
- **必须 (MUST)** 设置合理的分页大小限制

### 3. 分页查询实现
- **必须 (MUST)** 使用Page对象作为分页参数
- **必须 (MUST)** 使用selectPage方法进行分页查询
- **必须 (MUST)** 返回IPage对象
- **必须 (MUST)** 转换为PageResult格式

### 4. 分页参数规范
- **page**: 页码，从1开始
- **size**: 每页大小，建议10-100之间
- **默认值**: page=1, size=10
- **最大限制**: size不超过1000

## 理由 (Justification)
此规则源于分页查询最佳实践，确保：
1. **性能优化**: 避免一次性查询大量数据
2. **内存管理**: 控制内存使用量
3. **用户体验**: 提供流畅的分页体验
4. **系统稳定性**: 避免系统因大数据量查询而崩溃

## 示例 (Examples)

### ✅ 正确做法
```java
// Base模块中的分页插件配置
@Configuration
public class MyBatisPlusConfig {
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}

// Service层分页查询
@Service
public class PublisherTaskServiceImpl implements PublisherTaskService {
    
    @Override
    public PageResult<TaskListResponse> getTaskList(TaskListRequest request) {
        // 构建查询条件
        LambdaQueryWrapper<PublisherTask> wrapper = new LambdaQueryWrapper<>();
        if (request.getTaskTypeId() != null) {
            wrapper.eq(PublisherTask::getTaskTypeId, request.getTaskTypeId());
        }
        wrapper.orderByDesc(PublisherTask::getCreatedTime);
        
        // 分页查询
        Page<PublisherTask> page = new Page<>(request.getPage(), request.getSize());
        Page<PublisherTask> result = taskMapper.selectPage(page, wrapper);
        
        // 转换为响应对象
        List<TaskListResponse> responses = result.getRecords().stream()
            .map(this::convertToTaskListResponse)
            .collect(Collectors.toList());
        
        return new PageResult<>(responses, result.getTotal(), result.getCurrent(), result.getSize());
    }
}

// Controller层分页接口
@RestController
public class PublisherTaskController {
    
    @GetMapping
    public ResultData<PageResult<TaskListResponse>> getTaskList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer taskTypeId) {
        
        TaskListRequest request = new TaskListRequest();
        request.setPage(page);
        request.setSize(size);
        request.setTaskTypeId(taskTypeId);
        
        PageResult<TaskListResponse> result = taskService.getTaskList(request);
        return ResultData.ok("获取任务列表成功", result);
    }
}
```

### ❌ 错误做法
```java
// 不使用分页，一次性查询所有数据
@Service
public class TaskService {
    public List<Task> getAllTasks() {
        return taskMapper.selectList(null); // 可能返回大量数据
    }
}

// 手动分页，不使用MyBatis-Plus分页功能
@Service
public class TaskService {
    public List<Task> getTasks(int page, int size) {
        int offset = (page - 1) * size;
        return taskMapper.selectByLimit(offset, size); // 手动分页
    }
}

// 分页大小过大
@GetMapping
public ResultData<List<Task>> getTasks(@RequestParam(defaultValue = "10000") Integer size) {
    // 分页大小过大，可能导致性能问题
}
```

## 相关规则
- **LR-002**: MyBatis-Plus依赖版本一致性规则
- **LR-008**: Spring配置类优先base模块规则
- **DEV-012**: 统一响应数据类和实体类规范 
# MyBatis-Plus分页查询开发规则

## 📋 规则概述

**ID**: DEV-002  
**Name**: MyBatis-Plus分页查询开发规则  
**Status**: Active  
**创建时间**: 2025-07-31  

## 🎯 核心原则

### 1. 分页插件配置
- **必须 (MUST)** 配置MyBatis-Plus分页插件
- **必须 (MUST)** 使用 `IPage<T>` 接口进行分页查询
- **必须 (MUST)** 使用 `Page<T>` 类创建分页对象

### 2. 分页查询规范
- **必须 (MUST)** 在Service层进行分页逻辑处理
- **必须 (MUST)** 在Controller层接收分页参数
- **必须 (MUST)** 返回统一的分页响应格式
- **禁止 (MUST NOT)** 在Mapper层直接处理分页逻辑

### 3. 性能优化
- **必须 (MUST)** 合理设置分页大小，避免大数据量查询
- **必须 (MUST)** 使用索引优化分页查询性能
- **建议 (SHOULD)** 使用缓存减少重复查询

## 🔧 配置实现

### 1. 分页插件配置
```java
@Configuration
public class MyBatisPlusConfig {
    
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        
        // 添加分页插件
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        
        return interceptor;
    }
}
```

### 2. 分页参数DTO
```java
@Data
public class PageRequest {
    
    @Min(value = 1, message = "页码必须大于0")
    private Integer current = 1;
    
    @Min(value = 1, message = "每页大小必须大于0")
    @Max(value = 100, message = "每页大小不能超过100")
    private Integer size = 10;
    
    private String keyword;
    private String sortField;
    private String sortOrder = "desc";
}
```

### 3. 分页响应DTO
```java
@Data
public class PageResponse<T> {
    
    private List<T> records;           // 数据列表
    private Long total;                // 总记录数
    private Long size;                 // 每页大小
    private Long current;              // 当前页码
    private Long pages;                // 总页数
    private Boolean hasNext;           // 是否有下一页
    private Boolean hasPrevious;       // 是否有上一页
    
    public static <T> PageResponse<T> of(IPage<T> page) {
        PageResponse<T> response = new PageResponse<>();
        response.setRecords(page.getRecords());
        response.setTotal(page.getTotal());
        response.setSize(page.getSize());
        response.setCurrent(page.getCurrent());
        response.setPages(page.getPages());
        response.setHasNext(page.getCurrent() < page.getPages());
        response.setHasPrevious(page.getCurrent() > 1);
        return response;
    }
}
```

## 📝 代码实现

### 1. Mapper层实现
```java
@Mapper
public interface UserMapper extends BaseMapper<User> {
    
    // 基础分页查询
    IPage<User> selectUserPage(IPage<User> page, @Param("keyword") String keyword);
    
    // 复杂分页查询
    IPage<User> selectUserPageWithCondition(IPage<User> page, @Param("condition") UserQueryCondition condition);
}
```

### 2. Service层实现
```java
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    
    @Override
    public PageResponse<User> getUserPage(PageRequest request) {
        // 创建分页对象
        Page<User> page = new Page<>(request.getCurrent(), request.getSize());
        
        // 构建查询条件
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(request.getKeyword())) {
            queryWrapper.like(User::getUsername, request.getKeyword())
                       .or()
                       .like(User::getNickname, request.getKeyword());
        }
        
        // 排序
        if (StringUtils.hasText(request.getSortField())) {
            boolean isAsc = "asc".equalsIgnoreCase(request.getSortOrder());
            queryWrapper.orderBy(true, isAsc, 
                getColumnByField(request.getSortField()));
        } else {
            queryWrapper.orderByDesc(User::getCreateTime);
        }
        
        // 执行分页查询
        IPage<User> result = this.page(page, queryWrapper);
        
        // 返回分页响应
        return PageResponse.of(result);
    }
    
    @Override
    public PageResponse<User> getUserPageWithCondition(PageRequest request, UserQueryCondition condition) {
        // 创建分页对象
        Page<User> page = new Page<>(request.getCurrent(), request.getSize());
        
        // 执行自定义分页查询
        IPage<User> result = baseMapper.selectUserPageWithCondition(page, condition);
        
        // 返回分页响应
        return PageResponse.of(result);
    }
    
    private SFunction<User, ?> getColumnByField(String field) {
        // 字段映射逻辑
        switch (field) {
            case "username": return User::getUsername;
            case "nickname": return User::getNickname;
            case "createTime": return User::getCreateTime;
            default: return User::getCreateTime;
        }
    }
}
```

### 3. Controller层实现
```java
@RestController
@RequestMapping("/service/user")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/page")
    public ResultData<PageResponse<User>> getUserPage(PageRequest request) {
        try {
            PageResponse<User> response = userService.getUserPage(request);
            return ResultData.success(response);
        } catch (Exception e) {
            log.error("分页查询用户失败", e);
            return ResultData.error(ErrorCode.QUERY_FAILED, "分页查询失败");
        }
    }
    
    @PostMapping("/page/condition")
    public ResultData<PageResponse<User>> getUserPageWithCondition(
            @RequestBody PageRequest request,
            @RequestBody UserQueryCondition condition) {
        try {
            PageResponse<User> response = userService.getUserPageWithCondition(request, condition);
            return ResultData.success(response);
        } catch (Exception e) {
            log.error("条件分页查询用户失败", e);
            return ResultData.error(ErrorCode.QUERY_FAILED, "条件分页查询失败");
        }
    }
}
```

### 4. XML映射文件
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" 
    "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.origin.user.mapper.UserMapper">
    
    <!-- 基础分页查询 -->
    <select id="selectUserPage" resultType="com.origin.user.entity.User">
        SELECT * FROM sys_user
        <where>
            <if test="keyword != null and keyword != ''">
                AND (username LIKE CONCAT('%', #{keyword}, '%') 
                     OR nickname LIKE CONCAT('%', #{keyword}, '%'))
            </if>
        </where>
        ORDER BY create_time DESC
    </select>
    
    <!-- 复杂分页查询 -->
    <select id="selectUserPageWithCondition" resultType="com.origin.user.entity.User">
        SELECT u.* FROM sys_user u
        <where>
            <if test="condition.keyword != null and condition.keyword != ''">
                AND (u.username LIKE CONCAT('%', #{condition.keyword}, '%') 
                     OR u.nickname LIKE CONCAT('%', #{condition.keyword}, '%'))
            </if>
            <if test="condition.userType != null">
                AND u.user_type = #{condition.userType}
            </if>
            <if test="condition.status != null">
                AND u.status = #{condition.status}
            </if>
            <if test="condition.startTime != null">
                AND u.create_time >= #{condition.startTime}
            </if>
            <if test="condition.endTime != null">
                AND u.create_time <= #{condition.endTime}
            </if>
        </where>
        ORDER BY u.create_time DESC
    </select>
    
</mapper>
```

## 📊 性能优化

### 1. 索引优化
```sql
-- 为分页查询字段创建索引
CREATE INDEX idx_user_username ON sys_user(username);
CREATE INDEX idx_user_nickname ON sys_user(nickname);
CREATE INDEX idx_user_create_time ON sys_user(create_time);
CREATE INDEX idx_user_type_status ON sys_user(user_type, status);
```

### 2. 查询优化
```java
// 使用selectMaps减少数据传输
public PageResponse<Map<String, Object>> getUserPageOptimized(PageRequest request) {
    Page<Map<String, Object>> page = new Page<>(request.getCurrent(), request.getSize());
    
    // 只查询需要的字段
    LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.select(User::getUserId, User::getUsername, User::getNickname, User::getCreateTime);
    
    IPage<Map<String, Object>> result = this.pageMaps(page, queryWrapper);
    return PageResponse.of(result);
}
```

### 3. 缓存优化
```java
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Override
    @Cacheable(value = "user:page", key = "#request.current + ':' + #request.size + ':' + #request.keyword")
    public PageResponse<User> getUserPage(PageRequest request) {
        // 分页查询逻辑
        return PageResponse.of(result);
    }
}
```

## 🚫 禁止事项

### 1. 错误的分页实现
```java
// 禁止：在Mapper层直接处理分页
@Select("SELECT * FROM sys_user LIMIT #{offset}, #{size}")
List<User> selectUserPage(@Param("offset") int offset, @Param("size") int size);

// 禁止：手动计算分页参数
int offset = (page - 1) * size;
List<User> users = userMapper.selectUserPage(offset, size);
```

### 2. 性能问题
```java
// 禁止：查询所有数据再分页
List<User> allUsers = userMapper.selectList(null);
List<User> pageUsers = allUsers.subList(offset, offset + size);

// 禁止：不设置分页大小限制
@Max(value = 1000, message = "每页大小不能超过1000")  // 设置合理的上限
private Integer size = 10;
```

## ✅ 最佳实践

### 1. 统一分页处理
```java
@Component
public class PageHelper {
    
    public static <T> Page<T> createPage(PageRequest request) {
        return new Page<>(request.getCurrent(), request.getSize());
    }
    
    public static <T> PageResponse<T> createResponse(IPage<T> page) {
        return PageResponse.of(page);
    }
    
    public static <T> LambdaQueryWrapper<T> addKeywordCondition(
            LambdaQueryWrapper<T> wrapper, 
            String keyword, 
            SFunction<T, ?>... fields) {
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> {
                for (SFunction<T, ?> field : fields) {
                    w.like(field, keyword).or();
                }
            });
        }
        return wrapper;
    }
}
```

### 2. 分页参数验证
```java
@Validated
@RestController
public class UserController {
    
    @GetMapping("/page")
    public ResultData<PageResponse<User>> getUserPage(
            @Valid PageRequest request) {
        // 分页查询逻辑
    }
}
```

### 3. 异常处理
```java
@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ConstraintViolationException.class)
    public ResultData<String> handleValidationException(ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        return ResultData.error(ErrorCode.PARAM_ERROR, message);
    }
}
```

## 📈 监控指标

### 1. 性能监控
- **分页查询响应时间**: 目标 < 200ms
- **分页查询成功率**: 目标 > 99%
- **分页大小分布**: 监控分页大小使用情况

### 2. 业务监控
- **分页查询频率**: 监控分页查询的调用频率
- **热门查询条件**: 分析常用的查询条件
- **缓存命中率**: 监控分页查询的缓存效果

## 📝 检查清单

- [ ] 已配置MyBatis-Plus分页插件
- [ ] 已实现统一的分页请求和响应DTO
- [ ] 已在Service层处理分页逻辑
- [ ] 已设置合理的分页大小限制
- [ ] 已为分页查询字段创建索引
- [ ] 已实现分页参数验证
- [ ] 已添加异常处理机制
- [ ] 已配置性能监控

---

**版本**: v1.0  
**创建日期**: 2025-07-31  
**维护者**: scccy 
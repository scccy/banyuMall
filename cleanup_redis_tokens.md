# Redis Token清理指南

## 问题描述

由于之前的实现问题，Redis中存储了多个重复的token记录，需要进行清理。

## 清理方案

### 1. 手动清理（推荐）

#### 1.1 查看当前Redis数据
```bash
# 连接到Redis
redis-cli

# 查看所有token相关的key
KEYS token:valid:*

# 查看黑名单相关的key
KEYS token:blacklist:*
```

#### 1.2 清理重复数据
```bash
# 删除所有旧的token记录
DEL token:valid:*

# 删除所有黑名单记录（如果需要）
DEL token:blacklist:*
```

### 2. 自动化清理脚本

#### 2.1 创建清理工具类
```java
@Component
public class RedisTokenCleanupUtil {
    
    private final StringRedisTemplate redisTemplate;
    
    @Value("${jwt.valid-token-prefix}")
    private String validTokenPrefix;
    
    @Value("${jwt.blacklist-prefix}")
    private String blacklistPrefix;
    
    /**
     * 清理所有token相关数据
     */
    public void cleanupAllTokens() {
        // 清理有效token
        cleanupValidTokens();
        // 清理黑名单
        cleanupBlacklist();
    }
    
    /**
     * 清理有效token数据
     */
    public void cleanupValidTokens() {
        Set<String> keys = redisTemplate.keys(validTokenPrefix + "*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
            log.info("已清理 {} 个有效token记录", keys.size());
        }
    }
    
    /**
     * 清理黑名单数据
     */
    public void cleanupBlacklist() {
        Set<String> keys = redisTemplate.keys(blacklistPrefix + "*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
            log.info("已清理 {} 个黑名单记录", keys.size());
        }
    }
}
```

#### 2.2 创建清理接口
```java
@RestController
@RequestMapping("/admin/redis")
public class RedisCleanupController {
    
    private final RedisTokenCleanupUtil cleanupUtil;
    
    @PostMapping("/cleanup/tokens")
    public ResultData<String> cleanupTokens() {
        cleanupUtil.cleanupAllTokens();
        return ResultData.ok("Redis token数据清理完成");
    }
}
```

### 3. 监控和预防

#### 3.1 添加监控指标
```java
@Component
public class TokenStorageMonitor {
    
    private final StringRedisTemplate redisTemplate;
    
    @Value("${jwt.valid-token-prefix}")
    private String validTokenPrefix;
    
    /**
     * 获取当前token存储数量
     */
    public long getValidTokenCount() {
        Set<String> keys = redisTemplate.keys(validTokenPrefix + "*");
        return keys != null ? keys.size() : 0;
    }
    
    /**
     * 检查是否有重复存储
     */
    public boolean hasDuplicateStorage() {
        long count = getValidTokenCount();
        // 如果token数量超过用户数量的一定倍数，可能存在重复
        return count > 1000; // 根据实际情况调整阈值
    }
}
```

#### 3.2 定期清理任务
```java
@Component
@EnableScheduling
public class TokenCleanupScheduler {
    
    private final TokenStorageMonitor monitor;
    private final RedisTokenCleanupUtil cleanupUtil;
    
    @Scheduled(cron = "0 0 2 * * ?") // 每天凌晨2点执行
    public void scheduledCleanup() {
        if (monitor.hasDuplicateStorage()) {
            log.warn("检测到token重复存储，开始清理...");
            cleanupUtil.cleanupValidTokens();
        }
    }
}
```

## 验证清理效果

### 1. 清理前检查
```bash
# 查看清理前的数据量
redis-cli
INFO memory
KEYS token:valid:* | wc -l
```

### 2. 执行清理
```bash
# 执行清理操作
# 使用上述任一方法
```

### 3. 清理后验证
```bash
# 查看清理后的数据量
redis-cli
INFO memory
KEYS token:valid:* | wc -l

# 测试登录功能
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"test","password":"123456"}'
```

### 4. 监控新数据
```bash
# 多次登录测试
for i in {1..5}; do
  curl -X POST http://localhost:8080/auth/login \
    -H "Content-Type: application/json" \
    -d '{"username":"test","password":"123456"}'
  echo "第 $i 次登录完成"
  sleep 1
done

# 检查Redis中的数据
redis-cli
KEYS token:valid:*
```

## 预期效果

### 清理前
- Redis中存储了大量重复的token记录
- 每个用户多次登录产生多个token记录
- 内存占用较高

### 清理后
- 每个用户只存储一个token记录
- 内存占用显著减少
- 数据管理更加规范

## 注意事项

1. **备份数据**: 清理前建议备份Redis数据
2. **测试验证**: 清理后需要测试登录功能
3. **监控告警**: 添加监控避免问题再次发生
4. **渐进实施**: 可以先在测试环境验证

## 长期维护

1. **定期监控**: 定期检查token存储情况
2. **自动清理**: 设置定时任务自动清理
3. **告警机制**: 当检测到异常时及时告警
4. **文档更新**: 更新相关文档和规范 
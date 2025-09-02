package com.origin.banyu.coreflink.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Flink 配置类
 * 统一管理Flink配置属性
 * 
 * @author Linus Torvalds
 * @since 2024-01-01
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "flink")
public class FlinkConfig {
    
    /**
     * 作业配置
     */
    private Job job = new Job();
    
    /**
     * 检查点配置
     */
    private Checkpoint checkpoint = new Checkpoint();
    
    /**
     * 并行度配置
     */
    private Parallelism parallelism = new Parallelism();
    
    /**
     * 作业配置
     */
    @Data
    public static class Job {
        /**
         * 作业名称
         */
        private String name = "banyu-realtime-processor";
        
        /**
         * 作业描述
         */
        private String description = "Banyu实时数据处理作业";
    }
    
    /**
     * 检查点配置
     */
    @Data
    public static class Checkpoint {
        /**
         * 检查点间隔（毫秒）
         */
        private long interval = 60000;
        
        /**
         * 检查点超时时间（毫秒）
         */
        private long timeout = 30000;
        
        /**
         * 检查点最小暂停时间（毫秒）
         */
        private long minPause = 5000;
        
        /**
         * 最大并发检查点数
         */
        private int maxConcurrent = 1;
        
        /**
         * 检查点存储路径
         */
        private String storagePath = "file:///tmp/flink-checkpoints";
        
        /**
         * 是否启用检查点
         */
        private boolean enabled = true;
    }
    
    /**
     * 并行度配置
     */
    @Data
    public static class Parallelism {
        /**
         * 默认并行度
         */
        private int defaultParallelism = 2;
        
        /**
         * 数据源并行度
         */
        private int source = 2;
        
        /**
         * 数据汇并行度
         */
        private int sink = 2;
        
        /**
         * 处理器并行度
         */
        private int processor = 2;
    }
}

package com.origin.banyu.coreflink;

import com.origin.banyu.coreflink.service.BaseFlinkJobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import java.util.Comparator;
import java.util.List;

/**
 * Flink 实时数据处理应用启动类
 *
 * @author Linus Torvalds
 * @since 2024-01-01
 */
@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
public class CoreFlinkApplication {

    private final List<BaseFlinkJobService> jobServices;

    public static void main(String[] args) {
        SpringApplication.run(CoreFlinkApplication.class, args);
    }

    /**
     * 应用启动完成后自动启动所有Job
     */
    @EventListener(ApplicationReadyEvent.class)
    public void autoStartJobs() {
        log.info("Application ready, starting Flink Jobs...");
        
        // 按优先级排序
        List<BaseFlinkJobService> sortedServices = jobServices.stream()
                .filter(BaseFlinkJobService::isAutoStart)
                .sorted(Comparator.comparingInt(BaseFlinkJobService::getPriority))
                .toList();

        log.info("Found {} auto-start jobs", sortedServices.size());

        for (BaseFlinkJobService service : sortedServices) {
            try {
                String jobName = service.getJobName();
                log.info("Starting job: {} (Priority: {})", jobName, service.getPriority());
                
                service.startJob();
                log.info("Successfully started job: {}", jobName);
            } catch (Exception e) {
                log.error("Failed to start job: {}", service.getJobName(), e);
            }
        }
        
        log.info("Flink Jobs auto-start completed");
    }
}

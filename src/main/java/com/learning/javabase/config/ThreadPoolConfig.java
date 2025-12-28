package com.learning.javabase.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池配置类
 *
 * 设计目的：
 * 1. 替代默认的Executors创建方式，避免OOM风险。
 * 2. 统一管理系统中的线程资源。
 *
 * 学习重点 (Phase 2 - JUC):
 * - 核心参数：corePoolSize, maxPoolSize, queueCapacity, keepAliveTime
 * - 拒绝策略：CallerRunsPolicy (主线程执行) vs AbortPolicy (抛异常)
 */
@Slf4j
@Configuration
public class ThreadPoolConfig {

    @Bean("commonThreadPool")
    public ThreadPoolTaskExecutor commonThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 核心线程数：根据CPU核数设置，IO密集型一般为 2n
        executor.setCorePoolSize(Runtime.getRuntime().availableProcessors() * 2);
        // 最大线程数
        executor.setMaxPoolSize(50);
        // 队列容量：重要！防止堆积过多任务导致OOM
        executor.setQueueCapacity(1000);
        // 线程活跃时间
        executor.setKeepAliveSeconds(60);
        // 线程名前缀，方便日志排查
        executor.setThreadNamePrefix("common-exec-");
        // 拒绝策略：由调用线程处理该任务 (背压机制)
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        
        executor.initialize();
        log.info("通用线程池初始化完成|Common_thread_pool_initialized,coreSize={},maxSize={}", 
                executor.getCorePoolSize(), executor.getMaxPoolSize());
        return executor;
    }
}

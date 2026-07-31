package com.himpact.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Asynchronous Event Processing Configuration.
 * Configures a dedicated ThreadPoolTaskExecutor for async domain event listeners (@Async).
 *
 * See: PO Review — Architecture Improvement 2 (Verify Event Processing Model)
 * See: project-index/05_Software_Architecture.md — Event-Driven Model
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("himpact-async-");
        executor.initialize();
        return executor;
    }
}

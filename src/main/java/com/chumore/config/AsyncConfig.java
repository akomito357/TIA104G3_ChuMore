package com.chumore.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfig {

    @Bean(name = "taskExecutor") // 指定 Bean 名稱
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5); // 核心執行緒數量
        executor.setMaxPoolSize(10); // 最大執行緒數量
        executor.setQueueCapacity(100); // 任務queue大小
        executor.setThreadNamePrefix("AsyncThread-"); // 執行緒名稱前綴，方便分析
        executor.initialize();
        return executor;
    }
}
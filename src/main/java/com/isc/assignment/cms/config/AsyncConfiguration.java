package com.isc.assignment.cms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfiguration {

    public static final String EXECUTOR_NAME = "cmsExecutor";

    @Bean(name = EXECUTOR_NAME)
    public Executor customExecutor() {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(5);
        executor.setKeepAliveSeconds(30);
        executor.setThreadNamePrefix("AsyncUpdateCatch-");
        executor.initialize();
        return executor;
    }
}

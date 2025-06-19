package com.adrian.challenge.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfig {

    @Bean("orderExecutor")
    public Executor orderExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(50);          // hilos activos mínimos
        executor.setMaxPoolSize(200);          // máximo simultáneo
        executor.setQueueCapacity(500);        // cola de tareas en espera
        executor.setThreadNamePrefix("OrderThread-");
        executor.initialize();
        return executor;
    }
}

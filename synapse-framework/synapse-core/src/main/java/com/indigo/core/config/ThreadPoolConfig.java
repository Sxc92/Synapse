package com.indigo.core.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.*;

/**
 * Thread pool configuration
 * Different thread pools for different scenarios
 * 
 * @author 史偕成
 * @date 2025/04/24 21:57
 **/
@Slf4j
@Configuration
@EnableAsync
public class ThreadPoolConfig {

    /**
     * IO密集型任务线程池
     * 核心线程数 = CPU核心数 * 2
     * 最大线程数 = CPU核心数 * 4
     */
    @Bean("ioThreadPool")
    public ThreadPoolTaskExecutor ioThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        int processors = Runtime.getRuntime().availableProcessors();
        executor.setCorePoolSize(processors * 2);
        executor.setMaxPoolSize(processors * 4);
        executor.setQueueCapacity(1000);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("io-thread-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

    /**
     * CPU密集型任务线程池
     * 核心线程数 = CPU核心数 + 1
     * 最大线程数 = CPU核心数 + 1
     */
    @Bean("cpuThreadPool")
    public ThreadPoolTaskExecutor cpuThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        int processors = Runtime.getRuntime().availableProcessors();
        executor.setCorePoolSize(processors + 1);
        executor.setMaxPoolSize(processors + 1);
        executor.setQueueCapacity(1000);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("cpu-thread-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

    /**
     * 定时任务线程池
     * 核心线程数 = CPU核心数
     */
    @Bean("scheduledThreadPool")
    public ScheduledExecutorService scheduledThreadPool() {
        int processors = Runtime.getRuntime().availableProcessors();
        return new ScheduledThreadPoolExecutor(processors,
                new ThreadFactory() {
                    private int counter = 1;
                    @Override
                    public Thread newThread(Runnable r) {
                        Thread thread = new Thread(r);
                        thread.setName("scheduled-thread-" + counter++);
                        thread.setDaemon(true);
                        return thread;
                    }
                },
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

    /**
     * 通用线程池
     * 用于处理一般的异步任务
     */
    @Bean("commonThreadPool")
    public ThreadPoolTaskExecutor commonThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(2000);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("common-thread-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

    /**
     * 监控线程池
     * 用于处理监控、日志等非关键任务
     */
    @Bean("monitorThreadPool")
    public ThreadPoolTaskExecutor monitorThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(100);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("monitor-thread-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
        executor.initialize();
        return executor;
    }
} 
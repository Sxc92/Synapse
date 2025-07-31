package com.indigo.core.utils;

import com.indigo.core.exception.ThreadException;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;
import java.util.function.Supplier;

/**
 * Thread utility class
 * Provides convenient methods to use thread pools
 * 
 * @author 史偕成
 * @date 2025/04/24 21:57
 **/
@Slf4j
@Component
public class ThreadUtils {

    private final ThreadPoolTaskExecutor ioThreadPool;
    private final ThreadPoolTaskExecutor cpuThreadPool;
    private final ScheduledExecutorService scheduledThreadPool;
    private final ThreadPoolTaskExecutor commonThreadPool;
    private final ThreadPoolTaskExecutor monitorThreadPool;

    public ThreadUtils(
            @Qualifier("ioThreadPool") ThreadPoolTaskExecutor ioThreadPool,
            @Qualifier("cpuThreadPool") ThreadPoolTaskExecutor cpuThreadPool,
            ScheduledExecutorService scheduledThreadPool,
            @Qualifier("commonThreadPool") ThreadPoolTaskExecutor commonThreadPool,
            @Qualifier("monitorThreadPool") ThreadPoolTaskExecutor monitorThreadPool) {
        this.ioThreadPool = ioThreadPool;
        this.cpuThreadPool = cpuThreadPool;
        this.scheduledThreadPool = scheduledThreadPool;
        this.commonThreadPool = commonThreadPool;
        this.monitorThreadPool = monitorThreadPool;
    }

    /**
     * 执行IO密集型任务
     */
    public void executeIoTask(Runnable task) {
        if (task == null) {
            throw new ThreadException("Task cannot be null");
        }
        ioThreadPool.execute(wrapRunnable(task));
    }

    /**
     * 执行CPU密集型任务
     */
    public void executeCpuTask(Runnable task) {
        if (task == null) {
            throw new ThreadException("Task cannot be null");
        }
        cpuThreadPool.execute(wrapRunnable(task));
    }

    /**
     * 执行通用任务
     */
    public void executeCommonTask(Runnable task) {
        if (task == null) {
            throw new ThreadException("Task cannot be null");
        }
        commonThreadPool.execute(wrapRunnable(task));
    }

    /**
     * 执行监控任务
     */
    public void executeMonitorTask(Runnable task) {
        if (task == null) {
            throw new ThreadException("Task cannot be null");
        }
        monitorThreadPool.execute(wrapRunnable(task));
    }

    /**
     * 提交IO密集型任务并返回Future
     */
    public <T> Future<T> submitIoTask(Callable<T> task) {
        if (task == null) {
            throw new ThreadException("Task cannot be null");
        }
        return ioThreadPool.submit(wrapCallable(task));
    }

    /**
     * 提交CPU密集型任务并返回Future
     */
    public <T> Future<T> submitCpuTask(Callable<T> task) {
        if (task == null) {
            throw new ThreadException("Task cannot be null");
        }
        return cpuThreadPool.submit(wrapCallable(task));
    }

    /**
     * 提交通用任务并返回Future
     */
    public <T> Future<T> submitCommonTask(Callable<T> task) {
        if (task == null) {
            throw new ThreadException("Task cannot be null");
        }
        return commonThreadPool.submit(wrapCallable(task));
    }

    /**
     * 提交监控任务并返回Future
     */
    public <T> Future<T> submitMonitorTask(Callable<T> task) {
        if (task == null) {
            throw new ThreadException("Task cannot be null");
        }
        return monitorThreadPool.submit(wrapCallable(task));
    }

    /**
     * 执行定时任务
     */
    public ScheduledFuture<?> schedule(Runnable task, long delay, TimeUnit unit) {
        if (task == null) {
            throw new ThreadException("Task cannot be null");
        }
        if (unit == null) {
            throw new ThreadException("TimeUnit cannot be null");
        }
        return scheduledThreadPool.schedule(wrapRunnable(task), delay, unit);
    }

    /**
     * 执行定时任务并返回Future
     */
    public <T> ScheduledFuture<T> schedule(Callable<T> task, long delay, TimeUnit unit) {
        if (task == null) {
            throw new ThreadException("Task cannot be null");
        }
        if (unit == null) {
            throw new ThreadException("TimeUnit cannot be null");
        }
        return scheduledThreadPool.schedule(wrapCallable(task), delay, unit);
    }

    /**
     * 执行周期性任务
     */
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, long initialDelay, long period, TimeUnit unit) {
        if (task == null) {
            throw new ThreadException("Task cannot be null");
        }
        if (unit == null) {
            throw new ThreadException("TimeUnit cannot be null");
        }
        return scheduledThreadPool.scheduleAtFixedRate(wrapRunnable(task), initialDelay, period, unit);
    }

    /**
     * 执行带延迟的周期性任务
     */
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, long initialDelay, long delay, TimeUnit unit) {
        if (task == null) {
            throw new ThreadException("Task cannot be null");
        }
        if (unit == null) {
            throw new ThreadException("TimeUnit cannot be null");
        }
        return scheduledThreadPool.scheduleWithFixedDelay(wrapRunnable(task), initialDelay, delay, unit);
    }

    /**
     * 异步执行任务并返回CompletableFuture
     */
    public <T> CompletableFuture<T> supplyAsync(Supplier<T> supplier) {
        if (supplier == null) {
            throw new ThreadException("Supplier cannot be null");
        }
        return CompletableFuture.supplyAsync(supplier, commonThreadPool);
    }

    /**
     * 异步执行任务
     */
    public CompletableFuture<Void> runAsync(Runnable runnable) {
        if (runnable == null) {
            throw new ThreadException("Runnable cannot be null");
        }
        return CompletableFuture.runAsync(runnable, commonThreadPool);
    }

    /**
     * 包装Runnable，添加异常处理
     */
    private Runnable wrapRunnable(Runnable runnable) {
        return () -> {
            try {
                runnable.run();
            } catch (Exception e) {
                log.error("Task execution failed", e);
                if (e instanceof RuntimeException) {
                    throw e; // 保留原始异常
                } else {
                    throw new ThreadException(e.getMessage(), e);
                }
            }
        };
    }

    /**
     * 包装Callable，添加异常处理
     */
    private <T> Callable<T> wrapCallable(Callable<T> callable) {
        return () -> {
            try {
                return callable.call();
            } catch (Exception e) {
                log.error("Task execution failed", e);
                if (e instanceof RuntimeException) {
                    throw e; // 保留原始异常
                } else {
                    throw new ThreadException(e.getMessage(), e);
                }
            }
        };
    }

    /**
     * 获取线程池状态
     */
    public ThreadPoolStatus getThreadPoolStatus() {
        return ThreadPoolStatus.builder()
                .ioThreadPool(getExecutorStatus(ioThreadPool))
                .cpuThreadPool(getExecutorStatus(cpuThreadPool))
                .commonThreadPool(getExecutorStatus(commonThreadPool))
                .monitorThreadPool(getExecutorStatus(monitorThreadPool))
                .build();
    }

    private ExecutorStatus getExecutorStatus(ThreadPoolTaskExecutor executor) {
        if (executor == null) {
            throw new ThreadException("Executor cannot be null");
        }
        return ExecutorStatus.builder()
                .poolSize(executor.getPoolSize())
                .activeThreads(executor.getActiveCount())
                .queueSize(executor.getThreadPoolExecutor().getQueue().size())
                .completedTasks(executor.getThreadPoolExecutor().getCompletedTaskCount())
                .totalTasks(executor.getThreadPoolExecutor().getTaskCount())
                .build();
    }

    /**
     * 线程池状态信息
     */
    @Data
    @Builder
    public static class ThreadPoolStatus {
        private final ExecutorStatus ioThreadPool;
        private final ExecutorStatus cpuThreadPool;
        private final ExecutorStatus commonThreadPool;
        private final ExecutorStatus monitorThreadPool;
    }

    /**
     * 执行器状态信息
     */
    @Data
    @Builder
    public static class ExecutorStatus {
        private final int poolSize;
        private final int activeThreads;
        private final int queueSize;
        private final long completedTasks;
        private final long totalTasks;
    }
} 
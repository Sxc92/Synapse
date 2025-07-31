package com.indigo.synapse.databases;

import com.indigo.databases.dynamic.DynamicDataSourceContextHolder;
import com.indigo.databases.dynamic.DynamicRoutingDataSource;
import com.indigo.databases.enums.DataSourceType;
import com.indigo.databases.health.DataSourceHealthChecker;
import com.indigo.databases.loadbalance.DataSourceLoadBalancer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.jdbc.CannotGetJdbcConnectionException;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 动态数据源集成测试
 *
 * @author 史偕成
 * @date 2024/03/21
 */
@Slf4j
@SpringBootTest
@ActiveProfiles("test")
class DynamicDataSourceIntegrationTest {

    @Autowired
    private DynamicRoutingDataSource routingDataSource;

    @Autowired
    private DataSourceHealthChecker healthChecker;

    @Autowired
    private DataSourceLoadBalancer loadBalancer;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        // 等待数据源初始化
        waitForDataSourcesInitialization();
        
        // 清理测试表
        DynamicDataSourceContextHolder.setDataSource("master1");
        jdbcTemplate.execute("DROP TABLE IF EXISTS test_table");
        jdbcTemplate.execute("CREATE TABLE test_table (id INT PRIMARY KEY)");
        DynamicDataSourceContextHolder.clear();
    }

    @AfterEach
    void tearDown() {
        // 清理测试表
        DynamicDataSourceContextHolder.setDataSource("master1");
        jdbcTemplate.execute("DROP TABLE IF EXISTS test_table");
        DynamicDataSourceContextHolder.clear();
    }

    /**
     * 等待数据源初始化完成
     */
    private void waitForDataSourcesInitialization() {
        int maxRetries = 3;
        int retryCount = 0;
        while (retryCount < maxRetries) {
            try {
                // 触发健康检查
                healthChecker.checkHealthAndWait();
                
                // 检查所有数据源是否健康
                Map<String, Boolean> healthStatus = healthChecker.getHealthStatus();
                if (!healthStatus.isEmpty() && healthStatus.values().stream().allMatch(Boolean::booleanValue)) {
                    log.info("All data sources initialized successfully");
                    return;
                }
                
                retryCount++;
                if (retryCount < maxRetries) {
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        fail("Failed to initialize data sources after " + maxRetries + " retries");
    }

    @Test
    void testHealthChecker() {
        // 触发健康检查
        healthChecker.checkHealthAndWait();
        
        // 获取健康状态
        Map<String, Boolean> healthStatus = healthChecker.getHealthStatus();
        
        // 验证健康状态不为空
        assertFalse(healthStatus.isEmpty(), "Health status should not be empty");
        
        // 验证所有数据源都是健康的
        assertTrue(healthStatus.values().stream().allMatch(Boolean::booleanValue),
                "All data sources should be healthy");
    }

    @Test
    void testLoadBalancer() {
        // 获取主数据源
        String masterDataSource = loadBalancer.getDataSource(DataSourceType.MASTER);
        assertNotNull(masterDataSource, "Should get a master data source");
        assertTrue(masterDataSource.startsWith("master"), "Should be a master data source");
        
        // 获取从数据源
        String slaveDataSource = loadBalancer.getDataSource(DataSourceType.SLAVE);
        assertNotNull(slaveDataSource, "Should get a slave data source");
        assertTrue(slaveDataSource.startsWith("slave"), "Should be a slave data source");
    }

    @Test
    void testLoadBalancerRoundRobin() {
        // 获取所有可用的主数据源
        List<String> masterDataSources = loadBalancer.getAvailableDataSources(DataSourceType.MASTER);
        assertFalse(masterDataSources.isEmpty(), "Should have available master data sources");
        
        // 多次获取主数据源，验证轮询
        Set<String> selectedDataSources = java.util.stream.Stream.generate(() -> 
                loadBalancer.getDataSource(DataSourceType.MASTER))
                .limit(masterDataSources.size() * 2)
                .collect(Collectors.toSet());
        
        // 验证所有主数据源都被选中
        assertTrue(selectedDataSources.containsAll(masterDataSources),
                "Load balancer should distribute across multiple master data sources");
    }

    @Test
    void testDataSourceOperations() {
        // 写入主库
        DynamicDataSourceContextHolder.setDataSource("master1");
        jdbcTemplate.execute("INSERT INTO test_table (id) VALUES (1)");
        DynamicDataSourceContextHolder.clear();
        
        // 从从库读取
        DynamicDataSourceContextHolder.setDataSource("slave1");
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM test_table", Integer.class);
        DynamicDataSourceContextHolder.clear();
        
        assertEquals(1, count, "Should read the data from slave");
    }

    @Test
    void testDataSourceFailureHandling() {
        // 模拟数据源故障
        String tempMaster = "temp_master";
        routingDataSource.addDataSource(tempMaster, routingDataSource.getDataSources().get("master1"));
        
        // 标记数据源为不健康
        healthChecker.checkHealthAndWait();
        
        // 尝试获取数据源
        String selectedDataSource = loadBalancer.getDataSource(DataSourceType.MASTER);
        assertNotEquals(tempMaster, selectedDataSource, "Load balancer should not select failed data source");
        
        // 清理临时数据源
        routingDataSource.removeDataSource(tempMaster);
    }

    @Test
    void testAutoDataSourceSwitching() {
        // 写入主库
        jdbcTemplate.execute("INSERT INTO test_table (id) VALUES (1)");
        
        // 从从库读取
        DynamicDataSourceContextHolder.setDataSource("slave1");
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM test_table", Integer.class);
        DynamicDataSourceContextHolder.clear();
        
        assertEquals(1, count, "Should read the data from slave");
    }

    @Test
    void testStrictMode() {
        // 尝试使用不存在的数据源
        DynamicDataSourceContextHolder.setDataSource("non_existent_ds");
        CannotGetJdbcConnectionException ex = assertThrows(CannotGetJdbcConnectionException.class, () -> 
                jdbcTemplate.queryForObject("SELECT 1", Integer.class),
                "Should throw CannotGetJdbcConnectionException when using non-existent data source");
        assertTrue(ex.getCause() instanceof IllegalStateException);
        DynamicDataSourceContextHolder.clear();
    }

    @Test
    @Transactional
    void testConcurrentOperations() throws InterruptedException {
        int threadCount = 6;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        
        // 创建多个线程并发操作
        for (int i = 0; i < threadCount; i++) {
            final int id = i + 1;
            executorService.submit(() -> {
                try {
                    // 写入主库
                    DynamicDataSourceContextHolder.setDataSource("master1");
                    jdbcTemplate.execute("INSERT INTO test_table (id) VALUES (" + id + ")");
                    DynamicDataSourceContextHolder.clear();
                    
                    // 从从库读取
                    DynamicDataSourceContextHolder.setDataSource("slave1");
                    Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM test_table", Integer.class);
                    DynamicDataSourceContextHolder.clear();
                    
                    assertTrue(count > 0, "Should read data from slave");
                } finally {
                    latch.countDown();
                }
            });
        }
        
        // 等待所有线程完成
        assertTrue(latch.await(10, TimeUnit.SECONDS), "All threads should complete within timeout");
        executorService.shutdown();
    }
} 
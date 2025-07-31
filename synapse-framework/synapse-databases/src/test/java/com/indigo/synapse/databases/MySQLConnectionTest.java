package com.indigo.synapse.databases;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import javax.sql.DataSource;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = TestConfig.class)
@ActiveProfiles("test")
public class MySQLConnectionTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void testDatabaseConnection() {
        assertNotNull(dataSource, "DataSource should not be null");
        assertNotNull(jdbcTemplate, "JdbcTemplate should not be null");

        // Test the connection by executing a simple query
        String result = jdbcTemplate.queryForObject("SELECT DATABASE()", String.class);
        assertEquals("indigo", result, "Database name should be 'indigo'");
    }
} 
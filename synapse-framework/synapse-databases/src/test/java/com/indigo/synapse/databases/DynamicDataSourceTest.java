package com.indigo.synapse.databases;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = TestConfig.class)
@ActiveProfiles("test")
public class DynamicDataSourceTest {
    private static final Logger logger = LoggerFactory.getLogger(DynamicDataSourceTest.class);

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void testMasterDataSource() {
        assertNotNull(dataSource, "DataSource should not be null");
        assertNotNull(jdbcTemplate, "JdbcTemplate should not be null");

        // Test database connection
        String database = jdbcTemplate.queryForObject("SELECT DATABASE()", String.class);
        assertEquals("indigo", database, "Database name should be indigo");

        // Test character encoding
        String charset = jdbcTemplate.queryForObject(
            "SHOW VARIABLES LIKE 'character_set_connection'", 
            (rs, rowNum) -> rs.getString("Value")
        );
        assertEquals("utf8mb4", charset, "Character encoding should be utf8mb4");

        // Test timezone
        String timezone = jdbcTemplate.queryForObject(
            "SHOW VARIABLES LIKE 'time_zone'",
            (rs, rowNum) -> rs.getString("Value")
        );
        assertNotNull(timezone, "Timezone should not be null");
    }
} 
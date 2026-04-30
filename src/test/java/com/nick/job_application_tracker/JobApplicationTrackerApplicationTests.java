package com.nick.job_application_tracker;

import static org.assertj.core.api.Assertions.assertThat;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class JobApplicationTrackerApplicationTests {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Test
    void contextLoads() {
        assertThat(dataSource).isNotNull();
    }

    @Test
    void usesTestDatasourceAndCanRunSimpleQuery() {
        assertThat(datasourceUrl).startsWith("jdbc:h2:mem:testdb");
        Integer result = jdbcTemplate.queryForObject("select 1", Integer.class);
        assertThat(result).isEqualTo(1);
    }
}

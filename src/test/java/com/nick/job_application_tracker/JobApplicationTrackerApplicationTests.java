package com.nick.job_application_tracker;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

// @ActiveProfiles("test")
@SpringBootTest
class JobApplicationTrackerApplicationTests {

	@Test
	void contextLoads() {
	}
	@Value("${spring.datasource.url}")
	private String datasourceUrl;

	@Test
	void testDatabaseConnection() {
		System.out.println("Datasource URL: " + datasourceUrl);
		// Your test logic here
	}
}

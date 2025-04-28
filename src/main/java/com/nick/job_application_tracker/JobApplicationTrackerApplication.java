package com.nick.job_application_tracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JobApplicationTrackerApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(JobApplicationTrackerApplication.class);
        app.run(args);
    }
}

package com.nick.job_application_tracker.dto;

public class JobApplicationDTO {
    public Long id;
    public String jobTitle;
    public String company;
    public String status;

    public JobApplicationDTO(Long id, String jobTitle, String company, String status) {
        this.id = id;
        this.jobTitle = jobTitle;
        this.company = company;
        this.status = status;
    }
}

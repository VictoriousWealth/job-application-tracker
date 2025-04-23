package com.nick.job_application_tracker.dto;

public class JobApplicationResponseDTO {
    public Long id;
    public String jobTitle;
    public String company;
    public String status;
    public String notes;
    public String appliedOn;
    public Long userId;
    public Long locationId;
    public Long sourceId;
    public Long resumeId;
    public Long coverLetterId;

    public JobApplicationResponseDTO() {}
}

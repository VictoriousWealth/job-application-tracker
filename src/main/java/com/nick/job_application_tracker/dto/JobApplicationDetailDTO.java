package com.nick.job_application_tracker.dto;

public class JobApplicationDetailDTO {
    public Long id;
    public String jobTitle;
    public String company;
    public String status;
    public String notes;
    public String appliedOn;
    
    public ResumeDTO resume;
    public CoverLetterDTO coverLetter;
    public JobSourceDTO source;
    public LocationDTO location;
    public UserInfoDTO user;

    // Optional: use Jackson @JsonInclude or Lombok
}

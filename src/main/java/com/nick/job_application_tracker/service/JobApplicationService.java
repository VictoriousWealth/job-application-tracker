package com.nick.job_application_tracker.service;

import com.nick.job_application_tracker.model.JobApplication;
import com.nick.job_application_tracker.repository.JobApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class JobApplicationService {

    private final JobApplicationRepository jobApplicationRepository;

    @Autowired
    public JobApplicationService(JobApplicationRepository jobApplicationRepository) {
        this.jobApplicationRepository = jobApplicationRepository;
    }

    public List<JobApplication> getAllApplicationsForUser(Long userId) {
        return jobApplicationRepository.findByUserId(userId);
    }

    public Optional<JobApplication> getById(Long id) {
        return jobApplicationRepository.findById(id);
    }

    public JobApplication createOrUpdate(JobApplication jobApplication) {
        return jobApplicationRepository.save(jobApplication);
    }

    public void deleteById(Long id) {
        jobApplicationRepository.deleteById(id);
    }

    public List<JobApplication> getByStatus(JobApplication.Status status) {
        return jobApplicationRepository.findByStatus(status);
    }
}

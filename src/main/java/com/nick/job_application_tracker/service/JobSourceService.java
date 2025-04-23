package com.nick.job_application_tracker.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.nick.job_application_tracker.model.JobSource;
import com.nick.job_application_tracker.repository.JobSourceRepository;

@Service
public class JobSourceService {

    private final JobSourceRepository jobSourceRepository;

    public JobSourceService(JobSourceRepository jobSourceRepository) {
        this.jobSourceRepository = jobSourceRepository;
    }

    public List<JobSource> findAll() {
        return jobSourceRepository.findAll();
    }

    public JobSource save(JobSource source) {
        return jobSourceRepository.save(source);
    }

    public void delete(Long id) {
        jobSourceRepository.deleteById(id);
    }
}

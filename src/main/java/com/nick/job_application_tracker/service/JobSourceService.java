package com.nick.job_application_tracker.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.nick.job_application_tracker.dto.JobSourceCreateDTO;
import com.nick.job_application_tracker.dto.JobSourceDTO;
import com.nick.job_application_tracker.model.JobSource;
import com.nick.job_application_tracker.repository.JobSourceRepository;

@Service
public class JobSourceService {

    private final JobSourceRepository jobSourceRepository;

    public JobSourceService(JobSourceRepository jobSourceRepository) {
        this.jobSourceRepository = jobSourceRepository;
    }

    public List<JobSourceDTO> getAllSources() {
        return jobSourceRepository.findAll().stream()
                .map(source -> new JobSourceDTO(source.getId(), source.getName()))
                .collect(Collectors.toList());
    }

    public JobSourceDTO createSource(JobSourceCreateDTO createDTO) {
        JobSource source = new JobSource();
        source.setName(createDTO.getName());
        JobSource saved = jobSourceRepository.save(source);
        return new JobSourceDTO(saved.getId(), saved.getName());
    }

    public Optional<JobSourceDTO> getSourceById(Long id) {
        return jobSourceRepository.findById(id)
                .map(source -> new JobSourceDTO(source.getId(), source.getName()));
    }

    public Optional<JobSourceDTO> updateSource(Long id, JobSourceCreateDTO createDTO) {
        return jobSourceRepository.findById(id).map(source -> {
            source.setName(createDTO.getName());
            JobSource updated = jobSourceRepository.save(source);
            return new JobSourceDTO(updated.getId(), updated.getName());
        });
    }

    public void deleteSource(Long id) {
        jobSourceRepository.deleteById(id);
    }
}

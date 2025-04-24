package com.nick.job_application_tracker.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.nick.job_application_tracker.dto.JobSourceCreateDTO;
import com.nick.job_application_tracker.dto.JobSourceDTO;
import com.nick.job_application_tracker.mapper.JobSourceMapper;
import com.nick.job_application_tracker.model.JobSource;
import com.nick.job_application_tracker.repository.JobSourceRepository;

@Service
public class JobSourceService {

    private final JobSourceRepository jobSourceRepository;
    private final JobSourceMapper mapper;

    public JobSourceService(JobSourceRepository jobSourceRepository, JobSourceMapper mapper) {
        this.jobSourceRepository = jobSourceRepository;
        this.mapper = mapper;
    }

    public List<JobSourceDTO> getAllSources() {
        return jobSourceRepository.findAll().stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    public JobSourceDTO createSource(JobSourceCreateDTO createDTO) {
        JobSource source = mapper.toEntity(createDTO);
        return mapper.toDTO(jobSourceRepository.save(source));
    }

    public Optional<JobSourceDTO> getSourceById(Long id) {
        return jobSourceRepository.findById(id)
                .map(mapper::toDTO);
    }

    public Optional<JobSourceDTO> updateSource(Long id, JobSourceCreateDTO createDTO) {
        return jobSourceRepository.findById(id).map(source -> {
            source.setName(createDTO.getName());
            return mapper.toDTO(jobSourceRepository.save(source));
        });
    }

    public void deleteSource(Long id) {
        jobSourceRepository.deleteById(id);
    }
}

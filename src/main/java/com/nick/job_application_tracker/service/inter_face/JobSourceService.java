package com.nick.job_application_tracker.service.inter_face;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.nick.job_application_tracker.dto.JobSourceDTO;
import com.nick.job_application_tracker.dto.create.JobSourceCreateDTO;
import com.nick.job_application_tracker.mapper.JobSourceMapper;
import com.nick.job_application_tracker.model.JobSource;
import com.nick.job_application_tracker.repository.inter_face.JobSourceRepository;

@Service
public class JobSourceService {

    private final JobSourceRepository jobSourceRepository;
    private final JobSourceMapper mapper;
    private final AuditLogService auditLogService;

    public JobSourceService(JobSourceRepository jobSourceRepository, JobSourceMapper mapper, AuditLogService auditLogService) {
        this.jobSourceRepository = jobSourceRepository;
        this.mapper = mapper;
        this.auditLogService = auditLogService;
    }

    public List<JobSourceDTO> getAllSources() {
        return jobSourceRepository.findAll().stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    public JobSourceDTO createSource(JobSourceCreateDTO createDTO) {
        JobSource source = mapper.toEntity(createDTO);
        JobSource savedSource = jobSourceRepository.save(source);
        auditLogService.logCreate("Created JobSource with ID " + savedSource.getId() + " and name '" + savedSource.getName() + "'");
        return mapper.toDTO(savedSource);
    }

    public JobSource getModelById(UUID id) {
        return jobSourceRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Job source not found"));
    }

    public Optional<JobSourceDTO> getSourceById(UUID id) {
        return jobSourceRepository.findById(id)
                .map(mapper::toDTO);
    }

    public Optional<JobSourceDTO> updateSource(UUID id, JobSourceCreateDTO createDTO) {
        return jobSourceRepository.findById(id).map(source -> {
            source.setName(createDTO.getName());
            JobSource updatedSource = jobSourceRepository.save(source);
            auditLogService.logUpdate("Updated JobSource with ID " + updatedSource.getId() + " to name '" + updatedSource.getName() + "'");
            return mapper.toDTO(updatedSource);
        });
    }

    public void deleteSource(UUID id) {
        jobSourceRepository.deleteById(id);
        auditLogService.logDelete("Deleted JobSource with ID " + id);
    }
}

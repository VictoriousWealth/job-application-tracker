package com.nick.job_application_tracker.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.nick.job_application_tracker.dto.CommunicationLogDTO;
import com.nick.job_application_tracker.model.CommunicationLog;
import com.nick.job_application_tracker.model.JobApplication;
import com.nick.job_application_tracker.repository.CommunicationLogRepository;

@Service
public class CommunicationLogService {

    private final CommunicationLogRepository repo;

    public CommunicationLogService(CommunicationLogRepository repo) {
        this.repo = repo;
    }

    public List<CommunicationLogDTO> getByJobAppId(Long jobAppId) {
        return repo.findByJobApplicationId(jobAppId).stream()
            .map(this::toDTO)
            .toList();
    }

    public CommunicationLogDTO save(CommunicationLogDTO dto) {
        CommunicationLog log = new CommunicationLog();
        log.setType(CommunicationLog.Method.valueOf(dto.type));
        log.setDirection(CommunicationLog.Direction.valueOf(dto.direction));
        log.setTimestamp(dto.timestamp != null ? dto.timestamp : LocalDateTime.now());
        log.setMessage(dto.message);

        JobApplication job = new JobApplication();
        job.setId(dto.jobApplicationId);
        log.setJobApplication(job);

        return toDTO(repo.save(log));
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    private CommunicationLogDTO toDTO(CommunicationLog log) {
        return new CommunicationLogDTO(
            log.getId(),
            log.getType().name(),
            log.getDirection().name(),
            log.getTimestamp(),
            log.getMessage(),
            log.getJobApplication().getId()
        );
    }

}

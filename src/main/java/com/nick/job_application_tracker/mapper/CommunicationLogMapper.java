package com.nick.job_application_tracker.mapper;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.nick.job_application_tracker.dto.CommunicationLogDTO;
import com.nick.job_application_tracker.model.CommunicationLog;
import com.nick.job_application_tracker.model.JobApplication;

@Component
public class CommunicationLogMapper {

    public CommunicationLogDTO toDTO(CommunicationLog log) {
        return new CommunicationLogDTO(
            log.getId(),
            log.getType().name(),
            log.getDirection().name(),
            log.getTimestamp(),
            log.getMessage(),
            log.getJobApplication().getId()
        );
    }

    public CommunicationLog toEntity(CommunicationLogDTO dto) {
        CommunicationLog log = new CommunicationLog();
        log.setType(CommunicationLog.Method.valueOf(dto.type));
        log.setDirection(CommunicationLog.Direction.valueOf(dto.direction));
        log.setTimestamp(dto.timestamp != null ? dto.timestamp : LocalDateTime.now());
        log.setMessage(dto.message);

        JobApplication job = new JobApplication();
        job.setId(dto.jobApplicationId);
        log.setJobApplication(job);

        return log;
    }
}

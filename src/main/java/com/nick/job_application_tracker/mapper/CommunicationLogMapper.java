package com.nick.job_application_tracker.mapper;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.nick.job_application_tracker.dto.create.CommunicationLogCreateDTO;
import com.nick.job_application_tracker.dto.detail.CommunicationLogDetailDTO;
import com.nick.job_application_tracker.dto.response.CommunicationLogResponseDTO;
import com.nick.job_application_tracker.dto.update.CommunicationLogUpdateDTO;
import com.nick.job_application_tracker.model.CommunicationLog;
import com.nick.job_application_tracker.model.JobApplication;

@Component
public class CommunicationLogMapper {

    public static final Set<String> patchableFields = Set.of(
        "type",
        "direction",
        "timestamp",
        "message"
    );

    public static CommunicationLog updateEntityWithDTOInfo(CommunicationLog communicationLog, CommunicationLogUpdateDTO dto, JobApplication jobApplication) {
        communicationLog.setType(dto.getType());
        communicationLog.setDirection(dto.getDirection());
        communicationLog.setTimestamp(dto.getTimestamp());
        communicationLog.setMessage(dto.getMessage());
        return communicationLog;
    }

    public CommunicationLog toEntity(CommunicationLogCreateDTO dto, JobApplication jobApplication) {
        CommunicationLog log = new CommunicationLog();
        log.setType(dto.getType());
        log.setDirection(dto.getDirection());
        log.setTimestamp(dto.getTimestamp());
        log.setMessage(dto.getMessage());
        log.setJobApplication(jobApplication);
        return log;
    }

    public static CommunicationLogResponseDTO toResponseDTO(CommunicationLog communicationLog) {
        CommunicationLogResponseDTO dto = new CommunicationLogResponseDTO();
        dto.setId(communicationLog.getId());
        dto.setDirection(communicationLog.getDirection());
        dto.setMessage(communicationLog.getMessage());
        dto.setTimestamp(communicationLog.getTimestamp());
        dto.setType(communicationLog.getType());
        dto.setJobApplicationId(communicationLog.getJobApplication() == null ? null : communicationLog.getJobApplication().getId());
        return dto;
    }

    public CommunicationLogDetailDTO toDetailDTO(CommunicationLog log) {
        CommunicationLogDetailDTO dto = new CommunicationLogDetailDTO();
        dto.setId(log.getId());
        dto.setDirection(log.getDirection());
        dto.setType(log.getType());
        dto.setTimestamp(log.getTimestamp());
        dto.setMessage(log.getMessage());
        dto.setJobApplicationId(log.getJobApplication() == null ? null : log.getJobApplication().getId());
        return dto;
    }

    public CommunicationLog toEntity(com.nick.job_application_tracker.dto.CommunicationLogDTO dto, JobApplication jobApplication) {
        CommunicationLog log = new CommunicationLog();
        if (dto.getId() != null) {
            log.setId(dto.getId());
        }
        log.setType(dto.getType() == null ? null : CommunicationLog.Method.valueOf(dto.getType()));
        log.setDirection(dto.getDirection() == null ? null : CommunicationLog.Direction.valueOf(dto.getDirection()));
        log.setTimestamp(dto.getTimestamp());
        log.setMessage(dto.getMessage());
        log.setJobApplication(jobApplication);
        return log;
    }

    public CommunicationLog toEntity(com.nick.job_application_tracker.dto.CommunicationLogDTO dto) {
        JobApplication jobApplication = new JobApplication();
        jobApplication.setId(dto.getJobApplicationId());
        return toEntity(dto, jobApplication);
    }

    public com.nick.job_application_tracker.dto.CommunicationLogDTO toDTO(CommunicationLog communicationLog) {
        com.nick.job_application_tracker.dto.CommunicationLogDTO dto =
            new com.nick.job_application_tracker.dto.CommunicationLogDTO();
        dto.setId(communicationLog.getId());
        dto.setType(communicationLog.getType() == null ? null : communicationLog.getType().name());
        dto.setDirection(communicationLog.getDirection() == null ? null : communicationLog.getDirection().name());
        dto.setTimestamp(communicationLog.getTimestamp());
        dto.setMessage(communicationLog.getMessage());
        dto.setJobApplicationId(communicationLog.getJobApplication() == null ? null : communicationLog.getJobApplication().getId());
        return dto;
    }

    
}

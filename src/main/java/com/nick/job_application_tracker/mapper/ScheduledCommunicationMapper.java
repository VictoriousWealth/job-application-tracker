package com.nick.job_application_tracker.mapper;

import org.springframework.stereotype.Component;

import com.nick.job_application_tracker.dto.ScheduledCommunicationDTO;
import com.nick.job_application_tracker.dto.create.ScheduledCommunicationCreateDTO;
import com.nick.job_application_tracker.dto.detail.ScheduledCommunicationDetailDTO;
import com.nick.job_application_tracker.dto.response.ScheduledCommunicationResponseDTO;
import com.nick.job_application_tracker.dto.update.ScheduledCommunicationUpdateDTO;
import com.nick.job_application_tracker.model.JobApplication;
import com.nick.job_application_tracker.model.ScheduledCommunication;
import com.nick.job_application_tracker.model.ScheduledCommunication.Type;

@Component
public class ScheduledCommunicationMapper {

    public static ScheduledCommunicationResponseDTO toResponseDTO(ScheduledCommunication entity) {
        ScheduledCommunicationResponseDTO dto = new ScheduledCommunicationResponseDTO();
        dto.setId(entity.getId());
        dto.setType(entity.getType() == null ? null : entity.getType().name());
        dto.setScheduledFor(entity.getScheduledFor());
        dto.setNotes(entity.getNotes());
        dto.setJobApplicationId(entity.getJobApplication() != null ? entity.getJobApplication().getId() : null);
        return dto;
    }

    public static ScheduledCommunicationDetailDTO toDetailDTO(ScheduledCommunication entity) {
        ScheduledCommunicationDetailDTO dto = new ScheduledCommunicationDetailDTO();
        dto.setId(entity.getId());
        dto.setType(entity.getType() == null ? null : entity.getType().name());
        dto.setScheduledFor(entity.getScheduledFor());
        dto.setNotes(entity.getNotes());
        dto.setJobApplicationId(entity.getJobApplication() != null ? entity.getJobApplication().getId() : null);
        return dto;
    }

    public static ScheduledCommunicationDTO toDTO(ScheduledCommunication entity) {
        return new ScheduledCommunicationDTO(
            entity.getId(),
            entity.getType().name(), 
            entity.getScheduledFor(),
            entity.getNotes(),
            entity.getJobApplication() != null ? entity.getJobApplication().getId() : null
        );
    }
    
    public static ScheduledCommunication toEntity(ScheduledCommunicationCreateDTO dto, JobApplication jobApp) {
        ScheduledCommunication entity = new ScheduledCommunication();
        entity.setType(Type.from(dto.getType()));
        entity.setScheduledFor(dto.getScheduledFor());
        entity.setNotes(dto.getNotes());
        entity.setJobApplication(jobApp);
        return entity;
    }

    public static ScheduledCommunication toEntity(
        com.nick.job_application_tracker.dto.ScheduledCommunicationCreateDTO dto,
        JobApplication jobApp
    ) {
        ScheduledCommunication entity = new ScheduledCommunication();
        entity.setType(Type.from(dto.getType()));
        entity.setScheduledFor(dto.getScheduledFor());
        entity.setNotes(dto.getNotes());
        entity.setJobApplication(jobApp);
        return entity;
    }

    public static ScheduledCommunication updateEntityWithDTOInfo(
        ScheduledCommunication entity,
        ScheduledCommunicationUpdateDTO dto,
        JobApplication jobApp
    ) {
        entity.setType(Type.from(dto.getType()));
        entity.setScheduledFor(dto.getScheduledFor());
        entity.setNotes(dto.getNotes());
        entity.setJobApplication(jobApp);
        return entity;
    }
}    

package com.nick.job_application_tracker.mapper;

import com.nick.job_application_tracker.dto.ScheduledCommunicationCreateDTO;
import com.nick.job_application_tracker.dto.ScheduledCommunicationDTO;
import com.nick.job_application_tracker.model.JobApplication;
import com.nick.job_application_tracker.model.ScheduledCommunication;
import com.nick.job_application_tracker.model.ScheduledCommunication.Type;

public class ScheduledCommunicationMapper {

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
        entity.setType(Type.valueOf(dto.getType())); 
        entity.setScheduledFor(dto.getScheduledFor());
        entity.setNotes(dto.getNotes());
        entity.setJobApplication(jobApp);
        return entity;
    }
}    
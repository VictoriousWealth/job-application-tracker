package com.nick.job_application_tracker.mapper;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.nick.job_application_tracker.dto.create.ApplicationTimelineCreateDTO;
import com.nick.job_application_tracker.dto.detail.ApplicationTimelineDetailDTO;
import com.nick.job_application_tracker.dto.response.ApplicationTimelineResponseDTO;
import com.nick.job_application_tracker.dto.update.ApplicationTimelineUpdateDTO;
import com.nick.job_application_tracker.model.ApplicationTimeline;
import com.nick.job_application_tracker.model.JobApplication;

@Component
public class ApplicationTimelineMapper {

    public static final Set<String> patchableFields = Set.of(
        "eventType",
        "eventTime",
        "description"
    );

    public static ApplicationTimeline updateEntityWithDTOInfo(ApplicationTimeline applicationTimeline, ApplicationTimelineUpdateDTO dto) {
        applicationTimeline.setEventTime(dto.getEventTime());
        applicationTimeline.setEventType(dto.getEventType());
        applicationTimeline.setDescription(dto.getDescription());
        return applicationTimeline;
    }

    public static ApplicationTimeline toEntity(ApplicationTimelineCreateDTO dto, JobApplication jobApplication) {
        ApplicationTimeline entity = new ApplicationTimeline();
        entity.setEventTime(dto.getEventTime());
        entity.setEventType(dto.getEventType());
        entity.setDescription(dto.getDescription());
        entity.setJobApplication(jobApplication);
        return entity;
    }

    public static ApplicationTimelineResponseDTO toResponseDTO(ApplicationTimeline applicationTimeline) {
        ApplicationTimelineResponseDTO dto = new ApplicationTimelineResponseDTO();
        dto.id = applicationTimeline.getId();
        dto.eventTime = applicationTimeline.getEventTime();
        dto.eventType = applicationTimeline.getEventType();
        dto.description = applicationTimeline.getDescription();
        return dto;
    }

    public static ApplicationTimelineDetailDTO toDetailDTO(ApplicationTimeline applicationTimeline) {
        ApplicationTimelineDetailDTO dto = new ApplicationTimelineDetailDTO();
        dto.id = applicationTimeline.getId();
        dto.eventTime = applicationTimeline.getEventTime();
        dto.eventType = applicationTimeline.getEventType();
        dto.description = applicationTimeline.getDescription();
        dto.jobApplicationId = applicationTimeline.getJobApplication() == null ? null : applicationTimeline.getJobApplication().getId();
        return dto;

    }
   
}

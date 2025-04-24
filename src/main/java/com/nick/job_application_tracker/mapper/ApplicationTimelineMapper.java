package com.nick.job_application_tracker.mapper;

import org.springframework.stereotype.Component;

import com.nick.job_application_tracker.dto.ApplicationTimelineDTO;
import com.nick.job_application_tracker.model.ApplicationTimeline;
import com.nick.job_application_tracker.model.JobApplication;

@Component
public class ApplicationTimelineMapper {

    public static ApplicationTimelineDTO toDTO(ApplicationTimeline entity) {
        return new ApplicationTimelineDTO(
            entity.getId(),
            entity.getEventType().name(),
            entity.getEventTime(),
            entity.getDescription(),
            entity.getJobApplication().getId()
        );
    }

    public static ApplicationTimeline toEntity(ApplicationTimelineDTO dto) {
        ApplicationTimeline entity = new ApplicationTimeline();
        entity.setEventType(ApplicationTimeline.EventType.valueOf(dto.eventType));
        entity.setEventTime(dto.eventTime);
        entity.setDescription(dto.description);

        JobApplication job = new JobApplication();
        job.setId(dto.jobApplicationId);
        entity.setJobApplication(job);

        return entity;
    }
}

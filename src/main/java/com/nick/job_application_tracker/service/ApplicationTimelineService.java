package com.nick.job_application_tracker.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.nick.job_application_tracker.dto.ApplicationTimelineDTO;
import com.nick.job_application_tracker.model.ApplicationTimeline;
import com.nick.job_application_tracker.model.JobApplication;
import com.nick.job_application_tracker.repository.ApplicationTimelineRepository;

@Service
public class ApplicationTimelineService {

    private final ApplicationTimelineRepository repo;

    public ApplicationTimelineService(ApplicationTimelineRepository repo) {
        this.repo = repo;
    }

    public List<ApplicationTimelineDTO> getByJobAppId(Long jobAppId) {
        return repo.findByJobApplicationId(jobAppId).stream()
            .map(this::toDTO)
            .toList();
    }

    public ApplicationTimeline save(ApplicationTimeline event) {
        return repo.save(event);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

   

    public ApplicationTimelineDTO save(ApplicationTimelineDTO dto) {
        ApplicationTimeline event = new ApplicationTimeline();
        event.setEventType(ApplicationTimeline.EventType.valueOf(dto.eventType));
        event.setEventTime(dto.eventTime);
        event.setDescription(dto.description);
        
        JobApplication job = new JobApplication();
        job.setId(dto.jobApplicationId);
        event.setJobApplication(job);

        return toDTO(repo.save(event));
    }

    private ApplicationTimelineDTO toDTO(ApplicationTimeline entity) {
        return new ApplicationTimelineDTO(
            entity.getId(),
            entity.getEventType().name(),
            entity.getEventTime(),
            entity.getDescription(),
            entity.getJobApplication().getId()
        );
    }

}

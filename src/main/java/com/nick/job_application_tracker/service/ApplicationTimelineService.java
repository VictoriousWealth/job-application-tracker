package com.nick.job_application_tracker.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.nick.job_application_tracker.dto.ApplicationTimelineDTO;
import com.nick.job_application_tracker.mapper.ApplicationTimelineMapper;
import com.nick.job_application_tracker.model.ApplicationTimeline;
import com.nick.job_application_tracker.repository.ApplicationTimelineRepository;

@Service
public class ApplicationTimelineService {

    private final ApplicationTimelineRepository repo;

    public ApplicationTimelineService(ApplicationTimelineRepository repo) {
        this.repo = repo;
    }

    public List<ApplicationTimelineDTO> getByJobAppId(Long jobAppId) {
        return repo.findByJobApplicationId(jobAppId).stream()
            .map(ApplicationTimelineMapper::toDTO)
            .toList();
    }

    public ApplicationTimeline save(ApplicationTimeline event) {
        return repo.save(event);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    public ApplicationTimelineDTO save(ApplicationTimelineDTO dto) {
        ApplicationTimeline entity = ApplicationTimelineMapper.toEntity(dto);
        return ApplicationTimelineMapper.toDTO(repo.save(entity));
    }
}

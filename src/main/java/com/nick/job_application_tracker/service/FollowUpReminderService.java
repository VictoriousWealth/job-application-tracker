package com.nick.job_application_tracker.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.nick.job_application_tracker.dto.FollowUpReminderCreateDTO;
import com.nick.job_application_tracker.dto.FollowUpReminderDTO;
import com.nick.job_application_tracker.mapper.FollowUpReminderMapper;
import com.nick.job_application_tracker.model.FollowUpReminder;
import com.nick.job_application_tracker.model.JobApplication;
import com.nick.job_application_tracker.repository.FollowUpReminderRepository;
import com.nick.job_application_tracker.repository.JobApplicationRepository;

@Service
public class FollowUpReminderService {

    private final FollowUpReminderRepository repository;
    private final JobApplicationRepository jobAppRepo;
    private final FollowUpReminderMapper mapper;

    public FollowUpReminderService(
            FollowUpReminderRepository repository,
            JobApplicationRepository jobAppRepo,
            FollowUpReminderMapper mapper) {
        this.repository = repository;
        this.jobAppRepo = jobAppRepo;
        this.mapper = mapper;
    }

    public List<FollowUpReminderDTO> getByJobAppId(Long jobAppId) {
        return repository.findByJobApplicationId(jobAppId)
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    public FollowUpReminderDTO save(FollowUpReminderCreateDTO dto) {
        JobApplication jobApp = jobAppRepo.findById(dto.getJobApplicationId())
                .orElseThrow(() -> new RuntimeException("Job application not found"));

        FollowUpReminder reminder = mapper.toEntity(dto, jobApp);
        return mapper.toDTO(repository.save(reminder));
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}

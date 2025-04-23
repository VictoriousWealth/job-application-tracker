package com.nick.job_application_tracker.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.nick.job_application_tracker.dto.FollowUpReminderCreateDTO;
import com.nick.job_application_tracker.dto.FollowUpReminderDTO;
import com.nick.job_application_tracker.model.FollowUpReminder;
import com.nick.job_application_tracker.model.JobApplication;
import com.nick.job_application_tracker.repository.FollowUpReminderRepository;
import com.nick.job_application_tracker.repository.JobApplicationRepository;

@Service
public class FollowUpReminderService {

    private final FollowUpReminderRepository repository;
    private final JobApplicationRepository jobAppRepo;

    public FollowUpReminderService(FollowUpReminderRepository repository, JobApplicationRepository jobAppRepo) {
        this.repository = repository;
        this.jobAppRepo = jobAppRepo;
    }

    public List<FollowUpReminderDTO> getByJobAppId(Long jobAppId) {
        return repository.findByJobApplicationId(jobAppId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public FollowUpReminderDTO save(FollowUpReminderCreateDTO dto) {
        JobApplication jobApp = jobAppRepo.findById(dto.getJobApplicationId())
                .orElseThrow(() -> new RuntimeException("Job application not found"));

        FollowUpReminder reminder = new FollowUpReminder();
        reminder.setRemindOn(dto.getRemindOn());
        reminder.setNote(dto.getNote());
        reminder.setJobApplication(jobApp);

        return toDTO(repository.save(reminder));
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    private FollowUpReminderDTO toDTO(FollowUpReminder entity) {
        FollowUpReminderDTO dto = new FollowUpReminderDTO();
        dto.setId(entity.getId());
        dto.setRemindOn(entity.getRemindOn());
        dto.setNote(entity.getNote());
        dto.setJobApplicationId(entity.getJobApplication().getId());
        return dto;
    }
}

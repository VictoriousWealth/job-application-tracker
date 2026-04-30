package com.nick.job_application_tracker.mapper;

import org.springframework.stereotype.Component;

import com.nick.job_application_tracker.dto.FollowUpReminderDTO;
import com.nick.job_application_tracker.dto.create.FollowUpReminderCreateDTO;
import com.nick.job_application_tracker.dto.detail.FollowUpReminderDetailDTO;
import com.nick.job_application_tracker.dto.response.FollowUpReminderResponseDTO;
import com.nick.job_application_tracker.dto.update.FollowUpReminderUpdateDTO;
import com.nick.job_application_tracker.model.FollowUpReminder;
import com.nick.job_application_tracker.model.JobApplication;

@Component
public class FollowUpReminderMapper {

    public FollowUpReminderResponseDTO toResponseDTO(FollowUpReminder entity) {
        FollowUpReminderResponseDTO dto = new FollowUpReminderResponseDTO();
        dto.setId(entity.getId());
        dto.setRemindOn(entity.getRemindOn());
        dto.setNote(entity.getNote());
        dto.setJobApplicationId(entity.getJobApplication() == null ? null : entity.getJobApplication().getId());
        return dto;
    }

    public FollowUpReminderDetailDTO toDetailDTO(FollowUpReminder entity) {
        FollowUpReminderDetailDTO dto = new FollowUpReminderDetailDTO();
        dto.setId(entity.getId());
        dto.setRemindOn(entity.getRemindOn());
        dto.setNote(entity.getNote());
        dto.setJobApplicationId(entity.getJobApplication() == null ? null : entity.getJobApplication().getId());
        return dto;
    }

    public FollowUpReminderDTO toDTO(FollowUpReminder entity) {
        FollowUpReminderDTO dto = new FollowUpReminderDTO();
        dto.setId(entity.getId());
        dto.setRemindOn(entity.getRemindOn());
        dto.setNote(entity.getNote());
        dto.setJobApplicationId(entity.getJobApplication().getId());
        return dto;
    }

    public FollowUpReminder toEntity(FollowUpReminderCreateDTO dto, JobApplication jobApp) {
        FollowUpReminder reminder = new FollowUpReminder();
        reminder.setRemindOn(dto.getRemindOn());
        reminder.setNote(dto.getNote());
        reminder.setJobApplication(jobApp);
        return reminder;
    }

    public FollowUpReminder updateEntityWithDTOInfo(FollowUpReminder reminder, FollowUpReminderUpdateDTO dto, JobApplication jobApp) {
        reminder.setRemindOn(dto.getRemindOn());
        reminder.setNote(dto.getNote());
        reminder.setJobApplication(jobApp);
        return reminder;
    }
}

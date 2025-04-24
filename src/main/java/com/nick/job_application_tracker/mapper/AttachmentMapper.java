package com.nick.job_application_tracker.mapper;

import org.springframework.stereotype.Component;

import com.nick.job_application_tracker.dto.AttachmentDTO;
import com.nick.job_application_tracker.model.Attachment;
import com.nick.job_application_tracker.model.JobApplication;

@Component
public class AttachmentMapper {

    public AttachmentDTO toDTO(Attachment attachment) {
        return new AttachmentDTO(
            attachment.getId(),
            attachment.getType().name(),
            attachment.getFilePath(),
            attachment.getJobApplication().getId()
        );
    }

    public Attachment toEntity(AttachmentDTO dto) {
        Attachment attachment = new Attachment();
        attachment.setType(Attachment.Type.valueOf(dto.type));
        attachment.setFilePath(dto.filePath);

        JobApplication job = new JobApplication();
        job.setId(dto.jobApplicationId);
        attachment.setJobApplication(job);

        return attachment;
    }
}

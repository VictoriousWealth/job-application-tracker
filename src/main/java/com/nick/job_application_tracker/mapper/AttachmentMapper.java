package com.nick.job_application_tracker.mapper;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.nick.job_application_tracker.dto.create.AttachmentCreateDTO;
import com.nick.job_application_tracker.dto.detail.AttachmentDetailDTO;
import com.nick.job_application_tracker.dto.response.AttachmentResponseDTO;
import com.nick.job_application_tracker.dto.update.AttachmentUpdateDTO;
import com.nick.job_application_tracker.model.Attachment;
import com.nick.job_application_tracker.model.JobApplication;

@Component
public class AttachmentMapper {
    
    public static final Set<String> patchableFields = Set.of(
        "type",
        "filePath"
    );


    public static Attachment updateEntityWithDTOInfo(Attachment attachment, AttachmentUpdateDTO dto, JobApplication jobApplication) {
        attachment.setType(dto.getType());
        attachment.setFilePath(dto.getFilePath());
        attachment.setJobApplication(jobApplication);
        return attachment;
    }

    public static Attachment toEntity(
        AttachmentCreateDTO dto, 
        JobApplication jobApplication
        )
        {
            Attachment entity = new Attachment();
            entity.setType(dto.getType());
            entity.setFilePath(dto.getFilePath());
            entity.setJobApplication(jobApplication);
            return entity;
        }

    public static AttachmentResponseDTO toResponseDTO(Attachment attachment) {
        AttachmentResponseDTO dto = new AttachmentResponseDTO();
        dto.setId(attachment.getId());
        dto.setType(attachment.getType());
        dto.setFilePath(attachment.getFilePath());
        dto.setJobApplicationId(attachment.getJobApplication() == null ? null : attachment.getJobApplication().getId());
        return dto;
    }

    public static AttachmentDetailDTO toDetailDTO(Attachment attachment) {
        AttachmentDetailDTO dto = new AttachmentDetailDTO();
        dto.setId(attachment.getId());
        dto.setType(attachment.getType());
        dto.setFilePath(attachment.getFilePath());
        dto.setJobApplicationId(attachment.getJobApplication() == null ? null : attachment.getJobApplication().getId());
        return dto;
    }

    public Attachment toEntity(com.nick.job_application_tracker.dto.AttachmentDTO dto, JobApplication jobApplication) {
        Attachment attachment = new Attachment();
        if (dto.getId() != null) {
            attachment.setId(dto.getId());
        }
        attachment.setType(dto.getType() == null ? null : Attachment.Type.from(dto.getType()));
        attachment.setFilePath(dto.getFilePath());
        attachment.setJobApplication(jobApplication);
        return attachment;
    }

    public Attachment toEntity(com.nick.job_application_tracker.dto.AttachmentDTO dto) {
        JobApplication jobApplication = new JobApplication();
        jobApplication.setId(dto.getJobApplicationId());
        return toEntity(dto, jobApplication);
    }

    public com.nick.job_application_tracker.dto.AttachmentDTO toDTO(Attachment attachment) {
        com.nick.job_application_tracker.dto.AttachmentDTO dto = new com.nick.job_application_tracker.dto.AttachmentDTO();
        dto.setId(attachment.getId());
        dto.setType(attachment.getType() == null ? null : attachment.getType().name());
        dto.setFilePath(attachment.getFilePath());
        dto.setJobApplicationId(attachment.getJobApplication() == null ? null : attachment.getJobApplication().getId());
        return dto;
    }

   
}

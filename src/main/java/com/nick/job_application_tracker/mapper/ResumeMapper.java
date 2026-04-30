package com.nick.job_application_tracker.mapper;

import org.springframework.stereotype.Component;

import com.nick.job_application_tracker.dto.ResumeDTO;
import com.nick.job_application_tracker.dto.create.ResumeCreateDTO;
import com.nick.job_application_tracker.dto.detail.ResumeDetailDTO;
import com.nick.job_application_tracker.dto.response.ResumeResponseDTO;
import com.nick.job_application_tracker.dto.update.ResumeUpdateDTO;
import com.nick.job_application_tracker.model.Resume;

@Component
public class ResumeMapper {
    public static ResumeResponseDTO toResponseDTO(Resume resume) {
        ResumeResponseDTO dto = new ResumeResponseDTO();
        dto.setId(resume.getId());
        dto.setTitle(resume.getTitle());
        dto.setFilePath(resume.getFilePath());
        return dto;
    }

    public static ResumeDetailDTO toDetailDTO(Resume resume) {
        ResumeDetailDTO dto = new ResumeDetailDTO();
        dto.setId(resume.getId());
        dto.setTitle(resume.getTitle());
        dto.setFilePath(resume.getFilePath());
        return dto;
    }

    public static ResumeDTO toDTO(Resume resume) {
        return new ResumeDTO(resume.getId(), resume.getTitle(), resume.getFilePath());
    }

    public static Resume toEntity(ResumeCreateDTO dto) {
        Resume resume = new Resume();
        resume.setTitle(dto.getTitle());
        resume.setFilePath(dto.getFilePath());
        return resume;
    }

    public static Resume toEntity(ResumeDTO dto) {
        Resume resume = new Resume();
        resume.setTitle(dto.getTitle() != null ? dto.getTitle() : dto.getFilePath());
        resume.setFilePath(dto.getFilePath());
        return resume;
    }

    public static Resume updateEntityWithDTOInfo(Resume resume, ResumeUpdateDTO dto) {
        resume.setTitle(dto.getTitle());
        resume.setFilePath(dto.getFilePath());
        return resume;
    }
}

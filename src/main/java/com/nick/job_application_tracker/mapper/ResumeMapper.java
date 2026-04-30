package com.nick.job_application_tracker.mapper;

import org.springframework.stereotype.Component;

import com.nick.job_application_tracker.dto.ResumeDTO;
import com.nick.job_application_tracker.model.Resume;

@Component
public class ResumeMapper {
    public static ResumeDTO toDTO(Resume resume) {
        return new ResumeDTO(resume.getId(), resume.getTitle(), resume.getFilePath());
    }

    public static Resume toEntity(ResumeDTO dto) {
        Resume resume = new Resume();
        resume.setTitle(dto.getTitle() != null ? dto.getTitle() : dto.getFilePath());
        resume.setFilePath(dto.getFilePath());
        return resume;
    }
}

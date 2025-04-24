package com.nick.job_application_tracker.mapper;

import org.springframework.stereotype.Component;

import com.nick.job_application_tracker.dto.CoverLetterDTO;
import com.nick.job_application_tracker.model.CoverLetter;

@Component
public class CoverLetterMapper {

    public CoverLetterDTO toDTO(CoverLetter entity) {
        return new CoverLetterDTO(
            entity.getId(),
            entity.getTitle(),
            entity.getFilePath(),
            entity.getContent()
        );
    }

    public CoverLetter toEntity(CoverLetterDTO dto) {
        CoverLetter coverLetter = new CoverLetter();
        coverLetter.setTitle(dto.getTitle());
        coverLetter.setFilePath(dto.getFilePath());
        coverLetter.setContent(dto.getContent());
        return coverLetter;
    }
}

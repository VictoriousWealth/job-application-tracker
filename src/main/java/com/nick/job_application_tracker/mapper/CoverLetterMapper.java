package com.nick.job_application_tracker.mapper;

import org.springframework.stereotype.Component;

import com.nick.job_application_tracker.dto.CoverLetterDTO;
import com.nick.job_application_tracker.dto.create.CoverLetterCreateDTO;
import com.nick.job_application_tracker.dto.detail.CoverLetterDetailDTO;
import com.nick.job_application_tracker.dto.response.CoverLetterResponseDTO;
import com.nick.job_application_tracker.dto.update.CoverLetterUpdateDTO;
import com.nick.job_application_tracker.model.CoverLetter;

@Component
public class CoverLetterMapper {

    public CoverLetterResponseDTO toResponseDTO(CoverLetter entity) {
        CoverLetterResponseDTO dto = new CoverLetterResponseDTO();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setFilePath(entity.getFilePath());
        dto.setContent(entity.getContent());
        return dto;
    }

    public CoverLetterDetailDTO toDetailDTO(CoverLetter entity) {
        CoverLetterDetailDTO dto = new CoverLetterDetailDTO();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setFilePath(entity.getFilePath());
        dto.setContent(entity.getContent());
        return dto;
    }

    public CoverLetter toEntity(CoverLetterCreateDTO dto) {
        CoverLetter coverLetter = new CoverLetter();
        coverLetter.setTitle(dto.getTitle());
        coverLetter.setFilePath(dto.getFilePath());
        coverLetter.setContent(dto.getContent());
        return coverLetter;
    }

    public CoverLetter updateEntityWithDTOInfo(CoverLetter coverLetter, CoverLetterUpdateDTO dto) {
        coverLetter.setTitle(dto.getTitle());
        coverLetter.setFilePath(dto.getFilePath());
        coverLetter.setContent(dto.getContent());
        return coverLetter;
    }

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

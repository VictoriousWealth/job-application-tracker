package com.nick.job_application_tracker.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.nick.job_application_tracker.dto.ResumeDTO;
import com.nick.job_application_tracker.model.Resume;

public class ResumeMapperTest {

    @Test
    @DisplayName("Should convert Resume entity to DTO")
    void testToDTO() {
        Resume resume = new Resume();
        resume.setId(5L);
        resume.setFilePath("/resumes/dev_resume.pdf");

        ResumeDTO dto = ResumeMapper.toDTO(resume);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(5L);
        assertThat(dto.getFilePath()).isEqualTo("/resumes/dev_resume.pdf");
    }

    @Test
    @DisplayName("Should convert ResumeDTO to entity")
    void testToEntity() {
        ResumeDTO dto = new ResumeDTO();
        dto.setFilePath("/files/backend_resume.pdf");

        Resume resume = ResumeMapper.toEntity(dto);

        assertThat(resume).isNotNull();
        assertThat(resume.getFilePath()).isEqualTo("/files/backend_resume.pdf");
    }

    @Test
    @DisplayName("Should handle null filePath in DTO")
    void testToEntityWithNullFilePath() {
        ResumeDTO dto = new ResumeDTO();
        dto.setFilePath(null);

        Resume resume = ResumeMapper.toEntity(dto);

        assertThat(resume).isNotNull();
        assertThat(resume.getFilePath()).isNull();
    }
}

package com.nick.job_application_tracker.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.nick.job_application_tracker.dto.CoverLetterDTO;
import com.nick.job_application_tracker.model.CoverLetter;

public class CoverLetterMapperTest {

    private final CoverLetterMapper mapper = new CoverLetterMapper();

    @Test
    void testToDTO() {
        CoverLetter cl = new CoverLetter();
        cl.setId(10L);
        cl.setTitle("My Cover Letter");
        cl.setFilePath("/path/cover.pdf");
        cl.setContent("Dear Hiring Manager...");

        CoverLetterDTO dto = mapper.toDTO(cl);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(10L);
        assertThat(dto.getTitle()).isEqualTo("My Cover Letter");
        assertThat(dto.getFilePath()).isEqualTo("/path/cover.pdf");
        assertThat(dto.getContent()).isEqualTo("Dear Hiring Manager...");
    }

    @Test
    void testToEntity() {
        CoverLetterDTO dto = new CoverLetterDTO(20L, "Internship Letter", "/docs/intern.pdf", "Excited to apply...");

        CoverLetter cl = mapper.toEntity(dto);

        assertThat(cl).isNotNull();
        assertThat(cl.getTitle()).isEqualTo("Internship Letter");
        assertThat(cl.getFilePath()).isEqualTo("/docs/intern.pdf");
        assertThat(cl.getContent()).isEqualTo("Excited to apply...");
    }
}

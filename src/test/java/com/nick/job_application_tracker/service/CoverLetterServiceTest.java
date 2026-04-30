package com.nick.job_application_tracker.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.nick.job_application_tracker.dto.create.CoverLetterCreateDTO;
import com.nick.job_application_tracker.dto.response.CoverLetterResponseDTO;
import com.nick.job_application_tracker.model.CoverLetter;
import com.nick.job_application_tracker.repository.inter_face.CoverLetterRepository;
import com.nick.job_application_tracker.service.implementation.CoverLetterService;
import com.nick.job_application_tracker.service.inter_face.AuditLogService;

import jakarta.transaction.Transactional;

@Transactional
@SpringBootTest
public class CoverLetterServiceTest {

    @Autowired
    private CoverLetterService coverLetterService;

    @Autowired
    private CoverLetterRepository coverLetterRepository;

    @MockBean
    private AuditLogService auditLogService;

    @BeforeEach
    void clearRepo() {
        coverLetterRepository.deleteAll();
    }

    @Test
    @DisplayName("Should create a cover letter response")
    void testCreateCoverLetter() {
        CoverLetterCreateDTO dto = new CoverLetterCreateDTO();
        dto.setTitle("Graduate Cover");
        dto.setFilePath("/files/grad.pdf");
        dto.setContent("Dear Hiring Manager...");

        CoverLetterResponseDTO saved = coverLetterService.create(dto);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getTitle()).isEqualTo("Graduate Cover");
        assertThat(saved.getContent()).isEqualTo("Dear Hiring Manager...");
    }

    @Test
    @DisplayName("Should return all cover letters")
    void testFindAll() {
        CoverLetter coverLetter = new CoverLetter();
        coverLetter.setTitle("Developer Role");
        coverLetter.setFilePath("/files/dev.pdf");
        coverLetter.setContent("To whom it may concern...");

        coverLetterRepository.save(coverLetter);

        List<CoverLetterResponseDTO> results = coverLetterService.findAll();

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getTitle()).isEqualTo("Developer Role");
    }
}

package com.nick.job_application_tracker.service;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import com.nick.job_application_tracker.dto.CoverLetterDTO;
import com.nick.job_application_tracker.model.CoverLetter;
import com.nick.job_application_tracker.repository.CoverLetterRepository;

import jakarta.transaction.Transactional;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
public class CoverLetterServiceTest {

    @Autowired
    private CoverLetterService coverLetterService;

    @Autowired
    private CoverLetterRepository coverLetterRepository;

    @MockBean
    private AuditLogService auditLogService; // ✅ ADD THIS

    @BeforeEach
    void clearRepo() {
        coverLetterRepository.deleteAll();
    }

    @Test
    @DisplayName("Should save a cover letter and return DTO with same data")
    void testSaveCoverLetter() {
        CoverLetterDTO dto = new CoverLetterDTO(null, "Graduate Cover", "/files/grad.pdf", "Dear Hiring Manager...");
        
        CoverLetterDTO saved = coverLetterService.save(dto);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getTitle()).isEqualTo("Graduate Cover");
        assertThat(saved.getFilePath()).isEqualTo("/files/grad.pdf");
        assertThat(saved.getContent()).isEqualTo("Dear Hiring Manager...");
    }

    @Test
    @DisplayName("Should return all cover letters as DTOs")
    void testFindAll() {
        CoverLetter cl1 = new CoverLetter();
        cl1.setTitle("Developer Role");
        cl1.setFilePath("/files/dev.pdf");
        cl1.setContent("To whom it may concern...");
        
        CoverLetter cl2 = new CoverLetter();
        cl2.setTitle("Analyst Role");
        cl2.setFilePath("/files/analyst.pdf");
        cl2.setContent("Dear Analyst Recruiter...");

        coverLetterRepository.saveAll(List.of(cl1, cl2));

        List<CoverLetterDTO> results = coverLetterService.findAll();

        assertThat(results).hasSize(2);
        assertThat(results).anyMatch(c -> c.getTitle().equals("Developer Role"));
        assertThat(results).anyMatch(c -> c.getFilePath().equals("/files/analyst.pdf"));
    }

    @Test
    @DisplayName("Should delete a cover letter by ID")
    void testDeleteCoverLetter() {
        CoverLetter cl = new CoverLetter();
        cl.setTitle("Temp Cover");
        cl.setFilePath("/files/temp.pdf");
        cl.setContent("Temporary content");

        cl = coverLetterRepository.save(cl);
        Long id = cl.getId();

        coverLetterService.delete(id);

        assertThat(coverLetterRepository.findById(id)).isNotPresent();
    }
}

package com.nick.job_application_tracker.repository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.nick.job_application_tracker.model.CoverLetter;

import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class CoverLetterRepositoryTest {

    @Autowired
    private CoverLetterRepository coverLetterRepository;

    @Test
    @DisplayName("Should save and retrieve a CoverLetter by ID")
    public void testSaveAndFindById() {
        // Given
        CoverLetter coverLetter = new CoverLetter();
        coverLetter.setTitle("Backend Engineer Application");
        coverLetter.setFilePath("/files/cv/backend_letter.pdf");
        coverLetter.setContent("Dear Hiring Manager, I am writing to express...");

        // When
        CoverLetter saved = coverLetterRepository.save(coverLetter);
        Optional<CoverLetter> found = coverLetterRepository.findById(saved.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Backend Engineer Application");
        assertThat(found.get().getFilePath()).contains("backend_letter");
        assertThat(found.get().getContent()).contains("Dear Hiring Manager");
    }

    @Test
    @DisplayName("Should return empty Optional for non-existent ID")
    public void testFindByNonExistentId() {
        Optional<CoverLetter> found = coverLetterRepository.findById(-1L);
        assertThat(found).isNotPresent();
    }

    @Test
    @DisplayName("Should delete a CoverLetter")
    public void testDelete() {
        // Given
        CoverLetter coverLetter = new CoverLetter();
        coverLetter.setTitle("To Delete");
        coverLetter.setContent("This will be deleted");
        coverLetter = coverLetterRepository.save(coverLetter);

        // When
        Long id = coverLetter.getId();
        coverLetterRepository.deleteById(id);
        Optional<CoverLetter> found = coverLetterRepository.findById(id);

        // Then
        assertThat(found).isNotPresent();
    }
}

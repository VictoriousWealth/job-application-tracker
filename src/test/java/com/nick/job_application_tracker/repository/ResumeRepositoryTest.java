package com.nick.job_application_tracker.repository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.nick.job_application_tracker.model.Resume;

import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class ResumeRepositoryTest {

    @Autowired
    private ResumeRepository resumeRepository;

    @Test
    @DisplayName("Should save and retrieve Resume by ID")
    public void testSaveAndFindById() {
        Resume resume = new Resume();
        resume.setFilePath("/resumes/dev_resume.pdf");

        Resume saved = resumeRepository.save(resume);

        Optional<Resume> found = resumeRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getFilePath()).isEqualTo("/resumes/dev_resume.pdf");
    }

    @Test
    @DisplayName("Should find all resumes")
    public void testFindAll() {
        Resume one = new Resume();
        one.setFilePath("/resumes/resume1.pdf");

        Resume two = new Resume();
        two.setFilePath("/resumes/resume2.pdf");

        resumeRepository.saveAll(List.of(one, two));

        List<Resume> all = resumeRepository.findAll();

        assertThat(all).isNotEmpty();
        assertThat(all).extracting(Resume::getFilePath)
                       .contains("/resumes/resume1.pdf", "/resumes/resume2.pdf");
    }

    @Test
    @DisplayName("Should delete resume by ID")
    public void testDeleteById() {
        Resume resume = new Resume();
        resume.setFilePath("/resumes/temp_resume.pdf");

        Resume saved = resumeRepository.save(resume);
        resumeRepository.deleteById(saved.getId());

        Optional<Resume> found = resumeRepository.findById(saved.getId());
        assertThat(found).isNotPresent();
    }

    @Test
    @DisplayName("Should return empty when resume not found")
    public void testFindByIdNotFound() {
        Optional<Resume> found = resumeRepository.findById(-1L);
        assertThat(found).isEmpty();
    }
}

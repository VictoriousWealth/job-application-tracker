package com.nick.job_application_tracker.repository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.nick.job_application_tracker.model.JobSource;

import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class JobSourceRepositoryTest {

    @Autowired
    private JobSourceRepository jobSourceRepository;

    @Test
    @DisplayName("Should save and retrieve a JobSource")
    public void testSaveAndFindById() {
        JobSource source = new JobSource();
        source.setName("LinkedIn");
        source = jobSourceRepository.save(source);

        Optional<JobSource> found = jobSourceRepository.findById(source.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("LinkedIn");
    }

    @Test
    @DisplayName("Should retrieve all JobSources")
    public void testFindAll() {
        JobSource source1 = new JobSource();
        source1.setName("Indeed");
        JobSource source2 = new JobSource();
        source2.setName("Glassdoor");

        jobSourceRepository.save(source1);
        jobSourceRepository.save(source2);

        List<JobSource> sources = jobSourceRepository.findAll();
        assertThat(sources).extracting(JobSource::getName)
                           .contains("Indeed", "Glassdoor");
    }

    @Test
    @DisplayName("Should return empty for non-existent ID")
    public void testFindByInvalidId() {
        Optional<JobSource> found = jobSourceRepository.findById(-1L);
        assertThat(found).isNotPresent();
    }

    @Test
    @DisplayName("Should delete a JobSource")
    public void testDeleteJobSource() {
        JobSource source = new JobSource();
        source.setName("Monster");
        source = jobSourceRepository.save(source);
        Long id = source.getId();

        jobSourceRepository.deleteById(id);
        Optional<JobSource> found = jobSourceRepository.findById(id);

        assertThat(found).isNotPresent();
    }
}

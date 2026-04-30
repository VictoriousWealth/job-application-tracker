package com.nick.job_application_tracker.mapper;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

import com.nick.job_application_tracker.dto.create.JobApplicationCreateDTO;
import com.nick.job_application_tracker.dto.detail.JobApplicationDetailDTO;
import com.nick.job_application_tracker.dto.response.JobApplicationResponseDTO;
import com.nick.job_application_tracker.model.CoverLetter;
import com.nick.job_application_tracker.model.JobApplication;
import com.nick.job_application_tracker.model.JobSource;
import com.nick.job_application_tracker.model.Location;
import com.nick.job_application_tracker.model.Resume;

public class JobApplicationMapperTest {

    @Test
    void testToEntity() {
        JobApplicationCreateDTO dto = new JobApplicationCreateDTO();
        dto.setCompany("Tesla");
        dto.setJobTitle("Software Engineer");
        dto.setNotes("Applied through referral");
        dto.setLocationCity("London");
        dto.setLocationCountry("UK");
        dto.setSourceId(com.nick.job_application_tracker.TestIds.uuid(1));
        dto.setResumeId(com.nick.job_application_tracker.TestIds.uuid(2));
        dto.setCoverLetterId(com.nick.job_application_tracker.TestIds.uuid(3));

        JobApplication entity = JobApplicationMapper.toEntity(dto);

        assertThat(entity.getCompany()).isEqualTo("Tesla");
        assertThat(entity.getJobTitle()).isEqualTo("Software Engineer");
        assertThat(entity.getNotes()).isEqualTo("Applied through referral");
    }

    @Test
    void testToResponseDTO() {
        JobApplication jobApp = new JobApplication();
        jobApp.setId(com.nick.job_application_tracker.TestIds.uuid(1));
        jobApp.setCompany("Amazon");
        jobApp.setJobTitle("Backend Developer");
        jobApp.setStatus(JobApplication.Status.INTERVIEW);

        JobApplicationResponseDTO dto = JobApplicationMapper.toResponseDTO(jobApp);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getCompany()).isEqualTo("Amazon");
        assertThat(dto.getJobTitle()).isEqualTo("Backend Developer");
        assertThat(dto.getStatus().getName()).isEqualTo("INTERVIEW");
    }

    @Test
    void testToDetailDTO() {
        JobApplication jobApp = new JobApplication();
        jobApp.setId(com.nick.job_application_tracker.TestIds.uuid(2));
        jobApp.setCompany("Google");
        jobApp.setJobTitle("Cloud Engineer");
        jobApp.setStatus(JobApplication.Status.REJECTED);
        jobApp.setAppliedOn(LocalDateTime.of(2025, 3, 15, 10, 0));
        jobApp.setNotes("Didn't go well");

        Location location = new Location();
        location.setCity("Zurich");
        location.setCountry("Switzerland");
        jobApp.setLocation(location);

        JobSource source = new JobSource();
        source.setId(com.nick.job_application_tracker.TestIds.uuid(7));
        source.setName("Referral");
        jobApp.setSource(source);

        Resume resume = new Resume();
        resume.setId(com.nick.job_application_tracker.TestIds.uuid(8));
        jobApp.setResume(resume);

        CoverLetter coverLetter = new CoverLetter();
        coverLetter.setId(com.nick.job_application_tracker.TestIds.uuid(9));
        jobApp.setCoverLetter(coverLetter);

        JobApplicationDetailDTO dto = JobApplicationMapper.toDetailDTO(jobApp);

        assertThat(dto.getId()).isEqualTo(2L);
        assertThat(dto.getCompany()).isEqualTo("Google");
        assertThat(dto.getStatus().getName()).isEqualTo("REJECTED");
        assertThat(dto.getLocationCity()).isEqualTo("Zurich");
        assertThat(dto.getLocationCountry()).isEqualTo("Switzerland");
        assertThat(dto.getSourceId()).isEqualTo(7L);
        assertThat(dto.getResumeId()).isEqualTo(8L);
        assertThat(dto.getCoverLetterId()).isEqualTo(9L);
        assertThat(dto.getNotes()).isEqualTo("Didn't go well");
    }
}

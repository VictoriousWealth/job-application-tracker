package com.nick.job_application_tracker.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.nick.job_application_tracker.dto.AttachmentDTO;
import com.nick.job_application_tracker.model.Attachment;
import com.nick.job_application_tracker.model.JobApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class AttachmentMapperTest {

    private AttachmentMapper mapper;

    @BeforeEach
    void setup() {
        mapper = new AttachmentMapper();
    }

    @Test
    @DisplayName("Should map Attachment to AttachmentDTO")
    void testToDTO() {
        JobApplication job = new JobApplication();
        job.setId(42L);

        Attachment attachment = new Attachment();
        attachment.setId(1L);
        attachment.setType(Attachment.Type.JOB_DESCRIPTION);
        attachment.setFilePath("/files/resume.pdf");
        attachment.setJobApplication(job);

        AttachmentDTO dto = mapper.toDTO(attachment);

        assertThat(dto).isNotNull();
        assertThat(dto.id).isEqualTo(1L);
        assertThat(dto.type).isEqualTo("JOB_DESCRIPTION");
        assertThat(dto.filePath).isEqualTo("/files/resume.pdf");
        assertThat(dto.jobApplicationId).isEqualTo(42L);
    }

    @Test
    @DisplayName("Should map AttachmentDTO to Attachment")
    void testToEntity() {
        AttachmentDTO dto = new AttachmentDTO(1L, "OFFER_LETTER", "/files/offer.pdf", 99L);

        Attachment entity = mapper.toEntity(dto);

        assertThat(entity).isNotNull();
        assertThat(entity.getType()).isEqualTo(Attachment.Type.OFFER_LETTER);
        assertThat(entity.getFilePath()).isEqualTo("/files/offer.pdf");
        assertThat(entity.getJobApplication().getId()).isEqualTo(99L);
    }
}

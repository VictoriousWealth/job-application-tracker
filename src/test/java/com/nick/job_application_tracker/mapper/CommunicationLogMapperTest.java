package com.nick.job_application_tracker.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.nick.job_application_tracker.dto.CommunicationLogDTO;
import com.nick.job_application_tracker.model.CommunicationLog;
import com.nick.job_application_tracker.model.CommunicationLog.Direction;
import com.nick.job_application_tracker.model.CommunicationLog.Method;
import com.nick.job_application_tracker.model.JobApplication;

public class CommunicationLogMapperTest {

    private final CommunicationLogMapper mapper = new CommunicationLogMapper();

    @Test
    void testToDTO() {
        JobApplication job = new JobApplication();
        job.setId(100L);

        CommunicationLog log = new CommunicationLog();
        log.setId(1L);
        log.setType(Method.EMAIL);
        log.setDirection(Direction.OUTBOUND);
        log.setTimestamp(LocalDateTime.now());
        log.setMessage("Sent details");
        log.setJobApplication(job);

        CommunicationLogDTO dto = mapper.toDTO(log);

        assertThat(dto).isNotNull();
        assertThat(dto.id).isEqualTo(log.getId());
        assertThat(dto.type).isEqualTo("EMAIL");
        assertThat(dto.direction).isEqualTo("OUTBOUND");
        assertThat(dto.message).isEqualTo("Sent details");
        assertThat(dto.jobApplicationId).isEqualTo(100L);
    }

    @Test
    void testToEntity() {
        CommunicationLogDTO dto = new CommunicationLogDTO();
        dto.id = 1L;
        dto.type = "CALL";
        dto.direction = "INBOUND";
        dto.timestamp = LocalDateTime.of(2023, 5, 1, 10, 0);
        dto.message = "Call received";
        dto.jobApplicationId = 99L;

        CommunicationLog entity = mapper.toEntity(dto);

        assertThat(entity).isNotNull();
        assertThat(entity.getType()).isEqualTo(Method.CALL);
        assertThat(entity.getDirection()).isEqualTo(Direction.INBOUND);
        assertThat(entity.getMessage()).isEqualTo("Call received");
        assertThat(entity.getJobApplication().getId()).isEqualTo(99L);
    }
}

package com.nick.job_application_tracker.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.nick.job_application_tracker.dto.JobSourceCreateDTO;
import com.nick.job_application_tracker.dto.JobSourceDTO;
import com.nick.job_application_tracker.model.JobSource;

public class JobSourceMapperTest {

    private JobSourceMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new JobSourceMapper();
    }

    @Test
    @DisplayName("Should map JobSource entity to DTO")
    void testToDTO() {
        JobSource source = new JobSource();
        source.setId(1L);
        source.setName("LinkedIn");

        JobSourceDTO dto = mapper.toDTO(source);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("LinkedIn");
    }

    @Test
    @DisplayName("Should map JobSourceCreateDTO to entity")
    void testToEntity() {
        JobSourceCreateDTO dto = new JobSourceCreateDTO();
        dto.setName("Indeed");

        JobSource entity = mapper.toEntity(dto);

        assertThat(entity).isNotNull();
        assertThat(entity.getName()).isEqualTo("Indeed");
        assertThat(entity.getId()).isNull(); // ID should not be set by the mapper
    }
}

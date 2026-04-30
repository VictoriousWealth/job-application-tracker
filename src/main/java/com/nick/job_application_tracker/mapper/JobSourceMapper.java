package com.nick.job_application_tracker.mapper;

import org.springframework.stereotype.Component;

import com.nick.job_application_tracker.dto.JobSourceDTO;
import com.nick.job_application_tracker.dto.create.JobSourceCreateDTO;
import com.nick.job_application_tracker.dto.detail.JobSourceDetailDTO;
import com.nick.job_application_tracker.dto.response.JobSourceResponseDTO;
import com.nick.job_application_tracker.dto.update.JobSourceUpdateDTO;
import com.nick.job_application_tracker.model.JobSource;

@Component
public class JobSourceMapper {

    public JobSourceResponseDTO toResponseDTO(JobSource source) {
        JobSourceResponseDTO dto = new JobSourceResponseDTO();
        dto.setId(source.getId());
        dto.setName(source.getName());
        return dto;
    }

    public JobSourceDetailDTO toDetailDTO(JobSource source) {
        JobSourceDetailDTO dto = new JobSourceDetailDTO();
        dto.setId(source.getId());
        dto.setName(source.getName());
        return dto;
    }

    public JobSourceDTO toDTO(JobSource source) {
        return new JobSourceDTO(source.getId(), source.getName());
    }

    public JobSource toEntity(JobSourceCreateDTO dto) {
        JobSource source = new JobSource();
        source.setName(dto.getName());
        return source;
    }

    public JobSource updateEntityWithDTOInfo(JobSource source, JobSourceUpdateDTO dto) {
        source.setName(dto.getName());
        return source;
    }
}

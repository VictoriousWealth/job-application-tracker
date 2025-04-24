package com.nick.job_application_tracker.mapper;

import org.springframework.stereotype.Component;

import com.nick.job_application_tracker.dto.JobSourceCreateDTO;
import com.nick.job_application_tracker.dto.JobSourceDTO;
import com.nick.job_application_tracker.model.JobSource;

@Component
public class JobSourceMapper {

    public JobSourceDTO toDTO(JobSource source) {
        return new JobSourceDTO(source.getId(), source.getName());
    }

    public JobSource toEntity(JobSourceCreateDTO dto) {
        JobSource source = new JobSource();
        source.setName(dto.getName());
        return source;
    }
}

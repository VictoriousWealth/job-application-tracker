package com.nick.job_application_tracker.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.nick.job_application_tracker.dto.CommunicationLogDTO;
import com.nick.job_application_tracker.mapper.CommunicationLogMapper;
import com.nick.job_application_tracker.model.CommunicationLog;
import com.nick.job_application_tracker.repository.CommunicationLogRepository;

@Service
public class CommunicationLogService {

    private final CommunicationLogRepository repo;
    private final CommunicationLogMapper mapper;

    public CommunicationLogService(CommunicationLogRepository repo, CommunicationLogMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    public List<CommunicationLogDTO> getByJobAppId(Long jobAppId) {
        return repo.findByJobApplicationId(jobAppId).stream()
            .map(mapper::toDTO)
            .toList();
    }

    public CommunicationLogDTO save(CommunicationLogDTO dto) {
        CommunicationLog entity = mapper.toEntity(dto);
        return mapper.toDTO(repo.save(entity));
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}

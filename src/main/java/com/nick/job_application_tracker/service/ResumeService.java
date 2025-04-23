package com.nick.job_application_tracker.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.nick.job_application_tracker.dto.ResumeDTO;
import com.nick.job_application_tracker.mapper.ResumeMapper;
import com.nick.job_application_tracker.model.Resume;
import com.nick.job_application_tracker.repository.ResumeRepository;

@Service
public class ResumeService {

    private final ResumeRepository resumeRepository;

    public ResumeService(ResumeRepository resumeRepository) {
        this.resumeRepository = resumeRepository;
    }

    public List<ResumeDTO> findAll() {
        return resumeRepository.findAll()
            .stream()
            .map(ResumeMapper::toDTO)
            .collect(Collectors.toList());
    }

    public ResumeDTO save(ResumeDTO dto) {
        Resume resume = ResumeMapper.toEntity(dto);
        Resume saved = resumeRepository.save(resume);
        return ResumeMapper.toDTO(saved);
    }

    public void delete(Long id) {
        resumeRepository.deleteById(id);
    }
}

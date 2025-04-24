package com.nick.job_application_tracker.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nick.job_application_tracker.dto.CoverLetterDTO;
import com.nick.job_application_tracker.mapper.CoverLetterMapper;
import com.nick.job_application_tracker.model.CoverLetter;
import com.nick.job_application_tracker.repository.CoverLetterRepository;

@Service
public class CoverLetterService {

    private final CoverLetterRepository coverLetterRepository;
    private final CoverLetterMapper coverLetterMapper;

    @Autowired
    public CoverLetterService(CoverLetterRepository coverLetterRepository, CoverLetterMapper coverLetterMapper) {
        this.coverLetterRepository = coverLetterRepository;
        this.coverLetterMapper = coverLetterMapper;
    }

    public List<CoverLetterDTO> findAll() {
        return coverLetterRepository.findAll().stream()
            .map(coverLetterMapper::toDTO)
            .toList();
    }

    public CoverLetterDTO save(CoverLetterDTO dto) {
        CoverLetter coverLetter = coverLetterMapper.toEntity(dto);
        return coverLetterMapper.toDTO(coverLetterRepository.save(coverLetter));
    }

    public void delete(Long id) {
        coverLetterRepository.deleteById(id);
    }
}

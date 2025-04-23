package com.nick.job_application_tracker.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nick.job_application_tracker.dto.CoverLetterDTO;
import com.nick.job_application_tracker.model.CoverLetter;
import com.nick.job_application_tracker.repository.CoverLetterRepository;

@Service
public class CoverLetterService {

    private final CoverLetterRepository coverLetterRepository;

    @Autowired
    public CoverLetterService(CoverLetterRepository coverLetterRepository) {
        this.coverLetterRepository = coverLetterRepository;
    }

    public List<CoverLetterDTO> findAll() {
        return coverLetterRepository.findAll().stream()
            .map(cl -> new CoverLetterDTO(cl.getId(), cl.getTitle(), cl.getFilePath(), cl.getContent()))
            .toList();
    }

    public CoverLetterDTO save(CoverLetterDTO dto) {
        CoverLetter coverLetter = new CoverLetter();
        coverLetter.setTitle(dto.getTitle());
        coverLetter.setFilePath(dto.getFilePath());
        coverLetter.setContent(dto.getContent());

        CoverLetter saved = coverLetterRepository.save(coverLetter);
        return new CoverLetterDTO(saved.getId(), saved.getTitle(), saved.getFilePath(), saved.getContent());
    }
    
    public void delete(Long id) {
        coverLetterRepository.deleteById(id);
    }

}

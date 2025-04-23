package com.nick.job_application_tracker.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nick.job_application_tracker.model.CoverLetter;
import com.nick.job_application_tracker.repository.CoverLetterRepository;

@Service
public class CoverLetterService {

    private final CoverLetterRepository coverLetterRepository;

    @Autowired
    public CoverLetterService(CoverLetterRepository coverLetterRepository) {
        this.coverLetterRepository = coverLetterRepository;
    }

    public List<CoverLetter> findAll() {
        return coverLetterRepository.findAll();
    }

    public CoverLetter save(CoverLetter coverLetter) {
        return coverLetterRepository.save(coverLetter);
    }

    public void delete(Long id) {
        coverLetterRepository.deleteById(id);
    }
}

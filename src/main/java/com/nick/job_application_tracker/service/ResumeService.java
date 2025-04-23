package com.nick.job_application_tracker.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nick.job_application_tracker.model.Resume;
import com.nick.job_application_tracker.repository.ResumeRepository;

@Service
public class ResumeService {

    private final ResumeRepository resumeRepository;

    @Autowired
    public ResumeService(ResumeRepository resumeRepository) {
        this.resumeRepository = resumeRepository;
    }

    public List<Resume> findAll() {
        return resumeRepository.findAll();
    }

    public Resume save(Resume resume) {
        return resumeRepository.save(resume);
    }

    public void delete(Long id) {
        resumeRepository.deleteById(id);
    }
}

package com.nick.job_application_tracker.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.nick.job_application_tracker.model.Attachment;
import com.nick.job_application_tracker.repository.AttachmentRepository;

@Service
public class AttachmentService {

    private final AttachmentRepository repo;

    public AttachmentService(AttachmentRepository repo) {
        this.repo = repo;
    }

    public List<Attachment> getByJobAppId(Long jobAppId) {
        return repo.findByJobApplicationId(jobAppId);
    }

    public Attachment save(Attachment attachment) {
        return repo.save(attachment);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}

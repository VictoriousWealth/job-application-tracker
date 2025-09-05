package com.nick.job_application_tracker.repository.custom;

import com.nick.job_application_tracker.model.Attachment;

import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


/**
 * Custom repository for {@link Attachment} with advanced query logic.
 */
public interface AttachmentRepositoryCustom {

    /**
     * Find attachments for a user created after a certain time.
     */
    Page<Attachment> findRecentAttachmentsByUser(UUID userId, LocalDateTime since, Pageable pageable);


    /**
     * Count how many attachments of a specific type a user has (non-deleted).
     */
    long countByUserAndType(UUID userId, Attachment.Type type);
}
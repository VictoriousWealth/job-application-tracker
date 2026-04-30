package com.nick.job_application_tracker.repository.inter_face;

import com.nick.job_application_tracker.dto.LegacyIdAdapter;
import com.nick.job_application_tracker.model.Attachment;
import com.nick.job_application_tracker.model.Attachment.Type;
import com.nick.job_application_tracker.repository.custom.AttachmentRepositoryCustom;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for accessing and managing {@link Attachment} entities.
 * Supports filtering, pagination, soft deletion, and advanced querying.
 */
public interface AttachmentRepository
        extends JpaRepository<Attachment, UUID>, AttachmentRepositoryCustom {

    // --- Ownership + soft delete filtering ---

    List<Attachment> findByJobApplicationIdAndDeletedFalse(UUID jobApplicationId);

    default List<Attachment> findByJobApplicationId(UUID jobApplicationId) {
        return findByJobApplicationIdAndDeletedFalse(jobApplicationId);
    }

    default List<Attachment> findByJobApplicationId(Long jobApplicationId) {
        return findByJobApplicationIdAndDeletedFalse(LegacyIdAdapter.fromLong(jobApplicationId));
    }

    Page<Attachment> findByJobApplicationIdAndDeletedFalse(UUID jobApplicationId, Pageable pageable);

    Page<Attachment> findByJobApplicationUserIdAndDeletedFalse(UUID userId, Pageable pageable);

    // --- Type filtering ---

    List<Attachment> findByJobApplicationIdAndTypeAndDeletedFalse(UUID jobApplicationId, Type type);

    Page<Attachment> findByJobApplicationIdAndTypeAndDeletedFalse(UUID jobApplicationId, Type type, Pageable pageable);

    // --- Searchable content (if applicable in filePath or description) ---

    Page<Attachment> findByJobApplicationUserIdAndFilePathContainingIgnoreCaseAndDeletedFalse(UUID userId, String keyword, Pageable pageable);

    // --- Recent entries ---

    List<Attachment> findTop5ByJobApplicationUserIdAndDeletedFalseOrderByCreatedAtDesc(UUID userId);

    // --- Soft-deleted / recycle bin ---

    Page<Attachment> findByJobApplicationIdAndDeletedTrue(UUID jobApplicationId, Pageable pageable);
}

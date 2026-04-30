package com.nick.job_application_tracker.repository.inter_face;

import com.nick.job_application_tracker.model.ScheduledCommunication;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ScheduledCommunicationRepository extends JpaRepository<ScheduledCommunication, UUID> {

    Page<ScheduledCommunication> findByJobApplicationIdAndDeletedFalse(UUID jobApplicationId, Pageable pageable);

    default java.util.List<ScheduledCommunication> findByJobApplicationId(UUID jobApplicationId) {
        return findByJobApplicationIdAndDeletedFalse(jobApplicationId, Pageable.unpaged()).getContent();
    }

    Optional<ScheduledCommunication> findByIdAndJobApplicationUserIdAndDeletedFalse(UUID id, UUID userId);

    Page<ScheduledCommunication> findByJobApplicationUserIdAndDeletedFalse(UUID userId, Pageable pageable);

    Page<ScheduledCommunication> findAllByDeletedFalse(Pageable pageable);

    Page<ScheduledCommunication> findAllByDeletedTrue(Pageable pageable);

    long countByDeletedFalse();
}

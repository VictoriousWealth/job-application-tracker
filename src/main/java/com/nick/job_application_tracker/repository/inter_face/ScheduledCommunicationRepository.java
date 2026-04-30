package com.nick.job_application_tracker.repository.inter_face;

import com.nick.job_application_tracker.dto.LegacyIdAdapter;
import com.nick.job_application_tracker.model.ScheduledCommunication;
import com.nick.job_application_tracker.repository.custom.ScheduledCommunicationRepositoryCustom;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ScheduledCommunicationRepository extends JpaRepository<ScheduledCommunication, UUID>, ScheduledCommunicationRepositoryCustom {

    Page<ScheduledCommunication> findByJobApplicationIdAndDeletedFalse(UUID jobApplicationId, Pageable pageable);

    default java.util.List<ScheduledCommunication> findByJobApplicationId(UUID jobApplicationId) {
        return findByJobApplicationIdAndDeletedFalse(jobApplicationId, Pageable.unpaged()).getContent();
    }

    default java.util.List<ScheduledCommunication> findByJobApplicationId(Long jobApplicationId) {
        return findByJobApplicationId(LegacyIdAdapter.fromLong(jobApplicationId));
    }

    default java.util.Optional<ScheduledCommunication> findById(Long id) {
        return findById(LegacyIdAdapter.fromLong(id));
    }

    default void deleteById(Long id) {
        deleteById(LegacyIdAdapter.fromLong(id));
    }

    Page<ScheduledCommunication> findAllByDeletedFalse(Pageable pageable);

    Page<ScheduledCommunication> findAllByDeletedTrue(Pageable pageable);

    long countByDeletedFalse();
}

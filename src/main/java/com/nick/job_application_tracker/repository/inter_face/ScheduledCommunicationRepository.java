package com.nick.job_application_tracker.repository.inter_face;

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

    Page<ScheduledCommunication> findAllByDeletedFalse(Pageable pageable);

    Page<ScheduledCommunication> findAllByDeletedTrue(Pageable pageable);

    long countByDeletedFalse();
}

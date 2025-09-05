package com.nick.job_application_tracker.repository.custom;

import com.nick.job_application_tracker.model.ScheduledCommunication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.UUID;

public interface ScheduledCommunicationRepositoryCustom {

    Page<ScheduledCommunication> findRecentByUser(UUID userId, LocalDateTime since, Pageable pageable);

    long countByUserAndType(UUID userId, ScheduledCommunication.Type type);

    Page<ScheduledCommunication> searchByNotes(UUID userId, String keyword, Pageable pageable);
}

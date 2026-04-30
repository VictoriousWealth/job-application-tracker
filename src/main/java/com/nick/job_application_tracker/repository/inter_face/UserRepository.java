package com.nick.job_application_tracker.repository.inter_face;

import com.nick.job_application_tracker.model.User;
import com.nick.job_application_tracker.repository.custom.UserRepositoryCustom;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID>, UserRepositoryCustom {

    Optional<User> findByEmailAndDeletedFalse(String email);

    default Optional<User> findByEmail(String email) {
        return findByEmailAndDeletedFalse(email);
    }

    default Optional<User> findById(Long id) {
        return findById(com.nick.job_application_tracker.dto.LegacyIdAdapter.fromLong(id));
    }

    Page<User> findAllByDeletedFalse(Pageable pageable);

    Page<User> findAllByDeletedTrue(Pageable pageable);
}

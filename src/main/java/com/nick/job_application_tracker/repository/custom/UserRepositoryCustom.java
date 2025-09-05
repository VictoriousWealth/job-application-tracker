package com.nick.job_application_tracker.repository.custom;

import com.nick.job_application_tracker.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserRepositoryCustom {

    Page<User> searchByEmail(String keyword, Pageable pageable);

    long countActiveUsers();
}

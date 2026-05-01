package com.nick.job_application_tracker.util;

import com.nick.job_application_tracker.model.User;
import com.nick.job_application_tracker.repository.interfaces.UserRepository;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;

public class SecurityUtils {

    public static User getCurrentUserOrThrow(UserRepository userRepository) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new IllegalStateException("No authenticated user found in context.");
        }

        return userRepository.findByEmailAndDeletedFalse(auth.getName())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found in database: " + auth.getName()));
    }
}

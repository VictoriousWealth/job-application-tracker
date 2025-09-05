package com.nick.job_application_tracker.mapper;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.nick.job_application_tracker.dto.special.UserDetailDTO;
import com.nick.job_application_tracker.model.Role;
import com.nick.job_application_tracker.model.User;

@Component
public class UserMapper {

    public UserDetailDTO toDTO(User user) {
        return new UserDetailDTO(
            user.getId(),
            user.getEmail(),
            user.isEnabled(),
            user.getRoles().stream().map(Role::name).collect(Collectors.toSet())
        );
    }
}

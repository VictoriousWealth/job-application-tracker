package com.nick.job_application_tracker.mapper;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.nick.job_application_tracker.dto.UserInfoDTO;
import com.nick.job_application_tracker.model.Role;
import com.nick.job_application_tracker.model.User;

@Component
public class UserMapper {

    public UserInfoDTO toDTO(User user) {
        return new UserInfoDTO(
            user.getId(),
            user.getEmail(),
            user.isEnabled(),
            user.getRoles().stream().map(Role::name).collect(Collectors.toSet())
        );
    }
}

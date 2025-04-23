package com.nick.job_application_tracker.dto;

import java.time.LocalDateTime;

public class ErrorResponseDTO {
    public int status;
    public String error;
    public String message;
    public LocalDateTime timestamp = LocalDateTime.now();

    public ErrorResponseDTO(int status, String error, String message) {
        this.status = status;
        this.error = error;
        this.message = message;
    }
}

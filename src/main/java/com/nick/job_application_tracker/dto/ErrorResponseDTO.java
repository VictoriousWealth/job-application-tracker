package com.nick.job_application_tracker.dto;

import java.time.OffsetDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Standard structure for API error responses")
public class ErrorResponseDTO {

    @Schema(description = "HTTP status code", example = "400")
    private int status;

    @Schema(description = "Short error type", example = "Bad Request")
    private String error;

    @Schema(description = "Detailed message about the error", example = "Validation failed for field 'email'")
    private String message;

    @Schema(description = "Request path that caused the error", example = "/api/users/signup")
    private String path;

    @Schema(description = "Timestamp when the error occurred", example = "2025-06-15T12:34:56.123Z")
    private OffsetDateTime timestamp;

    public ErrorResponseDTO(int status, String error, String message, String path) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.timestamp = OffsetDateTime.now();
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }
}

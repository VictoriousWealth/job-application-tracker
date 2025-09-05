package com.nick.job_application_tracker.dto.special;

import java.time.OffsetDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Standard structure for API error responses")
public class ErrorResponseDTO {

    @Schema(description = "HTTP status code", example = "400")
    private final int status;

    @Schema(description = "Short error type", example = "Bad Request")
    private final String error;

    @Schema(description = "Detailed message about the error", example = "Validation failed for field 'email'")
    private final String message;

    @Schema(description = "Request path that caused the error", example = "/api/users/signup")
    private final String path;

    @Schema(description = "Timestamp when the error occurred", example = "2025-06-15T12:34:56.123Z")
    private final OffsetDateTime timestamp;

    @Schema(hidden = true)
    private final String requestId; 
    
    @Schema(description = "List of field-specific validation errors")
    private final List<FieldError> fieldErrors;

    public record FieldError(String field, String message) {}
    
    public ErrorResponseDTO(int status, String error, String message, String path, String requestId, List<FieldError> fieldErrors) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.timestamp = OffsetDateTime.now();
        this.requestId = requestId;
        this.fieldErrors = fieldErrors;
    }

    public ErrorResponseDTO(int status, String error, String message, String path, String requestId) {
        this(status, error, message, path, requestId, null);
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

    public String getRequestId() {
        return requestId;
    }

    public List<FieldError> getFieldErrors() {
        return fieldErrors;
    }
}

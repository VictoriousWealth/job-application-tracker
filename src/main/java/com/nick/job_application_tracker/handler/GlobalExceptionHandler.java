package com.nick.job_application_tracker.handler;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;

import javax.naming.AuthenticationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.nick.job_application_tracker.common.LogKeys;
import com.nick.job_application_tracker.dto.special.ErrorResponseDTO;
import com.nick.job_application_tracker.exception.client.BadRequestException;
import com.nick.job_application_tracker.exception.client.ConflictException;
import com.nick.job_application_tracker.exception.client.ForbiddenException;
import com.nick.job_application_tracker.exception.client.NotFoundException;
import com.nick.job_application_tracker.exception.client.RateLimitExceededException;
import com.nick.job_application_tracker.exception.client.UnauthorizedException;
import com.nick.job_application_tracker.exception.client.ValidationException;
import com.nick.job_application_tracker.exception.server.InternalServerErrorException;
import com.nick.job_application_tracker.exception.server.ServiceUnavailableException;
import com.nick.job_application_tracker.exception.server.specialized.DownstreamApiUnavailableException;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import static net.logstash.logback.argument.StructuredArguments.kv;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler {

    private static final String RETRY_AFTER = "Retry-After";
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final String UNKNOWN = "unknown";

    private ErrorResponseDTO buildError(HttpStatus status, String message, HttpServletRequest request, List<ErrorResponseDTO.FieldError> fieldErrors) {
        String requestId = Optional.ofNullable(MDC.get(LogKeys.REQUEST_ID)).orElse(UNKNOWN);
        return new ErrorResponseDTO(
            status.value(),
            status.getReasonPhrase(),
            message,
            request.getRequestURI(),
            requestId,
            fieldErrors
        );
    }

    private ErrorResponseDTO buildError(HttpStatus status, String message, HttpServletRequest request) {
        return buildError(status, message, request, null);
    }

    private ResponseEntity<ErrorResponseDTO> error(HttpStatus status, String message, HttpServletRequest request) {
        return ResponseEntity.status(status).body(buildError(status, message, request));
    }

    private String resolveBadRequestMessage(Exception ex) {
        if (ex instanceof MethodArgumentNotValidException validEx) {
            return validEx.getBindingResult().getAllErrors().stream()
                .map(MessageSourceResolvable::getDefaultMessage)
                .reduce((a, b) -> a + "; " + b)
                .orElse("Validation failed");
        }
        if (ex instanceof ConstraintViolationException cve) {
            return cve.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .reduce((a, b) -> a + "; " + b)
                .orElse("Constraint violation");
        }
        return ex.getMessage();
    }


    private void logException(HttpServletRequest request, Exception ex, HttpStatus status) {
        String requestId = Optional.ofNullable(MDC.get(LogKeys.REQUEST_ID)).orElse(UNKNOWN);

        if (status.is4xxClientError()) {
            log.warn("Handled client error",
                kv(LogKeys.REQUEST_ID, requestId),
                kv("method", request.getMethod()),
                kv("uri", request.getRequestURI()),
                kv("status", status.value()),
                kv("reason", status.getReasonPhrase()),
                kv("exception", ex.getClass().getSimpleName()),
                kv("message", ex.getMessage())
            );
        } else {
            log.error("Unhandled server error",
                kv(LogKeys.REQUEST_ID, requestId),
                kv("method", request.getMethod()),
                kv("uri", request.getRequestURI()),
                kv("status", status.value()),
                kv("reason", status.getReasonPhrase()),
                kv("exception", ex.getClass().getSimpleName()),
                kv("message", ex.getMessage()),
                ex
            );
        }
    }

    // 400 - Bad Request
    @ExceptionHandler({
        MethodArgumentNotValidException.class,
        IllegalArgumentException.class,
        ConstraintViolationException.class,
        BadRequestException.class
    })
    public ResponseEntity<ErrorResponseDTO> handleBadRequest(Exception ex, HttpServletRequest request) {
        String message = resolveBadRequestMessage(ex);
        List<ErrorResponseDTO.FieldError> fieldErrors = null;

        if (ex instanceof MethodArgumentNotValidException validEx) {
            fieldErrors = validEx.getBindingResult().getFieldErrors().stream()
                .map(err -> new ErrorResponseDTO.FieldError(err.getField(), err.getDefaultMessage()))
                .toList();
        }

        if (ex instanceof ConstraintViolationException cve) {
            fieldErrors = cve.getConstraintViolations().stream()
                .map(v -> new ErrorResponseDTO.FieldError(v.getPropertyPath().toString(), v.getMessage()))
                .toList();
        }

        logException(request, ex, HttpStatus.BAD_REQUEST);
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(buildError(HttpStatus.BAD_REQUEST, message, request, fieldErrors));
    }



    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDTO> handleJsonParse(HttpMessageNotReadableException ex, HttpServletRequest request) {
        logException(request, ex, HttpStatus.BAD_REQUEST);
        return error(HttpStatus.BAD_REQUEST, "Malformed JSON request.", request);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponseDTO> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        String message = "Invalid value for parameter '" + ex.getName() + "'. Expected type: "
            + (ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : UNKNOWN);
        logException(request, ex, HttpStatus.BAD_REQUEST);
        return error(HttpStatus.BAD_REQUEST, message, request);
    }

    @ExceptionHandler({ UnauthorizedException.class, AuthenticationException.class })
    public ResponseEntity<ErrorResponseDTO> handleUnauthorized(Exception ex, HttpServletRequest request) {
        logException(request, ex, HttpStatus.UNAUTHORIZED);
        return error(HttpStatus.UNAUTHORIZED, "Authentication required.", request);
    }

    @ExceptionHandler({ ForbiddenException.class, AccessDeniedException.class })
    public ResponseEntity<ErrorResponseDTO> handleForbidden(Exception ex, HttpServletRequest request) {
        logException(request, ex, HttpStatus.FORBIDDEN);
        return error(HttpStatus.FORBIDDEN, "You do not have permission to perform this action.", request);
    }

    @ExceptionHandler({ NotFoundException.class, EntityNotFoundException.class })
    public ResponseEntity<ErrorResponseDTO> handleNotFound(Exception ex, HttpServletRequest request) {
        logException(request, ex, HttpStatus.NOT_FOUND);
        return error(HttpStatus.NOT_FOUND, "The requested resource was not found.", request);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponseDTO> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        logException(request, ex, HttpStatus.METHOD_NOT_ALLOWED);
        return error(HttpStatus.METHOD_NOT_ALLOWED, "HTTP method not allowed on this endpoint.", request);
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ResponseEntity<ErrorResponseDTO> handleNotAcceptable(HttpMediaTypeNotAcceptableException ex, HttpServletRequest request) {
        logException(request, ex, HttpStatus.NOT_ACCEPTABLE);
        return error(HttpStatus.NOT_ACCEPTABLE, "Response format not acceptable.", request);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponseDTO> handleConflict(ConflictException ex, HttpServletRequest request) {
        logException(request, ex, HttpStatus.CONFLICT);
        return error(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponseDTO> handleUnsupportedMediaType(HttpMediaTypeNotSupportedException ex, HttpServletRequest request) {
        logException(request, ex, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        return error(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Unsupported content type.", request);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationException(ValidationException ex, HttpServletRequest request) {
        logException(request, ex, HttpStatus.UNPROCESSABLE_ENTITY);
        return error(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), request);
    }

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<ErrorResponseDTO> handleTooManyRequests(RateLimitExceededException ex, HttpServletRequest request) {
        logException(request, ex, HttpStatus.TOO_MANY_REQUESTS);
        return ResponseEntity
            .status(HttpStatus.TOO_MANY_REQUESTS)
            .header(RETRY_AFTER, "60")
            .body(buildError(HttpStatus.TOO_MANY_REQUESTS, ex.getMessage(), request));
    }

    @ExceptionHandler(InternalServerErrorException.class)
    public ResponseEntity<ErrorResponseDTO> handleInternalError(InternalServerErrorException ex, HttpServletRequest request) {
        logException(request, ex, HttpStatus.INTERNAL_SERVER_ERROR);
        return error(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request);
    }


    @ExceptionHandler(DownstreamApiUnavailableException.class)
    public ResponseEntity<ErrorResponseDTO> handleDownstreamUnavailable(DownstreamApiUnavailableException ex, HttpServletRequest request) {
        logException(request, ex, HttpStatus.SERVICE_UNAVAILABLE);
        return ResponseEntity
            .status(HttpStatus.SERVICE_UNAVAILABLE)
            .header(RETRY_AFTER, "60")
            .body(buildError(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage(), request));
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<ErrorResponseDTO> handleServiceUnavailable(ServiceUnavailableException ex, HttpServletRequest request) {
        logException(request, ex, HttpStatus.SERVICE_UNAVAILABLE);
        return ResponseEntity
            .status(HttpStatus.SERVICE_UNAVAILABLE)
            .header(RETRY_AFTER, "120")
            .body(buildError(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage(), request));
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ErrorResponseDTO> handleThrowable(Throwable ex, HttpServletRequest request) {
        logException(request, ex instanceof Exception e ? e : new Exception(ex), HttpStatus.INTERNAL_SERVER_ERROR);
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "Critical system error.", request);
    }

}

package com.nick.job_application_tracker.exception.client_exception;

import com.nick.job_application_tracker.exception.base_case.ApiException;

public abstract class ClientException extends ApiException {
    public ClientException(String message, Throwable cause) {
        super(message, cause);
    }
}

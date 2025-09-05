package com.nick.job_application_tracker.exception.server_exception.specialised_case;

import com.nick.job_application_tracker.exception.server_exception.ServiceUnavailableException;

public class DownstreamApiUnavailableException extends ServiceUnavailableException {
    public DownstreamApiUnavailableException(String serviceName, Throwable cause) {
        super(serviceName + " is currently unavailable.", cause);
    }
}

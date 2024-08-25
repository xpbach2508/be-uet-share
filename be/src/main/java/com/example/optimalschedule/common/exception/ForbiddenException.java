package com.example.optimalschedule.common.exception;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends RuntimeException {

    private final HttpStatus statusCode = HttpStatus.FORBIDDEN;

    public ForbiddenException(String message) {
        super(message);
    }

    public HttpStatus getStatusCode() {
        return statusCode;
    }
}

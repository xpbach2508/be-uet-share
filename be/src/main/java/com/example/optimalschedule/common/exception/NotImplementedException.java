package com.example.optimalschedule.common.exception;

import org.springframework.http.HttpStatus;

public class NotImplementedException extends RuntimeException {

    private final HttpStatus statusCode = HttpStatus.NOT_IMPLEMENTED;

    public NotImplementedException(String message) {
        super(message);
    }

    public HttpStatus getStatusCode() {
        return statusCode;
    }

}

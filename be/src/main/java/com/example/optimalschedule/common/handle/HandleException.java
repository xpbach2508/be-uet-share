package com.example.optimalschedule.common.handle;

import com.example.optimalschedule.common.exception.BadRequestException;
import com.example.optimalschedule.common.exception.ForbiddenException;
import com.example.optimalschedule.common.exception.NotFoundException;
import com.example.optimalschedule.common.exception.NotImplementedException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class HandleException {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(NotFoundException e) {
        String errorMessage = "Error: " + e.getMessage();
        return ResponseEntity.status(e.getStatusCode()).body(errorMessage);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<String> handleForbiddenException(ForbiddenException e) {
        String errorMessage = "Error: " + e.getMessage();
        return ResponseEntity.status(e.getStatusCode()).body(errorMessage);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<String> handleBadRequestException(BadRequestException e) {
        String errorMessage = "Error: " + e.getMessage();
        return ResponseEntity.status(e.getStatusCode()).body(errorMessage);
    }

    @ExceptionHandler(NotImplementedException.class)
    public ResponseEntity<String> handleNotImplementedException(NotImplementedException e) {
        String errorMessage = "Error: " + e.getMessage();
        return ResponseEntity.status(e.getStatusCode()).body(errorMessage);
    }

}

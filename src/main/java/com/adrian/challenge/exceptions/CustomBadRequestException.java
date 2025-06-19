package com.adrian.challenge.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a request contains invalid data or fails validation.
 * Results in a 400 Bad Request response when handled by GlobalExceptionHandler.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CustomBadRequestException extends RuntimeException {

    public CustomBadRequestException(String message) {
        super(message);
    }
    public CustomBadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}

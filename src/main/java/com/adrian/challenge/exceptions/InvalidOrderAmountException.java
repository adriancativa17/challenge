package com.adrian.challenge.exceptions;

public class InvalidOrderAmountException extends RuntimeException {
    public InvalidOrderAmountException(String message) {
        super(message);
    }
}

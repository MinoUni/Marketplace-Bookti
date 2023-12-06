package com.teamchallenge.bookti.exception;

public class PasswordIsNotConfirmedException extends RuntimeException {
    public PasswordIsNotConfirmedException(String message) {
        super(message);
    }
}

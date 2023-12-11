package com.teamchallenge.bookti.exception;

public class PasswordIsNotMatchesException extends RuntimeException {
    public PasswordIsNotMatchesException(String message) {
        super(message);
    }
}

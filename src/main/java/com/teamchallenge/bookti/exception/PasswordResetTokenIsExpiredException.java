package com.teamchallenge.bookti.exception;

public class PasswordResetTokenIsExpiredException extends RuntimeException{
    public PasswordResetTokenIsExpiredException(String message) {
        super(message);
    }
}

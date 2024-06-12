package com.teamchallenge.bookti.exception.subscription;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class SubscriptionException extends RuntimeException {
    private HttpStatus httpStatus;

    public SubscriptionException(String message) {
        super(message);
    }

    public SubscriptionException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}

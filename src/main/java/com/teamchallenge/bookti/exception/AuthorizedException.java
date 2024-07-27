package com.teamchallenge.bookti.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AuthorizedException extends RuntimeException {

  private HttpStatus httpStatus;

  public AuthorizedException(String message) {
    super(message);
  }

  public AuthorizedException(String message, HttpStatus httpStatus) {
    super(message);
    this.httpStatus = httpStatus;
  }
}

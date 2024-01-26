package com.teamchallenge.bookti.exception;

/**
 * Exception that occurs when receive unauthorized redirect URI in request.
 *
 * @author MinoUni
 */
public class BadRequestException extends RuntimeException {
  public BadRequestException(String message) {
    super(message);
  }
}

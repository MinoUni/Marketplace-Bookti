package com.teamchallenge.bookti.exception;

/**
 * Exception that appears when refreshToken already revoked.
 *
 * @author Maksym Reva
 */
public class RefreshTokenAlreadyRevokedException extends RuntimeException {
  public RefreshTokenAlreadyRevokedException(String message) {
    super(message);
  }
}

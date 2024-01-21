package com.teamchallenge.bookti.exception;

/**
 * Exception that appears when {@link com.teamchallenge.bookti.model.PasswordResetToken} is expired.
 *
 * @author Katherine Sokol
 */
public class PasswordResetTokenIsExpiredException extends RuntimeException {
  public PasswordResetTokenIsExpiredException(String message) {
    super(message);
  }
}

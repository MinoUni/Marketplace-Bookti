package com.teamchallenge.bookti.exception;

/**
 * Exception that appears when {@link com.teamchallenge.bookti.model.PasswordResetToken} not found.
 *
 * @author Katherine Sokol
 */
public class PasswordResetTokenNotFoundException extends RuntimeException {
  public PasswordResetTokenNotFoundException(String message) {
    super(message);
  }
}

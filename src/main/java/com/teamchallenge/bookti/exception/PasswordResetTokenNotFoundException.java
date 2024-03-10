package com.teamchallenge.bookti.exception;

/**
 * Exception that appears when password reset token not found.
 *
 * @author Katherine Sokol
 */
public class PasswordResetTokenNotFoundException extends RuntimeException {
  public PasswordResetTokenNotFoundException(String message) {
    super(message);
  }
}

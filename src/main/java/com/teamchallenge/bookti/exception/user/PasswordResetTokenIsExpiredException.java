package com.teamchallenge.bookti.exception.user;

/**
 * Exception that appears when password reset token is expired.
 *
 * @author Katherine Sokol
 */
public class PasswordResetTokenIsExpiredException extends RuntimeException {
  public PasswordResetTokenIsExpiredException(String message) {
    super(message);
  }
}

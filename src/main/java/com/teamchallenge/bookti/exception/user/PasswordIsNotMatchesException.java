package com.teamchallenge.bookti.exception.user;

/**
 * Exception that appears when password is not matches.
 *
 * @author Maksym Reva
 */
public class PasswordIsNotMatchesException extends RuntimeException {
  public PasswordIsNotMatchesException(String message) {
    super(message);
  }
}

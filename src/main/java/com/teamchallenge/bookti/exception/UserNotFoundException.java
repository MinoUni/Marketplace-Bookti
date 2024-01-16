package com.teamchallenge.bookti.exception;

/**
 * Exception that appears when {@link com.teamchallenge.bookti.model.UserEntity} not found
 * in database.
 *
 * @author Maksym Reva
 */
public class UserNotFoundException extends RuntimeException {

  public UserNotFoundException(String message) {
    super(message);
  }
}

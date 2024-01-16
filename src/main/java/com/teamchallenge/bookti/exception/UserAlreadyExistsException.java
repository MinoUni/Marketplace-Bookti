package com.teamchallenge.bookti.exception;

/**
 * Exception that appears when {@link com.teamchallenge.bookti.model.UserEntity} already exists
 * in database.
 *
 * @author Katherine Sokol
 */
public class UserAlreadyExistsException extends RuntimeException {

  public UserAlreadyExistsException(String message) {
    super(message);
  }
}

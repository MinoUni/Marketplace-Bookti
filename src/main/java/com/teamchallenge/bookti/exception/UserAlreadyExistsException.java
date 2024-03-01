package com.teamchallenge.bookti.exception;

import com.teamchallenge.bookti.user.UserEntity;

/**
 * Exception that appears when {@link UserEntity} already exists
 * in database.
 *
 * @author Katherine Sokol
 */
public class UserAlreadyExistsException extends RuntimeException {

  public UserAlreadyExistsException(String message) {
    super(message);
  }
}

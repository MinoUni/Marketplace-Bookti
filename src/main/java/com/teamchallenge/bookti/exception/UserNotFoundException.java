package com.teamchallenge.bookti.exception;

import com.teamchallenge.bookti.user.UserEntity;

/**
 * Exception that appears when {@link UserEntity} not found
 * in database.
 *
 * @author Maksym Reva
 */
public class UserNotFoundException extends RuntimeException {

  public UserNotFoundException(String message) {
    super(message);
  }
}

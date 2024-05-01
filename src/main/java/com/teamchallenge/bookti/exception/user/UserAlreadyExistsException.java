package com.teamchallenge.bookti.exception.user;

import com.teamchallenge.bookti.user.User;

/**
 * Exception that appears when {@link User} already exists
 * in database.
 *
 * @author Katherine Sokol
 */
public class UserAlreadyExistsException extends RuntimeException {

  public UserAlreadyExistsException(String message) {
    super(message);
  }
}

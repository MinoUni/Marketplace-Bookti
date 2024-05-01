package com.teamchallenge.bookti.exception.user;

import com.teamchallenge.bookti.user.User;

/**
 * Exception that appears when {@link User} not found
 * in database.
 *
 * @author Maksym Reva
 */
public class UserNotFoundException extends RuntimeException {

  public UserNotFoundException(String message) {
    super(message);
  }
}

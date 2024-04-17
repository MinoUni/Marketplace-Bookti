package com.teamchallenge.bookti.exception;

/**
 * Exception class used when occur issues with book operations.
 *
 * @author MinoUni
 * @version 1.0
 */
public class BookException extends RuntimeException {

  public BookException() {}

  public BookException(String message) {
    super(message);
  }
}

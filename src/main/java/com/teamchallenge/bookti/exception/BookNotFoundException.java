package com.teamchallenge.bookti.exception;

/**
 * Custom exception for cases when we can't find book.
 *
 * @author MinoUni
 * @version 1.0
 */
public class BookNotFoundException extends RuntimeException {
  public BookNotFoundException(String message) {
    super(message);
  }
}

package com.teamchallenge.bookti.exception;

/**
 * Exception that can be thrown when trying to save file with unsupported type.
 *
 * @author MinoUni
 * @version 1.0
 */
public class UnsupportedFileTypeException extends RuntimeException {
  public UnsupportedFileTypeException(String message) {
    super(message);
  }
}

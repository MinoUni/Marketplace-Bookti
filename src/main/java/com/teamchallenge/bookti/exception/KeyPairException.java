package com.teamchallenge.bookti.exception;

/**
 * Exception that appears when KeyPair cannot be created.
 *
 * @author Maksym Reva
 */
public class KeyPairException extends RuntimeException {

  public KeyPairException(String message) {
    super(message);
  }
}

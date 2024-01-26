package com.teamchallenge.bookti.exception;

/**
 * Exception that appears when OAuth 2.0 process failed for some reason.
 *
 * @author MinoUni
 */
public class Oauth2AuthenticationProcessingException extends RuntimeException {

  public Oauth2AuthenticationProcessingException(String message) {
    super(message);
  }
}

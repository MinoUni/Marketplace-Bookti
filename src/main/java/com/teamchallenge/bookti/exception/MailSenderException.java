package com.teamchallenge.bookti.exception;

/**
 * Exception that appears when email cannot be sent to recipient.
 *
 * @author Katherine Sokol
 */
public class MailSenderException extends RuntimeException {
  public MailSenderException(String message) {
    super(message);
  }
}

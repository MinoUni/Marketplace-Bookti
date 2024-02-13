package com.teamchallenge.bookti.exception;

/**
 * Exception that can be thrown when error occur during uploading files to Cloudinary.
 *
 * @author MinoUni
 * @version 1.0
 */
public class CloudinaryException extends RuntimeException {

  public CloudinaryException(String message) {
    super(message);
  }
}

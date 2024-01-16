package com.teamchallenge.bookti.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * Response that contains information about what error appeared.
 * ErrorResponse contains fields 'timestamp', 'statusCode','message' and 'details'.
 *
 * @author Maksym Reva
 */
@Getter
@Setter
public class ErrorResponse {

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ssa")
  private LocalDateTime timestamp;

  @JsonProperty("status_code")
  private Integer statusCode;

  private String message;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private List<String> details;

  /**
   * Constructor that creates ErrorResponse with {@link LocalDateTime#now() current timestamp},
   * response's statusCode and message.
   *
   * @param statusCode HTTP response status code
   * @param message response message
   */
  public ErrorResponse(Integer statusCode, String message) {
    this.timestamp = LocalDateTime.now();
    this.statusCode = statusCode;
    this.message = message;
    this.details = null;
  }

  /**
   * Constructor that creates ErrorResponse with {@link LocalDateTime#now() current timestamp},
   * response's statusCode, message details.
   *
   * @param statusCode HTTP response status code
   * @param message response message
   * @param details error details
   */
  public ErrorResponse(Integer statusCode, String message, List<String> details) {
    this.timestamp = LocalDateTime.now();
    this.statusCode = statusCode;
    this.message = message;
    this.details = details;
  }
}

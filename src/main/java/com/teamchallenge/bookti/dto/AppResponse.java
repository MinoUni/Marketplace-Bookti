package com.teamchallenge.bookti.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * AppResponse that contains fields 'timestamp', 'statusCode' and 'message'.
 *
 * @author Maksym Reva
 */
@Setter
@Getter
public class AppResponse {

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ssa")
  private LocalDateTime timestamp;

  @JsonProperty("status_code")
  private Integer statusCode;

  private String message;

  /**
   * Constructor that creates AppResponse with {@link LocalDateTime#now() current timestamp},
   * response's statusCode and message.
   *
   * @param statusCode HTTP response status code
   * @param message response message
   */
  public AppResponse(Integer statusCode, String message) {
    this.timestamp = LocalDateTime.now();
    this.statusCode = statusCode;
    this.message = message;
  }
}

package com.teamchallenge.bookti.dto.authorization;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * Response that contains fields 'userId' and 'resetToken'.
 *
 * @author Katherine Sokol
 */
@Builder
@Data
@AllArgsConstructor
public class MailResetPasswordResponse {

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ssa")
  private LocalDateTime timestamp;

  @JsonProperty("user_id")
  private String userId;
}

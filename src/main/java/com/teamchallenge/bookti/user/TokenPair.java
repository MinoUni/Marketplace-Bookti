package com.teamchallenge.bookti.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * TokenPair that contains fields 'timestamp', 'userId', 'accessToken' and 'refreshToken'.
 *
 * @author Maksym Reva
 */
@Builder
@Data
@AllArgsConstructor
public class TokenPair {

  @Builder.Default
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ssa")
  private LocalDateTime timestamp = LocalDateTime.now();

  @JsonProperty("user_id")
  private String userId;

  @JsonProperty("access_token")
  private String accessToken;

  @JsonProperty("refresh_token")
  private String refreshToken;
}

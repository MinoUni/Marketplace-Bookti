package com.teamchallenge.bookti.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * UserTokenPair that contains fields 'userId' and 'refreshToken'.
 *
 * @author Maksym Reva
 */
@Getter
@Setter
@AllArgsConstructor
class UserTokenPair {

  @JsonProperty("user_id")
  @NotBlank(message = "Property <user_id> can't be blank")
  private String userId;

  @JsonProperty("refresh_token")
  @NotBlank(message = "Property <refresh_token> can't be blank")
  private String refreshToken;
}

package com.teamchallenge.bookti.user.dto;

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
public class UserTokenPair {

  @NotBlank(message = "Property <userId> can't be blank")
  private Integer userId;

  @NotBlank(message = "Property <refreshToken> can't be blank")
  private String refreshToken;
}

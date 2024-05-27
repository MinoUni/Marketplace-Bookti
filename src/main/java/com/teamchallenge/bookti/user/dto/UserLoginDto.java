package com.teamchallenge.bookti.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Request that contains user's email and password to lon in site if user is registered.
 * Request contains fields 'email' and 'password'.
 *
 * @author Maksym Reva
 */
@Data
@AllArgsConstructor
public class UserLoginDto {

  @Schema(type = "string", example = "mark.javar@gmail.com")
  @NotBlank(message = "Field <email> must not be blank")
  @Email
  private String email;

  @Schema(type = "string", example = "Javard1rkk")
  @Size(min = 8, max = 20, message = "Password must be from 8 to 20 symbols length")
  @NotBlank(message = "Field <password> must not be blank")
  private String password;
}

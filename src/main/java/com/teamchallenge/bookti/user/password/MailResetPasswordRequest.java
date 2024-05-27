package com.teamchallenge.bookti.user.password;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request that contains field 'email'.
 *
 * @author Katherine Sokol
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MailResetPasswordRequest {

  @Schema(type = "string", example = "mark.javar@gmail.com")
  @NotBlank(message = "Field <email> must not be blank")
  @Email
  private String email;
}

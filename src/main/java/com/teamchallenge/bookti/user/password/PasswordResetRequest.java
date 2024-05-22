package com.teamchallenge.bookti.user.password;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Request that contains fields 'password', 'confirmPassword' and 'resetToken'.
 *
 * @author Katherine Sokol
 */
@Data
@AllArgsConstructor
public class PasswordResetRequest {

  @Schema(type = "string", example = "Javard1rkk")
  @NotBlank(message = "Field <password> must not be blank")
  @Size(min = 8, max = 20, message = "Password must be from 8 to 20 symbols length")
  @Pattern(
      regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,20}$",
      message =
          """
          Password must contain minimum eight characters, at least one uppercase letter,
          one lowercase letter and one number
          """)
  private String password;

  @Schema(type = "string", example = "Javard1rkk")
  @NotBlank(message = "Field <confirm_password> must not be blank")
  @Size(min = 8, max = 20, message = "Password must be from 8 to 20 symbols length")
  @Pattern(
      regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,20}$",
      message =
          """
          Password must contain minimum eight characters, at least one uppercase letter,
          one lowercase letter and one number
          """)
  private String confirmPassword;

  @Schema(type = "string", example = "4183d7d1-7f9e-44a0-9cb9-ece3d001a0d0")
  @NotBlank(message = "Field <reset_token> must not be blank")
  private String resetToken;
}

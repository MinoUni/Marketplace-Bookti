package com.teamchallenge.bookti.dto.authorization;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Request that contains information about new user who wants to register on site.
 * Request contains fields 'fullName', 'email', 'password' and 'confirmPassword'.
 *
 * @author Katherine Sokol and Maksym Reva
 */
@AllArgsConstructor
@Data
public class NewUserRegistrationRequest {

  @JsonProperty("full_name")
  @NotBlank(message = "Field <full_name> must not be blank")
  private String fullName;

  @Schema(type = "string", example = "mark.javar@gmail.com")
  @NotBlank(message = "Field <email> must not be blank")
  @Pattern(regexp =  "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+"
      + "(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$",
      message = "Invalid email")
  private String email;

  @Schema(type = "string", example = "Javard1rkk")
  @NotBlank(message = "Field <password> must not be blank")
  @Size(min = 8, max = 20, message = "Password must be from 8 to 20 symbols length")
  @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,20}$",
      message = """
          Password must contain minimum eight characters, at least one uppercase letter, 
          one lowercase letter and one number
          """)
  private String password;

  @Schema(type = "string", example = "Javard1rkk")
  @JsonProperty("confirm_password")
  @NotBlank(message = "Field <confirm_password> must not be blank")
  @Size(min = 8, max = 20, message = "Password must be from 8 to 20 symbols length")
  @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,20}$",
      message = """
          Password must contain minimum eight characters, at least one uppercase letter, 
          one lowercase letter and one number
          """)
  private String confirmPassword;

  @NotBlank(message = "Field <city> must not be blank")
  private String city;

}


package com.teamchallenge.bookti.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO with user data to update.
 *
 * @author MinoUni
 * @version 1.0
 */
@Data
@AllArgsConstructor
public class UserUpdateDto {

  @Schema(type = "string", example = "mark.javar@gmail.com")
  @NotBlank(message = "Field <email> must not be blank")
  @Email
  private String email;

  @NotBlank(message = "Field <full_name> must not be blank")
  private String fullName;

  @NotBlank(message = "Field <location> must not be blank")
  private String location;

  @Size(min = 5, max = 32, message = "Must be from 5 to 32 symbols length")
  private String telegramId;

  private Boolean displayEmail;

  private Boolean displayTelegram;
}

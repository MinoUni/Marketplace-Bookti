package com.teamchallenge.bookti.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request that contains field 'email'.
 *
 * @author Katherine Sokol
 * */
@Data
@NoArgsConstructor
@AllArgsConstructor
class MailResetPasswordRequest {
  @JsonProperty("email")
  @Schema(type = "string", example = "mark.javar@gmail.com")
  @NotBlank(message = "Field <email> must not be blank")
  @Pattern(regexp =  "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+"
      + "(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$",
      message = "Invalid email")
  private String email;
}

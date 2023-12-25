package com.teamchallenge.bookti.dto.authorization;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetRequest {
    @JsonProperty("email")
    @Schema(type = "string", example = "mark.javar@gmail.com")
    @NotBlank(message = "Field <email> must not be blank")
    private String email;
}

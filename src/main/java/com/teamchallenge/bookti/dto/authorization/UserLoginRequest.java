package com.teamchallenge.bookti.dto.authorization;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserLoginRequest {

    @Schema(type = "string", example = "mark.javar@gmail.com")
    @NotBlank(message = "Field <email> must not be blank")
    private String email;

    @Schema(type = "string", example = "Javard1rkk")
    @Size(min = 8, max = 20, message = "Password must be from 8 to 20 symbols length")
    @NotBlank(message = "Field <password> must not be blank")
    private String password;
}

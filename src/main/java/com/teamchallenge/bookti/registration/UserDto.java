package com.teamchallenge.bookti.registration;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class UserDto {
    @JsonProperty("first_name")
    @NotBlank(message = "Field 'first_name' must not be blank")
    @NotNull
    private String firstName;

    @JsonProperty("last_name")
    @NotBlank(message = "Field 'last_name' must not be blank")
    @NotNull
    private String lastName;

    @NotBlank(message = "Field 'email' must not be blank")
    @Pattern(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$",
            message = "Email must contain at least 1 character before '@' and domain name after '@'")
    @NotNull
    private String email;

    @NotBlank(message = "Field 'password' must not be blank")
    @Size(min = 8, max = 20, message = "Password must be from 8 to 20 symbols length")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,20}$",
            message = "Password must contain minimum eight characters, at least one uppercase letter, one lowercase letter and one number")
    @NotNull
    private String password;

    @JsonProperty("confirm_password")
    @NotBlank(message = "Field 'confirm_password' must not be blank")
    @Size(min = 8, max = 20, message = "Password must be from 8 to 20 symbols length")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,20}$",
            message = "Password must contain minimum eight characters, at least one uppercase letter, one lowercase letter and one number")
    @NotNull
    private String confirmPassword;

    public boolean confirmPassword(String password, String confirmPassword) {
        return password.equals(confirmPassword);
    }



}


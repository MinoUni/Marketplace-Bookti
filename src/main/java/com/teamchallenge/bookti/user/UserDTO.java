package com.teamchallenge.bookti.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {
    
    private Integer id;

    private String fullName;

    private String email;

    @JsonFormat(shape = STRING, pattern = "yyyy-MM-dd")
    private LocalDate creationDate;

    private String location;

    private Boolean displayEmail;

    private String telegramId;

    private Boolean displayTelegram;

    private String avatarUrl;
}

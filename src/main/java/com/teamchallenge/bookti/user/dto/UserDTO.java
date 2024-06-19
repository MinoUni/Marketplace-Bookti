package com.teamchallenge.bookti.user.dto;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
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

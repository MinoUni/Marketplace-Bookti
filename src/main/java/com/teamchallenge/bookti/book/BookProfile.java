package com.teamchallenge.bookti.book;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * DTO that represent details about book that need to save.
 *
 * @author MinoUni
 * @version 1.0
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
class BookProfile {

  @NotBlank(message = "Field <title> must be not blank")
  private String title;

  @NotBlank(message = "Field <author> must be not blank")
  private String author;

  @NotBlank(message = "Field <genre> must be not blank")
  private String genre;

  @JsonProperty("publication_date")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  @NotNull(message = "Field <publication_date> must be not null")
  private LocalDate publicationDate;

  @NotBlank(message = "Field <language> must be not blank")
  private String language;

  @JsonProperty("trade_format")
  @NotBlank(message = "Field <trade_format> must be not blank")
  private String tradeFormat;

  @JsonProperty("user_id")
  @NotNull(message = "Field <user_id> must be not null")
  private UUID userId;

  private String description;
}

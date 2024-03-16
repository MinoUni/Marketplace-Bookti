package com.teamchallenge.bookti.book;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Year;
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
  @NotNull(message = "Field <publication_date> must be not null")
  private Year publicationDate;

  @NotBlank(message = "Field <language> must be not blank")
  private String language;

  @JsonProperty("trade_format")
  @NotBlank(message = "Field <trade_format> must be not blank")
  private String tradeFormat;

  private String description;
}

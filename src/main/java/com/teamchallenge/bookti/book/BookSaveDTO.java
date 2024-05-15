package com.teamchallenge.bookti.book;

import static com.teamchallenge.bookti.constant.ValidationConstant.FIELD_NOT_BLANK_MESSAGE;
import static com.teamchallenge.bookti.constant.ValidationConstant.FIELD_NOT_NULL_MESSAGE;

import com.teamchallenge.bookti.annotation.EnumValue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.time.Year;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookSaveDTO {

  @NotBlank(message = FIELD_NOT_BLANK_MESSAGE)
  private String title;

  @NotBlank(message = FIELD_NOT_BLANK_MESSAGE)
  private String author;

  @NotBlank(message = FIELD_NOT_BLANK_MESSAGE)
  private String genre;

  @NotNull(message = FIELD_NOT_NULL_MESSAGE)
  @PastOrPresent
  private Year publicationYear;

  @NotBlank(message = FIELD_NOT_BLANK_MESSAGE)
  private String language;

  @NotBlank(message = FIELD_NOT_BLANK_MESSAGE)
  @EnumValue(enumClass = BookExchangeFormat.class)
  private String exchangeFormat;

  private String description;
}

package com.teamchallenge.bookti.book;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.teamchallenge.bookti.user.UserDTO;
import java.time.Year;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookDetailsDTO {

  private Integer id;

  private String title;

  private String author;

  private String genre;

  private Year publicationYear;

  private String language;

  private String exchangeFormat;

  private String imageUrl;

  private String description;

  private UserDTO owner;

  public BookDetailsDTO(
      Integer id,
      String title,
      String author,
      String genre,
      Year publicationYear,
      String language,
      BookExchangeFormat exchangeFormat,
      String imageUrl,
      String description) {
    this.id = id;
    this.title = title;
    this.author = author;
    this.genre = genre;
    this.publicationYear = publicationYear;
    this.language = language;
    this.exchangeFormat = exchangeFormat.getFormat();
    this.imageUrl = imageUrl;
    this.description = description;
  }
}

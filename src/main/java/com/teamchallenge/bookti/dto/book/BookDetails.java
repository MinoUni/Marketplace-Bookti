package com.teamchallenge.bookti.dto.book;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO that represents book details.
 *
 * @author MinoUni
 * @version 1.0
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class BookDetails {

  private UUID id;

  private String title;

  private String author;

  private String genre;

  @JsonProperty("publication_date")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  private LocalDate publicationDate;

  private String language;

  @JsonProperty("trade_format")
  private String tradeFormat;

  @JsonProperty("image_url")
  private String imageUrl;

  @JsonProperty("user_id")
  private UUID userId;

  private String description;
}

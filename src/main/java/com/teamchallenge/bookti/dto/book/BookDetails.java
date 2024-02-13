package com.teamchallenge.bookti.dto.book;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.teamchallenge.bookti.model.Book;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
@Builder
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

  public BookDetails(
      UUID id,
      String title,
      String author,
      String genre,
      LocalDate publicationDate,
      String language,
      String tradeFormat,
      String imageUrl,
      String description) {
    this.id = id;
    this.title = title;
    this.author = author;
    this.genre = genre;
    this.publicationDate = publicationDate;
    this.language = language;
    this.tradeFormat = tradeFormat;
    this.imageUrl = imageUrl;
    this.description = description;
  }

  /**
   * Method to map book entity into book DTO.
   *
   * @param book entity
   * @return book DTO
   */
  public static BookDetails build(Book book) {
    return BookDetails.builder()
        .id(book.getId())
        .title(book.getTitle())
        .author(book.getAuthor())
        .genre(book.getGenre())
        .publicationDate(book.getPublicationDate())
        .language(book.getLanguage())
        .tradeFormat(book.getTradeFormat())
        .imageUrl(book.getImageUrl())
        .userId(book.getOwner().getId())
        .description(book.getDescription())
        .build();
  }
}

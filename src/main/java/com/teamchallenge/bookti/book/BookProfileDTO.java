package com.teamchallenge.bookti.book;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO that contains fields required to represent book at user profile page.
 *
 * @author MinoUni
 * @version 1.0
 */
@Data
@AllArgsConstructor
public class BookProfileDTO {

  private Integer id;

  private String title;

  private String author;

  private String language;

  private String imageUrl;
}

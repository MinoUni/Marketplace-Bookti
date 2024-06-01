package com.teamchallenge.bookti.review;

import com.teamchallenge.bookti.user.dto.UserProfileDTO;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserReviewDTO {

  private Integer id;

  private String reviewerName;

  private String message;

  private BigDecimal rating;

  private LocalDate creationDate;

  private String avatarUrl;

  private UserProfileDTO owner;

  public UserReviewDTO(
      Integer id,
      String reviewerName,
      String message,
      BigDecimal rating,
      LocalDate creationDate,
      String avatarUrl) {
    this.id = id;
    this.reviewerName = reviewerName;
    this.message = message;
    this.rating = rating;
    this.creationDate = creationDate;
    this.avatarUrl = avatarUrl;
  }
}

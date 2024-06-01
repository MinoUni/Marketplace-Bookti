package com.teamchallenge.bookti.review;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.teamchallenge.bookti.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "user_review")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserReview {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "reviewer_name")
  private String reviewerName;

  @Column(columnDefinition = "TEXT")
  private String message;

  @Column(name = "rating", precision = 10, scale = 1)
  private BigDecimal rating;

  @Builder.Default
  @Column(name = "creation_date", columnDefinition = "DATE")
  private LocalDate creationDate = LocalDate.now();

  @Column(name = "avatar_url")
  private String avatarUrl;

  @ToString.Exclude
  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  @JsonIgnore
  private User owner;

  @ToString.Exclude
  @ManyToOne
  @JoinColumn(name = "reviewers_id", nullable = false)
  @JsonIgnore
  private User reviewers;
}

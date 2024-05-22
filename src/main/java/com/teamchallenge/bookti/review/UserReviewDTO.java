package com.teamchallenge.bookti.review;

import com.teamchallenge.bookti.user.dto.UserProfileDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

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

    public UserReviewDTO(Integer id,
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

    public static UserReviewDTO mapFrom(UserReview user, UserProfileDTO userProfile) {
        return UserReviewDTO.builder()
                .id(user.getId())
                .reviewerName(user.getReviewerName())
                .message(user.getMessage())
                .rating(user.getRating())
                .creationDate(user.getCreationDate())
                .avatarUrl(user.getAvatarUrl())
                .owner(userProfile)
                .build();
    }
}

package com.teamchallenge.bookti.review;

import com.teamchallenge.bookti.user.dto.UserProfileDTO;
import java.util.List;
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
public class UserReviewResponseDTO {

  private List<UserReview> reviewsList;

  private UserProfileDTO owner;
}

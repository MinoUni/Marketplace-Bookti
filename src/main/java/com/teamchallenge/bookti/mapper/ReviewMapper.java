package com.teamchallenge.bookti.mapper;

import static org.mapstruct.InjectionStrategy.CONSTRUCTOR;
import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

import com.teamchallenge.bookti.review.UserReview;
import com.teamchallenge.bookti.review.UserReviewDTO;
import com.teamchallenge.bookti.review.UserReviewResponseDTO;
import com.teamchallenge.bookti.review.UserReviewSaveDTO;
import com.teamchallenge.bookti.user.User;
import com.teamchallenge.bookti.user.dto.UserProfileDTO;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
    componentModel = "spring",
    injectionStrategy = CONSTRUCTOR,
    nullValuePropertyMappingStrategy = IGNORE)
public interface ReviewMapper {

  @Mapping(source = "user.id", target = "id")
  @Mapping(source = "user.reviewerName", target = "reviewerName")
  @Mapping(source = "user.message", target = "message")
  @Mapping(source = "user.rating", target = "rating")
  @Mapping(source = "user.creationDate", target = "creationDate")
  @Mapping(source = "user.avatarUrl", target = "avatarUrl")
  @Mapping(source = "userProfile", target = "owner")
  UserReviewDTO toUserReviewDTO(UserReview user, UserProfileDTO userProfile);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "creationDate", ignore = true)
  @Mapping(source = "userReviewer.fullName", target = "reviewerName")
  @Mapping(source = "userReview.message", target = "message")
  @Mapping(
      target = "rating",
      expression = "java(userReview.getRating().setScale(1, java.math.RoundingMode.HALF_UP))")
  @Mapping(source = "userReviewer.avatarUrl", target = "avatarUrl")
  @Mapping(source = "userOwner", target = "owner")
  @Mapping(source = "userReviewer", target = "reviewers")
  UserReview toUserReview(UserReviewSaveDTO userReview, User userReviewer, User userOwner);

  @Mapping(source = "userReviewList", target = "reviewsList")
  @Mapping(source = "userProfileDTO", target = "owner")
  UserReviewResponseDTO toUserReviewResponseDTO(
      List<UserReview> userReviewList, UserProfileDTO userProfileDTO);
}

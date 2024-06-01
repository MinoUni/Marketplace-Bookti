package com.teamchallenge.bookti.review;

import java.util.List;

public interface UserReviewService {

  List<UserReview> findAllUserReceivedReviewsById(Integer userId);

  List<UserReview> findAllUserLeftReviewById(Integer userId);

  UserReviewResponseDTO save(UserReviewSaveDTO userReview, Integer reviewerId);

  Integer getUserIdFromAccessToken(String accessToken);

  String deleteById(Integer reviewId);
}

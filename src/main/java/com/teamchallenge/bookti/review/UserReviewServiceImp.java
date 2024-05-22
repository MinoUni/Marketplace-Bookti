package com.teamchallenge.bookti.review;

import com.teamchallenge.bookti.exception.user.UserNotFoundException;
import com.teamchallenge.bookti.user.User;
import com.teamchallenge.bookti.user.UserRepository;
import com.teamchallenge.bookti.user.dto.UserProfileDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

import static java.text.MessageFormat.format;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserReviewServiceImp implements UserReviewService {

    private final UserRepository userRepository;
    private final UserReviewRepository userReviewRepository;
    private final JwtDecoder jwtDecoder;

    @Override
    public List<UserReview> findAllUserReceivedReviewsById(Integer userId) {
        if (userId > 0) {
            List<UserReview> userReviewList = userReviewRepository.findAllUserReceivedReviewsById(userId);
            log.info("From UserReviewServiceImp method - findAllUserReviewById - return list or empty list, received review to user: {}.", userId);

            return userReviewList;
        } else {
            throw new UserNotFoundException("Write correct Users id.");
        }
    }

    @Override
    public List<UserReview> findAllUserLeftReviewById(Integer userId) {
        if (userId > 0) {
            List<UserReview> userReviewList = userReviewRepository.findAllUserLeftReviewById(userId);
            log.info("From UserReviewServiceImp method - findAllUserReviewById - return list or empty list, Left reviews to user: {}.", userId);

            return userReviewList;
        } else {
            throw new UserNotFoundException("Write correct Users id.");
        }
    }

    @Transactional
    @Override
    public UserReviewResponseDTO save(UserReviewSaveDTO review, Integer reviewerId) {

        if (checkValidUserId(reviewerId, review.getOwnerId())) {
            User userReviewer = userRepository.findById(reviewerId)
                    .orElseThrow(
                            () -> new UserNotFoundException(format("Reviewer's User with id <{0}> not found.", reviewerId)));

            User userOwner = userRepository.findById(review.getOwnerId())
                    .orElseThrow(
                            () -> new UserNotFoundException(format("Owner's User with id <{0}> not found.", review.getOwnerId())));

            UserReview newReview = createUserReview(review, userReviewer, userOwner);

            userReviewRepository.save(newReview);

            User userWithNewRating = addUserRating(userOwner);

            List<UserReview> userReviewList = userReviewRepository.findAllUserReceivedReviewsById(userOwner.getId());

            UserProfileDTO userProfileDTO = UserProfileDTO.mapFrom(userWithNewRating);

            UserReviewResponseDTO userReviewResponseDTO = createUserReviewResponseDTO(userReviewList, userProfileDTO);
            log.info("From UserReviewServiceImp method - save - Creat new User Review to user: {}.", userWithNewRating.getId());

            return userReviewResponseDTO;
        } else {
            throw new UserNotFoundException("Write correct Users id.");
        }
    }

    @Transactional
    @Override
    public String delete(Integer reviewId) {
        UserReview userReviewer = userReviewRepository.findById(reviewId)
                .orElseThrow(
                        () -> new UserNotFoundException(format("Reviewer's User with id <{0}> not found.", reviewId)));

        userReviewRepository.delete(userReviewer);
        log.info("From UserReviewServiceImp method - delete - deleted User Review from review id: {}.", userReviewer.getId());
        return "Review was deleted successfully";
    }

    private static UserReviewResponseDTO createUserReviewResponseDTO(List<UserReview> UserReviewList, UserProfileDTO userProfileDTO) {
        return UserReviewResponseDTO.builder()
                .reviewsList(UserReviewList)
                .owner(userProfileDTO)
                .build();
    }

    private static UserReview createUserReview(UserReviewSaveDTO userReview, User userReviewer, User userOwner) {
        return UserReview.builder()
                .reviewerName(userReviewer.getFullName())
                .avatarUrl(userReviewer.getAvatarUrl())
                .message(userReview.getMessage())
                .rating(userReview.getRating().setScale(1, RoundingMode.HALF_UP))
                .owner(userOwner)
                .reviewers(userReviewer)
                .build();
    }

    public User addUserRating(User userOwner) {
        BigDecimal userRating = userReviewRepository.getUserRating(userOwner.getId()).orElse(BigDecimal.valueOf(0.0));
        userOwner.setRating(userRating.setScale(1, RoundingMode.HALF_UP));
        User userWithNewRating = userRepository.save(userOwner);
        log.info("From UserReviewServiceImp method - addUserRating - Add new user rating: {}; userId: {}.", userRating, userOwner.getId());

        return userWithNewRating;
    }

    @Override
    public Integer getUserIdFromAccessToken(String accessToken) {
        Jwt jwt = jwtDecoder.decode(accessToken.replace("Bearer ", ""));
        String userId = jwt.getClaim("sub");

        return Integer.parseInt(userId);
    }

    public boolean checkValidUserId(Integer reviewerId, Integer ownerId) {

        return reviewerId > 0 && !Objects.equals(reviewerId, ownerId);
    }

}

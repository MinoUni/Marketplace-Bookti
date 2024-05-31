package com.teamchallenge.bookti.review;

import com.teamchallenge.bookti.constant.ReviewConstant;
import com.teamchallenge.bookti.constant.UserConstant;
import com.teamchallenge.bookti.exception.user.UserNotFoundException;
import com.teamchallenge.bookti.mapper.ReviewMapper;
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


@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserReviewServiceImp implements UserReviewService {

    private final UserRepository userRepository;
    private final UserReviewRepository userReviewRepository;
    private final ReviewMapper reviewMapper;
    private final JwtDecoder jwtDecoder;

    

    @Override
    public List<UserReview> findAllUserReceivedReviewsById(Integer userId) {
        if (!userRepository.existsById(userId)) {
            log.info("Throw UserNotFoundException with NOT_FOUND_MESSAGE. userId: {}", userId);
            throw new UserNotFoundException(String.format(UserConstant.NOT_FOUND_MESSAGE, userId));
        }
        List<UserReview> userReviewList = userReviewRepository.findAllUserReceivedReviewsById(userId);
        log.info("From UserReviewServiceImp method - findAllUserReviewById - return list or empty list, received review to user: {}.", userId);

        return userReviewList;
    }

    @Override
    public List<UserReview> findAllUserLeftReviewById(Integer userId) {
        if (!userRepository.existsById(userId)) {
            log.info("Throw UserNotFoundException with NOT_FOUND_MESSAGE. userId: {}", userId);
            throw new UserNotFoundException(String.format(UserConstant.NOT_FOUND_MESSAGE, userId));
        }
        List<UserReview> userReviewList = userReviewRepository.findAllUserLeftReviewById(userId);
        log.info("From UserReviewServiceImp method - findAllUserReviewById - return list or empty list, received review to user: {}.", userId);

        return userReviewList;
    }

    @Transactional
    @Override
    public UserReviewResponseDTO save(UserReviewSaveDTO review, Integer reviewerId) {
        if (Objects.equals(reviewerId, review.getOwnerId())) {
            throw new UserNotFoundException("User with same id can't left review.");
        }
        User userReviewer = userRepository.findById(reviewerId)
                .orElseThrow(
                        () -> new UserNotFoundException(String.format("Reviewer's User with id <{%d}> not found.", reviewerId)));

        User userOwner = userRepository.findById(review.getOwnerId())
                .orElseThrow(
                        () -> new UserNotFoundException(String.format("Owner's User with id <{%d}> not found.", review.getOwnerId())));

        UserReview newReview = reviewMapper.toUserReview(review, userReviewer, userOwner);


        userReviewRepository.save(newReview);

        User userWithNewRating = addUserRating(userOwner);

        List<UserReview> userReviewList = userReviewRepository.findAllUserReceivedReviewsById(userOwner.getId());

        UserProfileDTO userProfileDTO = UserProfileDTO.mapFrom(userWithNewRating);

        UserReviewResponseDTO userReviewResponseDTO = reviewMapper.toUserReviewResponseDTO(userReviewList, userProfileDTO);
        log.info("From UserReviewServiceImp method - save - Creat new User Review to user: {}.", userWithNewRating.getId());

        return userReviewResponseDTO;
    }

    @Transactional
    @Override
    public String deleteById(Integer reviewId) {
        if (!userReviewRepository.existsById(reviewId)) {
            log.info("Throw UserNotFoundException with NOT_FOUND_MESSAGE. reviewId: {}", reviewId);
            throw new UserNotFoundException(String.format(ReviewConstant.NOT_FOUND_MESSAGE, reviewId));
        }
        userReviewRepository.deleteById(reviewId);
        log.info("From UserReviewServiceImp method - delete - deleted User Review from review id: {}.", reviewId);

        return "Review was deleted successfully";
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
}

package com.teamchallenge.bookti.review;

import com.teamchallenge.bookti.exception.user.UserNotFoundException;
import com.teamchallenge.bookti.user.User;
import com.teamchallenge.bookti.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static java.text.MessageFormat.format;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserReviewServiceImp implements UserReviewService {

    private final UserRepository userRepository;
    private final UserReviewRepository userReviewRepository;

    @Transactional
    @Override
    public UserReview save(UserReviewSaveDTO userReview) {

        boolean idNotNull = userReview.getReviewerId() != null && userReview.getOwnerId() != null;
        boolean idMoreZero = userReview.getReviewerId() > 0 && userReview.getOwnerId() > 0;

        if (idNotNull && idMoreZero) {
            User userReviewer = userRepository.findById(userReview.getReviewerId())
                    .orElseThrow(
                            () -> new UserNotFoundException(format("Reviewer's User with id <{0}> not found.", userReview.getReviewerId())));

            User userOwner = userRepository.findById(userReview.getOwnerId())
                    .orElseThrow(
                            () -> new UserNotFoundException(format("Owner's User with id <{0}> not found.", userReview.getOwnerId())));

            UserReview newReview = UserReview.builder()
                    .reviewerName(userReviewer.getFullName())
                    .avatarUrl(userReviewer.getAvatarUrl())
                    .message(userReview.getMessage())
                    .rating(userReview.getRating())
                    .creationDate(LocalDate.now())
                    .owner(userOwner)
                    .build();

            UserReview saveReview = userReviewRepository.save(newReview);

            addUserRating(userOwner);

            return saveReview;
        } else {
            throw new UserNotFoundException(format("Write correct Users id."));
        }
    }

    public void addUserRating(User userOwner) {

        Float userRating = userReviewRepository.getUserRating(userOwner.getId()).orElse(0F);

        userOwner.setRating(userRating);

        userRepository.save(userOwner);
        logger.info("Add new user rating: {}; userId: {}.", userRating, userOwner.getId());
    }


}

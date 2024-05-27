package com.teamchallenge.bookti.review;

import com.teamchallenge.bookti.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


@DataJpaTest
class UserReviewRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserReviewRepository userReviewRepository;

    private UserReview userReview;
    private UserReview secondUserReview;
    private User user;
    private User reviewers;

    @BeforeEach
    void createUserOne() {
        user = User
                .builder()
                .fullName("First_name")
                .email("abc@gmail.com")
                .rating(BigDecimal.valueOf(3.5D))
                .password("Password1")
                .location("city")
                .build();

        reviewers = User
                .builder()
                .fullName("First_name")
                .email("abcdd@gmail.com")
                .rating(BigDecimal.valueOf(3.2D))
                .password("Password1")
                .location("city")
                .build();

        entityManager.persist(user);
        entityManager.persist(reviewers);

        userReview = UserReview.builder()
                .reviewerName("Ronald Serous")
                .message("Helpful, knowledgeable, always contributes.")
                .rating(BigDecimal.valueOf(4))
                .avatarUrl("https://cdn.iconscout.com/icon/free-avatar-3.png")
                .creationDate(LocalDate.now())
                .owner(user)
                .reviewers(reviewers)
                .build();

        secondUserReview = UserReview.builder()
                .reviewerName("Ted Carel")
                .message("Helpful, knowledgeable, always contributes.")
                .rating(BigDecimal.valueOf(3))
                .avatarUrl("https://cdn.iconscout.com/icon/free-avatar-3.png")
                .creationDate(LocalDate.now())
                .owner(user)
                .reviewers(reviewers)
                .build();
    }

    @Test
    @DisplayName("Test UserReviewRepository method getUserRating")
    void testMethodGetUserRating() {
        entityManager.persist(userReview);
        entityManager.persist(secondUserReview);
        BigDecimal avrRating = BigDecimal.valueOf(3.5D);

        Optional<BigDecimal> userRating = userReviewRepository.getUserRating(user.getId());

        assertTrue(userRating.isPresent());
        assertEquals(avrRating, userRating.get());
    }

    @Test
    @DisplayName("Test UserReviewRepository method findAllUserReviewById")
    void testMethodFindAllUserReviewById() {
        entityManager.persist(userReview);
        entityManager.persist(secondUserReview);

        List<UserReview> listUserReview = userReviewRepository.findAllUserReceivedReviewsById(user.getId());

        assertAll(
                () -> assertFalse(listUserReview.isEmpty()),
                () -> assertThat(listUserReview.size()).isEqualTo(2),
                () -> assertTrue(listUserReview.contains(userReview)));
    }

    @Test
    @DisplayName("Test UserReviewRepository method save")
    void testMethodSave() {
        userReviewRepository.save(userReview);

        UserReview newUserReview = entityManager.find(UserReview.class, userReview.getId());

        assertAll(
                () -> assertEquals(userReview.getId(), newUserReview.getId()),
                () -> assertEquals(userReview.getMessage(), newUserReview.getMessage()));
    }
}
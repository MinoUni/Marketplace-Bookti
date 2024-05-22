package com.teamchallenge.bookti.review;

import com.teamchallenge.bookti.exception.user.UserNotFoundException;
import com.teamchallenge.bookti.user.User;
import com.teamchallenge.bookti.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
@SpringBootTest
class UserReviewServiceImpTest {

    @MockBean
    private UserReviewRepository userReviewRepository;
    @MockBean
    private JwtDecoder jwtDecoder;
    @MockBean
    private UserRepository userRepository;
    @Autowired
    private UserReviewServiceImp userReviewService;


    private UserReview userReview;
    private UserReview secondUserReview;
    private User userRobert;
    private User userDenial;


    @BeforeEach
    void createUserOne() {
        userRobert = User
                .builder()
                .id(1)
                .fullName("Robert Ter")
                .email("abc@gmail.com")
                .rating(BigDecimal.valueOf(4D))
                .password("Password1")
                .location("city")
                .build();

        userDenial = User
                .builder()
                .id(2)
                .fullName("Denial Far")
                .email("abc@gmail.com")
                .rating(BigDecimal.valueOf(3.5D))
                .password("Password1")
                .location("city")
                .build();

        userReview = UserReview.builder()
                .id(1)
                .reviewerName("Denial Far")
                .message("Helpful, knowledgeable, always contributes.")
                .rating(BigDecimal.valueOf(4))
                .avatarUrl("https://cdn.iconscout.com/icon/free-avatar-3.png")
                .creationDate(LocalDate.now())
                .owner(userRobert)
                .reviewers(userDenial)
                .build();

        secondUserReview = UserReview.builder()
                .id(2)
                .reviewerName("Denial Far")
                .message("Helpful, knowledgeable, always contributes.")
                .rating(BigDecimal.valueOf(3))
                .avatarUrl("https://cdn.iconscout.com/icon/free-avatar-3.png")
                .creationDate(LocalDate.now())
                .owner(userRobert)
                .reviewers(userDenial)
                .build();
    }

    @Test
    @DisplayName("Test UserReviewServiceImp method findAllUserReviewById.")
    void testMethodFindAllUserReviewById() {

        List<UserReview> reviewsEmptyList = userReviewService.findAllUserLeftReviewById(userRobert.getId());

        assertTrue(reviewsEmptyList.isEmpty());

        List<UserReview> reviewList = List.of(userReview, secondUserReview);

        when(userReviewRepository.findAllUserReceivedReviewsById(any())).thenReturn(reviewList);

        List<UserReview> reviewsList = userReviewService.findAllUserReceivedReviewsById(userRobert.getId());

        assertAll(
                () -> assertFalse(reviewsList.isEmpty()),
                () -> assertEquals(2, reviewsList.size()),
                () -> assertTrue(reviewsList.contains(userReview))
        );

        verify(userReviewRepository, times(1)).findAllUserReceivedReviewsById(any());
    }

    @Test
    @DisplayName("Test UserReviewServiceImp exception in method findAllUserReviewById.")
    void testExceptionInMethodFindAllUserReviewById() {
        Integer notCorrectId = -1;

        UserNotFoundException errorIfOwnerIdIsEqualReviewerId = assertThrows(UserNotFoundException.class,
                () -> userReviewService.findAllUserReceivedReviewsById(notCorrectId));

        assertEquals("Write correct Users id.", errorIfOwnerIdIsEqualReviewerId.getMessage());

        verify(userReviewRepository, times(0)).findAllUserReceivedReviewsById(any());
    }

    @Test
    @DisplayName("Test UserReviewServiceImp method save.")
    void testMethodSave() {

        UserReviewSaveDTO userReviewSaveDTO = UserReviewSaveDTO.builder()
                .message(userReview.getMessage())
                .rating(userReview.getRating())
                .ownerId(userReview.getOwner().getId())
                .build();
        List<UserReview> reviewList = List.of(userReview, secondUserReview);

        when(userReviewRepository.save(any())).thenReturn(userReview);
        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(userRobert));
        when(userRepository.save(any())).thenReturn(userRobert);
        when(userReviewRepository.findAllUserReceivedReviewsById(any())).thenReturn(reviewList);

        UserReviewResponseDTO responseDTO = userReviewService.save(userReviewSaveDTO, userDenial.getId());

        assertAll(
                () -> assertNotNull(responseDTO),
                () -> assertEquals(2, responseDTO.getReviewsList().size()),
                () -> assertTrue(responseDTO.getReviewsList().contains(userReview)),
                () -> assertEquals(userRobert.getId(), responseDTO.getOwner().getId())
        );

        verify(userRepository, times(2)).findById(any());
        verify(userRepository, times(1)).save(any());
        verify(userReviewRepository, times(1)).save(any());
        verify(userReviewRepository, times(1)).findAllUserReceivedReviewsById(any());
    }

    @Test
    @DisplayName("Test UserReviewServiceImp exception in method save.")
    void testExceptionInMethodSave() {
        Integer ownerId = userRobert.getId();

        UserReviewSaveDTO userReviewSaveDTO = UserReviewSaveDTO.builder()
                .message(userReview.getMessage())
                .rating(userReview.getRating())
                .ownerId(ownerId)
                .build();

        UserNotFoundException errorIfOwnerIdIsEqualReviewerId = assertThrows(UserNotFoundException.class,
                () -> userReviewService.save(userReviewSaveDTO, ownerId));

        assertEquals("Write correct Users id.", errorIfOwnerIdIsEqualReviewerId.getMessage());

        UserNotFoundException errorIfReviewerIdIsNegative = assertThrows(UserNotFoundException.class,
                () -> userReviewService.save(userReviewSaveDTO, -1));

        assertEquals("Write correct Users id.", errorIfReviewerIdIsNegative.getMessage());

        verify(userRepository, times(0)).findById(any());
        verify(userReviewRepository, times(0)).findAllUserReceivedReviewsById(any());
    }


    @Test
    @DisplayName("Test UserReviewServiceImp method addUserRating.")
    void testMethodAddUserRating() {
        BigDecimal testRating = BigDecimal.valueOf(3.5);
        Integer userId = userRobert.getId();

        when(userReviewRepository.getUserRating(userId)).thenReturn(Optional.of(testRating));

        when(userRepository.save(any())).thenReturn(userRobert);

        User userWithNewRating = userReviewService.addUserRating(userRobert);

        assertNotNull(userWithNewRating);
        assertEquals(testRating, userRobert.getRating());

        verify(userReviewRepository, times(1)).getUserRating(userId);
        verify(userRepository, times(1)).save(userRobert);
    }

    @Test
    @DisplayName("Test UserReviewServiceImp method getUserIdFromAccessToken.")
    void testMethodGetUserIdFromAccessToken() {
        String userId = "5";
        Map<String, Object> headers = new HashMap<>();
        headers.put("alg", "HS256");
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", userId);

        Jwt jwt = new Jwt("token",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                headers,
                claims);
        when(jwtDecoder.decode(Mockito.anyString())).thenReturn(jwt);

        String accessToken = "Bearer eyJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJCb29r" +
                "E5DfYaLtRIND1SIYtvR1y1bfVjvqxS_hCJuFgE1MRiuqF1SN1vDftrkLUUI7YUER_iqqb-aQ";

        Integer result = userReviewService.getUserIdFromAccessToken(accessToken);

        assertEquals(Integer.valueOf(userId), result);

    }
}
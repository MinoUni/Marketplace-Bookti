package com.teamchallenge.bookti.review;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamchallenge.bookti.security.AuthorizedUser;
import com.teamchallenge.bookti.user.User;
import com.teamchallenge.bookti.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper jsonMapper;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private JwtDecoder jwtDecoder;

    @MockBean
    private UserReviewRepository reviewService;

    private UserReview userReview;
    private UserReview secondUserReview;
    private User userRobert;
    private User userDenial;
    private AuthorizedUser authorizedUser;

    private final String accessToken = "Bearer eyJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJCb29r" +
            "E5DfYaLtRIND1SIYtvR1y1bfVjvqxS_hCJuFgE1MRiuqF1SN1vDftrkLUUI7YUER_iqqb-aQ";

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
                .owner(userDenial)
                .reviewers(userDenial)
                .build();

        authorizedUser = new AuthorizedUser("testUser", "password", List.of(new SimpleGrantedAuthority("ROLE_USER")));

    }

    @Test
    @Tag("UserReviews")
    @DisplayName("When calling get /reviews/user/{userId}, expect that request is valid," +
            " then response with List of all User Reviews and status code 200")
    void testMethodFindAllUserReviewById() throws Exception {
        Integer userId = userDenial.getId();
        List<UserReview> reviewList = List.of(userReview, secondUserReview);

        when(reviewService.findAllUserReceivedReviewsById(userId)).thenReturn(reviewList);

        mockMvc.perform(get("/reviews/user/{userId}/received", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].id",
                        containsInAnyOrder(userReview.getId(), secondUserReview.getId())));

        verify(reviewService, times(1)).findAllUserReceivedReviewsById(userId);
    }

    @Test
    @Tag("UserReviews")
    @DisplayName("When calling post/reviews/user, expect that request is valid and we add new review," +
            " then response with List of all User Reviews and status code 201")
    @WithMockUser(username = "user", roles = {"USER"})
    void testMethodSave() throws Exception {
        Integer reviewerId = userDenial.getId();
        Integer ownerId = userRobert.getId();
        BigDecimal ownerRating = BigDecimal.valueOf(3.5D);

        UserReviewSaveDTO userReviewSaveDTO = UserReviewSaveDTO.builder()
                .message(userReview.getMessage())
                .rating(userReview.getRating())
                .ownerId(ownerId)
                .build();
        List<UserReview> reviewList = List.of(userReview, secondUserReview);

        Map<String, Object> headers = new HashMap<>();
        headers.put("alg", "HS256");
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", Integer.toString(reviewerId));

        Jwt jwt = new Jwt("token",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                headers,
                claims);


        authorizedUser.setId(reviewerId);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(authorizedUser, "password", authorizedUser.getAuthorities())
        );

        when(jwtDecoder.decode(Mockito.anyString())).thenReturn(jwt);
        when(reviewService.save(any())).thenReturn(userReview);
        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(userRobert));
        when(reviewService.getUserRating(ownerId)).thenReturn(Optional.of(ownerRating));
        when(userRepository.save(any())).thenReturn(userRobert);
        when(reviewService.findAllUserReceivedReviewsById(ownerId)).thenReturn(reviewList);

        mockMvc.perform(post("/reviews/user")
                        .header(HttpHeaders.AUTHORIZATION, accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(userReviewSaveDTO))
                        .with(csrf())
                        .principal(() -> "authorizedUser"))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.reviewsList", hasSize(2)))
                .andExpect(jsonPath("$.reviewsList[*].id",
                        containsInAnyOrder(userReview.getId(), secondUserReview.getId())))
                .andExpect(jsonPath("$.owner.id").value(ownerId))
                .andExpect(jsonPath("$.owner.rating").value(ownerRating));

        verify(reviewService, times(1)).findAllUserReceivedReviewsById(ownerId);
        verify(reviewService, times(1)).save(any(UserReview.class));
    }

    @Test
    @Tag("UserReviews")
    @DisplayName("When calling post/reviews/user, expect that request  are invalid (reviewerId and ownerId are not correct)" +
            "then response with ErrorResponse.class and status code 401")
    @WithMockUser(username = "user", roles = {"USER"})
    void testExceptionInMethodSave() throws Exception {
        Integer negativeReviewerId = -1;
        Integer ownerId = userRobert.getId();

        UserReviewSaveDTO userReviewSaveDTO = UserReviewSaveDTO.builder()
                .message(userReview.getMessage())
                .rating(userReview.getRating())
                .ownerId(ownerId)
                .build();

        Map<String, Object> headers = new HashMap<>();
        headers.put("alg", "HS256");
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", Integer.toString(ownerId));

        Jwt jwtWithOwnerId = new Jwt("token",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                headers,
                claims);

        authorizedUser.setId(negativeReviewerId);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(authorizedUser, "password", authorizedUser.getAuthorities())
        );

        when(jwtDecoder.decode(Mockito.anyString())).thenReturn(jwtWithOwnerId);

        mockMvc.perform(post("/reviews/user")
                        .header(HttpHeaders.AUTHORIZATION, accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(userReviewSaveDTO)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Write correct Users id."))
                .andExpect(jsonPath("status_code").value(HttpStatus.NOT_FOUND.value()));

        claims.put("sub", Integer.toString(negativeReviewerId));
        Jwt jwtWithNegativeReviewerId = new Jwt("token",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                headers,
                claims);
        when(jwtDecoder.decode(Mockito.anyString())).thenReturn(jwtWithNegativeReviewerId);

        mockMvc.perform(post("/reviews/user")
                        .header(HttpHeaders.AUTHORIZATION, accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(userReviewSaveDTO)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Write correct Users id."))
                .andExpect(jsonPath("status_code").value(HttpStatus.NOT_FOUND.value()));

        verify(reviewService, times(0)).findAllUserReceivedReviewsById(ownerId);
        verify(reviewService, times(0)).save(any(UserReview.class));
    }

    @Test
    @Tag("UserReviews")
    @DisplayName("When calling post/reviews/user, expect that request  are invalid (filed are not valid" +
            "then response with ErrorResponse.class and status code 401")
    @WithMockUser(username = "user", roles = {"USER"})
    void testValidationExceptionInMethodSave() throws Exception {
        Integer reviewerId = userDenial.getId();
        Integer ownerId = userRobert.getId();
        Integer negativeOwnerId = 0;
        BigDecimal ZeroOwnerRating = BigDecimal.valueOf(0);
        UserReviewSaveDTO userReviewSaveDTO = UserReviewSaveDTO.builder()
                .message(userReview.getMessage())
                .rating(ZeroOwnerRating)
                .ownerId(negativeOwnerId)
                .build();

        Map<String, Object> headers = new HashMap<>();
        headers.put("alg", "HS256");
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", Integer.toString(reviewerId));

        Jwt jwtWithOwnerId = new Jwt("token",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                headers,
                claims);

        when(jwtDecoder.decode(Mockito.anyString())).thenReturn(jwtWithOwnerId);

        mockMvc.perform(post("/reviews/user")
                        .header(HttpHeaders.AUTHORIZATION, accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(userReviewSaveDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.details[*]", containsInAnyOrder(
                        "Min <rating> value mast be 1",
                        "Min <ownerId> value mast be 1")))
                .andExpect(jsonPath("status_code").value(HttpStatus.BAD_REQUEST.value()));

        userReviewSaveDTO.setRating(BigDecimal.valueOf(6));
        userReviewSaveDTO.setOwnerId(ownerId);

        mockMvc.perform(post("/reviews/user")
                        .header(HttpHeaders.AUTHORIZATION, accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(userReviewSaveDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.details[*]", containsInAnyOrder(
                        "Max <rating> value mast be 5")))
                .andExpect(jsonPath("status_code").value(HttpStatus.BAD_REQUEST.value()));

        UserReviewSaveDTO EmptyUserReviewSaveDTO = new UserReviewSaveDTO();


        mockMvc.perform(post("/reviews/user")
                        .header(HttpHeaders.AUTHORIZATION, accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(EmptyUserReviewSaveDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.details[*]", containsInAnyOrder("Field <rating> must not be null",
                        "Field <message> must not be blank",
                        "Field <ownerId> must not be null")))
                .andExpect(jsonPath("status_code").value(HttpStatus.BAD_REQUEST.value()));

        verify(reviewService, times(0)).findAllUserReceivedReviewsById(ownerId);
        verify(reviewService, times(0)).save(any(UserReview.class));
    }

}
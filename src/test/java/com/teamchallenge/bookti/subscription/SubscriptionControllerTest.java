package com.teamchallenge.bookti.subscription;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.teamchallenge.bookti.constant.UserConstant;
import com.teamchallenge.bookti.security.AuthorizedUser;
import com.teamchallenge.bookti.user.User;
import com.teamchallenge.bookti.user.UserRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
class SubscriptionControllerTest {

  @Autowired
  private MockMvc mockMvc;
  @MockBean
  private UserRepository userRepository;
  @MockBean
  private SubscriptionRepository subscriptionRepository;

  private User user;
  private User subscriber;
  private Subscription subscription;
  private AuthorizedUser authorizedUser;

  private final String accessToken =
      "Bearer eyJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJCb29r"
          + "E5DfYaLtRIND1SIYtvR1y1bfVjvqxS_hCJuFgE1MRiuqF1SN1vDftrkLUUI7YUER_iqqb-aQ";

  @BeforeEach
  void createObjects() {
    user =
        User.builder()
            .id(1)
            .fullName("Saria Darkoff")
            .email("abc@gmail.com")
            .rating(BigDecimal.valueOf(3.5D))
            .password("Password1")
            .location("city")
            .build();

    subscriber =
        User.builder()
            .id(2)
            .fullName("Peter Berger")
            .email("abcdd@gmail.com")
            .rating(BigDecimal.valueOf(3.2D))
            .password("Password1")
            .location("city")
            .build();

    subscription =
        Subscription.builder()
            .id(1)
            .user(user)
            .subscriber(subscriber)
            .status(SubscriptionStatus.SUBSCRIBED)
            .build();

    authorizedUser =
        new AuthorizedUser(
            "testUser", "password", List.of(new SimpleGrantedAuthority("ROLE_USER")));
  }

  @Test
  @DisplayName(
      "When calling get /subscription, expect that request is valid,"
          + " then response with List of all User subscriptions and status code 200")
  @WithMockUser(
      username = "user",
      roles = {"USER"})
  void testMethodFindAllUserSubscriptionById() throws Exception {
    User secondSubscriber =
        User.builder()
            .id(3)
            .fullName("Amiya Shiro")
            .email("abcdda@gmail.com")
            .rating(BigDecimal.valueOf(3.2D))
            .password("Password1")
            .location("city")
            .build();

    Subscription secondSubscription =
        Subscription.builder()
            .id(2)
            .user(user)
            .subscriber(secondSubscriber)
            .status(SubscriptionStatus.SUBSCRIBED)
            .build();

    Integer userId = user.getId();
    List<Subscription> subscriptionList = List.of(subscription, secondSubscription);

    when(userRepository.existsById(userId)).thenReturn(Boolean.TRUE);
    when(subscriptionRepository.findAllUserSubscriptionById(userId)).thenReturn(subscriptionList);

    mockMvc
        .perform(
            get("/subscriptions")
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .param("userId", userId.toString()))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].fullName").value(secondSubscriber.getFullName()))
        .andExpect(jsonPath("$[1].fullName").value(subscriber.getFullName()));

    verify(subscriptionRepository, times(1)).findAllUserSubscriptionById(userId);
    verify(userRepository, times(1)).existsById(any());
  }

  @Test
  @DisplayName(
      "When calling get /subscription, expect that request  are invalid (userId are not correct)"
          + "then response with ErrorResponse.class and status code 404")
  @WithMockUser(
      username = "user",
      roles = {"USER"})
  void testMethodFindAllUserReceivedReviewsById() throws Exception {
    Integer userId = user.getId() + 100;

    when(userRepository.existsById(userId)).thenReturn(Boolean.FALSE);

    mockMvc
        .perform(
            get("/subscriptions")
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .param("userId", userId.toString()))
        .andExpect(status().isNotFound())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(
            jsonPath("$.message").value(String.format(UserConstant.NOT_FOUND_MESSAGE, userId)));

    verify(subscriptionRepository, times(0)).findAllUserSubscriptionById(userId);
    verify(userRepository, times(1)).existsById(any());
  }

  @Test
  @DisplayName(
      "When calling post /subscription, expect that request is valid and we add new subscription,"
          + "then response with successfully message and status code 201")
  @WithMockUser(
      username = "user",
      roles = {"USER"})
  void testMethodSave() throws Exception {
    Integer userId = user.getId();
    Integer subscriberId = subscriber.getId();
    authorizedUser.setId(userId);
    SecurityContextHolder.getContext()
        .setAuthentication(
            new UsernamePasswordAuthenticationToken(
                authorizedUser, "password", authorizedUser.getAuthorities()));

    when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(user));
    when(userRepository.findById(subscriberId)).thenReturn(Optional.ofNullable(subscriber));
    when(subscriptionRepository.checkIfUserIsSubscribed(userId, subscriberId))
        .thenReturn(Optional.empty());
    when(subscriptionRepository.save(any())).thenReturn(subscription);

    mockMvc
        .perform(
            post("/subscriptions")
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .param("subscriberId", subscriberId.toString()))
        .andExpect(status().isCreated())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.message").value("Your subscription was successfully"));

    verify(subscriptionRepository, times(1)).save(any());
    verify(userRepository, times(2)).findById(any());
  }

  @Test
  @DisplayName(
      "When calling post /subscription, expect that request  are invalid (userId and subscriberId are not correct)"
          + "then response with ErrorResponse.class and status code 404")
  @WithMockUser(
      username = "user",
      roles = {"USER"})
  void testExceptionInMethodSave() throws Exception {
    Integer userId = user.getId();
    Integer subscriberId = subscriber.getId();
    Integer notExistSubscriberId = subscriberId + 100;
    Integer notExistUserId = userId + 200;

    authorizedUser.setId(notExistUserId);
    SecurityContextHolder.getContext()
        .setAuthentication(
            new UsernamePasswordAuthenticationToken(
                authorizedUser, "password", authorizedUser.getAuthorities()));

    mockMvc
        .perform(
            post("/subscriptions")
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .param("subscriberId", subscriberId.toString()))
        .andExpect(status().isNotFound())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(
            jsonPath("$.message")
                .value(String.format(UserConstant.NOT_FOUND_MESSAGE, notExistUserId)))
        .andExpect(jsonPath("status_code").value(HttpStatus.NOT_FOUND.value()));

    authorizedUser.setId(userId);
    when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(user));

    mockMvc
        .perform(
            post("/subscriptions")
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .param("subscriberId", notExistSubscriberId.toString()))
        .andExpect(status().isNotFound())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(
            jsonPath("$.message")
                .value(String.format("Subscriber with id <{%d}> not found.", notExistSubscriberId)))
        .andExpect(jsonPath("status_code").value(HttpStatus.NOT_FOUND.value()));

    verify(subscriptionRepository, times(0)).save(any());
    verify(userRepository, times(3)).findById(any());
  }

  @Test
  @DisplayName(
      "When calling post /subscription, expect that request  are invalid"
          + " (user is are already subscribed or he try subscribe to itself)"
          + "then response with ErrorResponse.class and status code 400")
  @WithMockUser(
      username = "user",
      roles = {"USER"})
  void testExceptionCheckSubscribedInMethodSave() throws Exception {
    Integer userId = user.getId();
    Integer subscriberId = subscriber.getId();
    authorizedUser.setId(userId);
    SecurityContextHolder.getContext()
        .setAuthentication(
            new UsernamePasswordAuthenticationToken(
                authorizedUser, "password", authorizedUser.getAuthorities()));

    when(userRepository.findById(subscriberId)).thenReturn(Optional.ofNullable(subscriber));
    when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(user));
    when(subscriptionRepository.checkIfUserIsSubscribed(userId, subscriberId))
        .thenReturn(Optional.ofNullable(subscription));

    mockMvc
        .perform(
            post("/subscriptions")
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .param("subscriberId", subscriberId.toString()))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(
            jsonPath("$.message")
                .value(
                    String.format(
                        "Already subscribed on <{%d}>, or attempt to subscribe to himself.",
                        subscriberId)))
        .andExpect(jsonPath("status_code").value(HttpStatus.BAD_REQUEST.value()));

    mockMvc
        .perform(
            post("/subscriptions")
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .param("subscriberId", userId.toString()))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(
            jsonPath("$.message")
                .value(
                    String.format(
                        "Already subscribed on <{%d}>, or attempt to subscribe to himself.",
                        userId)))
        .andExpect(jsonPath("status_code").value(HttpStatus.BAD_REQUEST.value()));

    verify(subscriptionRepository, times(0)).save(any());
    verify(subscriptionRepository, times(1)).checkIfUserIsSubscribed(any(), any());
  }

  @Test
  @DisplayName(
      "When calling get /subscriptions/status, expect that request is valid and "
          + "then response with is correct and status code 200")
  @WithMockUser(
      username = "user",
      roles = {"USER"})
  void testMethodCheckIfUserIsSubscribed() throws Exception {
    Integer userId = user.getId();
    Integer subscriberId = subscriber.getId();
    authorizedUser.setId(userId);
    SecurityContextHolder.getContext()
        .setAuthentication(
            new UsernamePasswordAuthenticationToken(
                authorizedUser, "password", authorizedUser.getAuthorities()));

    when(subscriptionRepository.checkIfUserIsSubscribed(userId, subscriberId))
        .thenReturn(Optional.ofNullable(subscription));

    mockMvc
        .perform(
            get("/subscriptions/status")
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .param("subscriberId", subscriberId.toString()))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").value(true));

    verify(subscriptionRepository, times(1)).checkIfUserIsSubscribed(any(), any());
  }

  @Test
  @DisplayName(
      "When calling delete /subscription, expect that request is valid and we delete subscription,"
          + "then response with successfully message and status code 200")
  @WithMockUser(
      username = "user",
      roles = {"USER"})
  void testMethodDeleteById() throws Exception {
    Integer subscriptionId = subscription.getId();

    when(subscriptionRepository.existsById(subscriptionId)).thenReturn(Boolean.TRUE);
    doNothing().when(subscriptionRepository).deleteById(subscriptionId);

    mockMvc
        .perform(
            delete("/subscriptions/{subscriptionId}", subscriptionId)
                .header(HttpHeaders.AUTHORIZATION, accessToken))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.message").value("Subscription was deleted successfully"));

    verify(subscriptionRepository, times(1)).existsById(any());
    verify(subscriptionRepository, times(1)).deleteById(any());
  }

  @Test
  @DisplayName(
      "When calling delete /subscription, expect that request are invalid (subscriptionId is not exists)"
          + " then response with ErrorResponse.class and status code 401")
  @WithMockUser(
      username = "user",
      roles = {"USER"})
  void testExceptionInMethodDeleteById() throws Exception {
    Integer subscriptionId = subscription.getId() + 100;

    mockMvc
        .perform(
            delete("/subscriptions/{subscriptionId}", subscriptionId)
                .header(HttpHeaders.AUTHORIZATION, accessToken))
        .andExpect(status().isNotFound())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(
            jsonPath("$.message")
                .value(String.format("Subscription with id [%d] not found.", subscriptionId)));

    verify(subscriptionRepository, times(1)).existsById(any());
    verify(subscriptionRepository, times(0)).deleteById(any());
  }
}
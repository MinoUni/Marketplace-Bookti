package com.teamchallenge.bookti.subscription;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.teamchallenge.bookti.constant.UserConstant;
import com.teamchallenge.bookti.exception.subscription.SubscriptionException;
import com.teamchallenge.bookti.exception.user.UserNotFoundException;
import com.teamchallenge.bookti.user.User;
import com.teamchallenge.bookti.user.UserRepository;
import com.teamchallenge.bookti.user.dto.UserSubscriptionDTO;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class SubscriptionServiceImpTest {

  @MockBean
  private UserRepository userRepository;
  @MockBean
  private SubscriptionRepository subscriptionRepository;
  @Autowired
  private SubscriptionServiceImp subscriptionService;

  private User user;
  private User subscriber;
  private Subscription subscription;

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
  }

  @Test
  @DisplayName("Test SubscriptionServiceImp method findAllUserSubscriptionById")
  void testMethodFindAllUserSubscriptionById() {
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

    when(userRepository.existsById(userId)).thenReturn(true);
    when(subscriptionRepository.findAllUserSubscriptionById(userId)).thenReturn(subscriptionList);

    List<UserSubscriptionDTO> userSubscriptions =
        subscriptionService.findAllUserSubscriptionById(userId);

    assertFalse(userSubscriptions.isEmpty());
    assertEquals(2, userSubscriptions.size());
  }

  @Test
  @DisplayName("Test SubscriptionServiceImp exception in method findAllUserSubscriptionById.")
  void testExceptionInMethodFindAllUserSubscriptionById() {
    Integer userIdNotExist = user.getId() + 100;

    UserNotFoundException errorIfUserIdIsNotExist =
        assertThrows(
            UserNotFoundException.class,
            () -> subscriptionService.findAllUserSubscriptionById(userIdNotExist));

    assertEquals(
        String.format(UserConstant.NOT_FOUND_MESSAGE, userIdNotExist),
        errorIfUserIdIsNotExist.getMessage());

    verify(userRepository, times(1)).existsById(any());
    verify(subscriptionRepository, times(0)).findAllUserSubscriptionById(any());
  }

  @Test
  @DisplayName("Test SubscriptionServiceImp method save")
  void testMethodSave() {
    Integer userId = user.getId();
    Integer subscriberId = subscriber.getId();

    when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(user));
    when(userRepository.findById(subscriberId)).thenReturn(Optional.ofNullable(subscriber));
    when(subscriptionRepository.checkIfUserIsSubscribed(userId, subscriberId))
        .thenReturn(Optional.empty());
    when(subscriptionRepository.save(any())).thenReturn(subscription);

    String result = subscriptionService.save(userId, subscriberId);

    assertFalse(result.isEmpty());
    assertEquals("Your subscription was successfully", result);

    verify(userRepository, times(2)).findById(any());
    verify(subscriptionRepository, times(1)).checkIfUserIsSubscribed(any(), any());
    verify(subscriptionRepository, times(1)).save(any());
  }

  @Test
  @DisplayName("Test SubscriptionServiceImp exception in method save.")
  void testExceptionInMethodSave() {
    Integer userId = user.getId();
    Integer subscriberId = subscriber.getId();
    Integer userIdNotExist = user.getId() + 100;
    Integer subscriberIdNotExist = subscriber.getId() + 100;

    UserNotFoundException errorIfUserIdIsNotExist =
        assertThrows(
            UserNotFoundException.class,
            () -> subscriptionService.save(userIdNotExist, subscriberId));

    assertEquals(
        String.format(UserConstant.NOT_FOUND_MESSAGE, userIdNotExist),
        errorIfUserIdIsNotExist.getMessage());

    when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(user));

    UserNotFoundException errorIfSubscriberIdIsNotExist =
        assertThrows(
            UserNotFoundException.class,
            () -> subscriptionService.save(userId, subscriberIdNotExist));

    assertEquals(
        String.format("Subscriber with id <{%d}> not found.", subscriberIdNotExist),
        errorIfSubscriberIdIsNotExist.getMessage());

    when(userRepository.findById(subscriberId)).thenReturn(Optional.ofNullable(subscriber));
    when(subscriptionRepository.checkIfUserIsSubscribed(userId, subscriberId))
        .thenReturn(Optional.ofNullable(subscription));

    SubscriptionException errorIfUserIsSubscribed =
        assertThrows(
            SubscriptionException.class, () -> subscriptionService.save(userId, subscriberId));

    assertEquals(
        String.format(
            "Already subscribed on <{%d}>, or attempt to subscribe to himself.",
            subscriberId),
        errorIfUserIsSubscribed.getMessage());

    verify(userRepository, times(5)).findById(any());
    verify(subscriptionRepository, times(0)).save(any());
  }

  @Test
  @DisplayName("Test SubscriptionServiceImp method checkIfUserIsSubscribed")
  void testMethodCheckIfUserIsSubscribed() {
    Integer userId = user.getId();
    Integer subscriberId = subscriber.getId();
    Integer subscriberIdNotSubscribed = subscriber.getId() + 100;

    when(subscriptionRepository.checkIfUserIsSubscribed(userId, subscriberId))
        .thenReturn(Optional.ofNullable(subscription));
    when(subscriptionRepository.checkIfUserIsSubscribed(userId, subscriberIdNotSubscribed))
        .thenReturn(Optional.empty());

    Boolean userIsSubscribed = subscriptionService.checkIfUserIsSubscribed(userId, subscriberId);

    assertTrue(userIsSubscribed);

    Boolean userIsNotSubscribed =
        subscriptionService.checkIfUserIsSubscribed(userId, subscriberIdNotSubscribed);

    assertFalse(userIsNotSubscribed);

    verify(subscriptionRepository, times(2)).checkIfUserIsSubscribed(any(), any());
  }

  @Test
  @DisplayName("Test SubscriptionServiceImp method deleteById")
  void testMethodDeleteById() {
    Integer subscriptionId = subscription.getId();

    when(subscriptionRepository.existsById(subscriptionId)).thenReturn(Boolean.TRUE);
    doNothing().when(subscriptionRepository).deleteById(subscriptionId);

    String result = subscriptionService.deleteById(subscriptionId);

    assertFalse(result.isEmpty());
    assertEquals("Subscription was deleted successfully", result);

    verify(subscriptionRepository, times(1)).existsById(any());
    verify(subscriptionRepository, times(1)).deleteById(any());
  }
}
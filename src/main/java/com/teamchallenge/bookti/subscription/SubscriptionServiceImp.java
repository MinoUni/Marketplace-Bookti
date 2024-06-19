package com.teamchallenge.bookti.subscription;

import com.teamchallenge.bookti.constant.UserConstant;
import com.teamchallenge.bookti.exception.subscription.SubscriptionException;
import com.teamchallenge.bookti.exception.user.UserNotFoundException;
import com.teamchallenge.bookti.mapper.UserMapper;
import com.teamchallenge.bookti.user.User;
import com.teamchallenge.bookti.user.UserRepository;
import com.teamchallenge.bookti.user.dto.UserSubscriptionDTO;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubscriptionServiceImp implements SubscriptionService {

  private final UserRepository userRepository;
  private final SubscriptionRepository subscriptionRepository;
  private final UserMapper userMapper;

  @Override
  public List<UserSubscriptionDTO> findAllUserSubscriptionById(Integer userId) {
    if (!userRepository.existsById(userId)) {
      log.info(
          "SubscriptionServiceImp::findAllUserSubscriptionById."
              + " Throw UserNotFoundException with NOT_FOUND_MESSAGE. userId: {}",
          userId);
      throw new UserNotFoundException(String.format(UserConstant.NOT_FOUND_MESSAGE, userId));
    }
    List<UserSubscriptionDTO> userReviewList =
        subscriptionRepository.findAllUserSubscriptionById(userId).stream()
            .map(
                subscription ->
                    userMapper.toUserSubscriptionDTO(subscription.getSubscriber(), subscription))
            .sorted(Comparator.comparing(UserSubscriptionDTO::getFullName))
            .toList();
    log.info(
        "SubscriptionServiceImp::findAllUserSubscriptionById - "
            + "return list or empty list, received review to user: {}.",
        userId);

    return userReviewList;
  }

  @Transactional
  @Override
  public String save(Integer userId, Integer subscriberId) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(
                () -> {
                  log.info(
                      "SubscriptionServiceImp::save. Throw UserNotFoundException with message"
                          + " (User with id <{}> not found.)",
                      userId);
                  return new UserNotFoundException(
                      String.format(UserConstant.NOT_FOUND_MESSAGE, userId));
                });

    User userSubscriber =
        userRepository
            .findById(subscriberId)
            .orElseThrow(
                () -> {
                  log.info(
                      "SubscriptionServiceImp::save. Throw UserNotFoundException with message"
                          + " (Subscriber with id <{}> not found.)",
                      subscriberId);
                  return new UserNotFoundException(
                      String.format("Subscriber with id <{%d}> not found.", subscriberId));
                });

    if (userId.equals(subscriberId) || checkIfUserIsSubscribed(userId, subscriberId)) {
      log.info(
          "SubscriptionServiceImp::save - Throw UserNotFoundException with message"
              + "Already subscribed on <{}>, or attempt to subscribe to himself.",
          subscriberId);
      throw new SubscriptionException(
          String.format(
              "Already subscribed on <{%d}>, or attempt to subscribe to himself.", subscriberId),
          HttpStatus.BAD_REQUEST);
    }
    Subscription newSubscription =
        Subscription.builder()
            .user(user)
            .subscriber(userSubscriber)
            .status(SubscriptionStatus.SUBSCRIBED)
            .build();

    subscriptionRepository.save(newSubscription);
    log.info("SubscriptionServiceImp::save. Create new User subscription to user: {}.", userId);

    return "Your subscription was successfully";
  }

  @Override
  public Boolean checkIfUserIsSubscribed(Integer userId, Integer subscriberId) {
    return subscriptionRepository.checkIfUserIsSubscribed(userId, subscriberId).isPresent();
  }

  @Transactional
  @Override
  public String deleteById(Integer subscriptionId) {
    if (!subscriptionRepository.existsById(subscriptionId)) {
      log.info(
          "SubscriptionServiceImp::deleteById. "
              + "Throw SubscriptionException with NOT FOUND MESSAGE. subscriptionId: {}",
          subscriptionId);
      throw new SubscriptionException(
          String.format("Subscription with id [%d] not found.", subscriptionId),
          HttpStatus.NOT_FOUND);
    }

    subscriptionRepository.deleteById(subscriptionId);
    log.info(
        "SubscriptionServiceImp::deleteById. deleted User subscription from id: {}.",
        subscriptionId);

    return "Subscription was deleted successfully";
  }
}

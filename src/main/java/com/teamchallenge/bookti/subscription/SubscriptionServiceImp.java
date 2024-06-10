package com.teamchallenge.bookti.subscription;

import com.teamchallenge.bookti.constant.UserConstant;
import com.teamchallenge.bookti.exception.user.UserNotFoundException;
import com.teamchallenge.bookti.mapper.UserMapper;
import com.teamchallenge.bookti.user.User;
import com.teamchallenge.bookti.user.UserRepository;
import com.teamchallenge.bookti.user.dto.UserSubscriptionDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

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
            log.info("SubscriptionServiceImp::findAllUserSubscriptionById. Throw UserNotFoundException with NOT_FOUND_MESSAGE. userId: {}", userId);
            throw new UserNotFoundException(String.format(UserConstant.NOT_FOUND_MESSAGE, userId));
        }
        List<UserSubscriptionDTO> userReviewList = subscriptionRepository.findAllUserSubscriptionById(userId)
                .stream()
                .map(subscription -> userMapper.toUserSubscriptionDTO(subscription.getSubscriber(), subscription))
                .sorted(Comparator.comparing(UserSubscriptionDTO::getFullName))
                .toList();
        log.info("SubscriptionServiceImp::findAllUserSubscriptionById - return list or empty list, received review to user: {}.", userId);

        return userReviewList;
    }

    @Transactional
    @Override
    public String save(Integer userId, Integer subscriberId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format(UserConstant.NOT_FOUND_MESSAGE, userId)));

        User userSubscriber = userRepository.findById(subscriberId)
                .orElseThrow(() -> new UserNotFoundException(String.format("Subscriber with id <{%d}> not found.", subscriberId)));

        if (checkIfUserIsSubscribed(userId, subscriberId)) {
            log.info("SubscriptionServiceImp::save. Throw UserNotFoundException with message (You are already subscribed to user with id <{}>)", subscriberId);
            throw new UserNotFoundException(String.format("You are already subscribed to user with id <{%d}>.", subscriberId));
        }
        Subscription newSubscription = Subscription.builder()
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
            log.info("SubscriptionServiceImp::deleteById. Throw UserNotFoundException with NOT_FOUND_MESSAGE. subscriptionId: {}", subscriptionId);
            throw new UserNotFoundException(String.format("Subscription with id [%d] not found.", subscriptionId));
        }

        subscriptionRepository.deleteById(subscriptionId);
        log.info("SubscriptionServiceImp::deleteById. deleted User subscription from id: {}.", subscriptionId);

        return "Subscription was deleted successfully";
    }
}

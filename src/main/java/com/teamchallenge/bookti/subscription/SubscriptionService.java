package com.teamchallenge.bookti.subscription;

import com.teamchallenge.bookti.user.dto.UserSubscriptionDTO;

import java.util.List;

public interface SubscriptionService {

    List<UserSubscriptionDTO> findAllUserSubscriptionById(Integer userId);

    String save(Integer userId, Integer subscriberId);

    Boolean checkIfUserIsSubscribed(Integer userId, Integer subscriberId);

    String deleteById(Integer subscriptionId);
}

package com.teamchallenge.bookti.subscription;

import com.teamchallenge.bookti.user.dto.UserSubscriptionDTO;

import java.util.List;
import java.util.Set;

public interface SubscriptionService {

    List<UserSubscriptionDTO> findAllUserSubscriptionById(Integer userId);

    String save(Integer userId, Integer subscriId);

    Boolean checkIfUserIsSubscribed(Integer userId, Integer subscriId);

    String deleteById(Integer subscriptionId);
}

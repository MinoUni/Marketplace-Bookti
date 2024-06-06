package com.teamchallenge.bookti.user.dto;

import com.teamchallenge.bookti.subscription.SubscriptionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSubscriptionDTO {

    private Integer subscriptionId;

    private Integer userId;

    private String fullName;

    private String location;

    private String avatarUrl;

    private SubscriptionStatus status;
}

package com.teamchallenge.bookti.mapper;

import com.teamchallenge.bookti.subscription.Subscription;
import com.teamchallenge.bookti.user.User;
import com.teamchallenge.bookti.user.dto.UserSubscriptionDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import static org.mapstruct.InjectionStrategy.CONSTRUCTOR;
import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring",
        injectionStrategy = CONSTRUCTOR,
        nullValuePropertyMappingStrategy = IGNORE)
public interface UserMapper {

    @Mapping(source = "subscription.id", target = "subscriptionId")
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.fullName", target = "fullName")
    @Mapping(source = "user.location", target = "location")
    @Mapping(source = "user.avatarUrl", target = "avatarUrl")
    @Mapping(source = "subscription.status", target = "status")
    UserSubscriptionDTO toUserSubscriptionDTO(User user, Subscription subscription);
}

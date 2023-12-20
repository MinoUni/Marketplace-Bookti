package com.teamchallenge.bookti.mapper;

import com.teamchallenge.bookti.model.UserEntity;
import com.teamchallenge.bookti.security.AuthorizedUser;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

@UtilityClass
public class AuthorizedUserMapper {

    public AuthorizedUser mapFrom(UserEntity user) {
        List<? extends GrantedAuthority> authorities = List.of(); // TODO: Add authorities to user model
        return AuthorizedUser
                .authorizedUserBuilder(user.getEmail(), user.getPassword(), authorities)
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .avatarUrl(user.getAvatarUrl())
                .build();
    }
}

package com.teamchallenge.bookti.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.teamchallenge.bookti.model.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class UserInfo {

    private UUID id;

    private String email;

    @JsonProperty("full_name")
    private String fullName;

    @JsonProperty("avatar_url")
    private String avatarUrl;

    public static UserInfo mapFrom(UserEntity user) {
        return UserInfo
                .builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .avatarUrl(user.getAvatarUrl())
                .build();
    }
}

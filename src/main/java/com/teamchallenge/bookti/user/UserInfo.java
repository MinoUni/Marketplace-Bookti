package com.teamchallenge.bookti.user;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * UserInfo is DTO class witch contains information about user.
 *
 * @author Maksym Reva
 */
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

  /**
   * Maps {@link UserInfo} from {@link UserEntity}.
   *
   * @param user {@link UserEntity}
   * @return {@link UserInfo}
   */
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

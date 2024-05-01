package com.teamchallenge.bookti.mapper;

import com.teamchallenge.bookti.security.AuthorizedUser;
import com.teamchallenge.bookti.user.User;

/**
 * Mapper for {@link AuthorizedUser} and {@link User}.
 *
 * @author Maksym Reva
 */
public class AuthorizedUserMapper {

  private AuthorizedUserMapper() {}

  /**
   * Maps {@link AuthorizedUser} from {@link User}.
   *
   * @param user {@link User} that will be mapped to {@link AuthorizedUser}
   * @return {@link AuthorizedUser} custom realization of user principal
   */
  public static AuthorizedUser mapFrom(User user) {
    var authorities = user.getRole().getAuthorities();
    return AuthorizedUser.authorizedUserBuilder(user.getEmail(), user.getPassword(), authorities)
        .id(user.getId())
        .fullName(user.getFullName())
        .avatarUrl(user.getAvatarUrl())
        .build();
  }
}

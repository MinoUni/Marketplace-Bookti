package com.teamchallenge.bookti.mapper;

import com.teamchallenge.bookti.model.UserEntity;
import com.teamchallenge.bookti.security.AuthorizedUser;
import java.util.List;
import java.util.Map;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

/**
 * Mapper for {@link AuthorizedUser} and {@link UserEntity}.
 *
 * @author Maksym Reva
 */
public class AuthorizedUserMapper {

  private AuthorizedUserMapper() {}

  /**
   * Maps {@link AuthorizedUser} from {@link UserEntity}.
   *
   * @param user {@link UserEntity} that will be mapped to {@link AuthorizedUser}
   * @return {@link AuthorizedUser} custom realization of user principal
   */
  public static AuthorizedUser mapFrom(UserEntity user) {
    List<? extends GrantedAuthority> authorities = List.of(); // TODO: Add authorities to user model
    return AuthorizedUser.authorizedUserBuilder(user.getEmail(), user.getPassword(), authorities)
        .id(user.getId())
        .fullName(user.getFullName())
        .avatarUrl(user.getAvatarUrl())
        .build();
  }

  /**
   * Build {@link AuthorizedUser} user principal object from {@link UserEntity} user info stored in
   * database and {@link OAuth2User} attributes.
   *
   * @param user user info that stored in database
   * @param attributes the attributes about the user
   * @return {@link AuthorizedUser} custom realization of user principal
   */
  public static AuthorizedUser mapFrom(UserEntity user, Map<String, Object> attributes) {
    // TODO: Add authorities to user model
    List<? extends GrantedAuthority> authorities = List.of();
    return AuthorizedUser.authorizedUserBuilder(user.getEmail(), user.getPassword(), authorities)
        .id(user.getId())
        .fullName(user.getFullName())
        .avatarUrl(user.getAvatarUrl())
        .attributes(attributes)
        .build();
  }
}

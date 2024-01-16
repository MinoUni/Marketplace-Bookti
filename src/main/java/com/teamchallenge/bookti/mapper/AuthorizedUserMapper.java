package com.teamchallenge.bookti.mapper;

import com.teamchallenge.bookti.model.UserEntity;
import com.teamchallenge.bookti.security.AuthorizedUser;
import java.util.List;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.GrantedAuthority;

/**
 * Mapper for {@link AuthorizedUser} and {@link UserEntity}.
 *
 * @author Maksym Reva
 */
@UtilityClass
public class AuthorizedUserMapper {

  /**
   * Maps {@link AuthorizedUser} from {@link UserEntity}.
   *
   * @param user {@link UserEntity} that will be mapped to {@link AuthorizedUser}
   * @return {@link AuthorizedUser}
   */
  public AuthorizedUser mapFrom(UserEntity user) {
    List<? extends GrantedAuthority> authorities = List.of(); // TODO: Add authorities to user model
    return AuthorizedUser
        .authorizedUserBuilder(user.getEmail(), user.getPassword(), authorities)
        .id(user.getId())
        .fullName(user.getFullName())
        .avatarUrl(user.getAvatarUrl())
        .build();
  }
}

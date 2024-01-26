package com.teamchallenge.bookti.security;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.util.Assert;

/**
 * Class that contains information about authorized user.
 *
 * @author Maksym Reva
 */
@Setter
@Getter
public class AuthorizedUser extends User implements OAuth2User {

  private UUID id;
  @Setter(AccessLevel.NONE)
  @Getter(AccessLevel.NONE)
  private String email;
  private String fullName;
  private String avatarUrl;
  private Map<String, Object> attributes;

  public AuthorizedUser(String username,
                        String password,
                        Collection<? extends GrantedAuthority> authorities) {
    super(username, password, authorities);
  }

  public String getEmail() {
    return this.getUsername();
  }

  public static AuthorizedUserBuilder authorizedUserBuilder(
      String username, String password, Collection<? extends GrantedAuthority> authorities) {
    return new AuthorizedUserBuilder(username, password, authorities);
  }

  @Override
  public Map<String, Object> getAttributes() {
    return attributes;
  }

  @Override
  public String getName() {
    return email;
  }

  /**
   * Builder for {@link AuthorizedUser}.
   *
   * @author Maksym Reva
   */
  public static final class AuthorizedUserBuilder {

    private final AuthorizedUser authorizedUser;

    public AuthorizedUserBuilder(
        String username, String password, Collection<? extends GrantedAuthority> authorities) {
      this.authorizedUser = new AuthorizedUser(username, password, authorities);
    }

    /**
     * Sets {@link AuthorizedUser#id}.
     *
     * @param id user's UUID that cannot be null
     * @return {@link AuthorizedUserBuilder}
     */
    public AuthorizedUserBuilder id(UUID id) {
      Assert.notNull(id, "id cannot be null");
      this.authorizedUser.setId(id);
      return this;
    }

    public AuthorizedUserBuilder fullName(String fullName) {
      this.authorizedUser.setFullName(fullName);
      return this;
    }

    public AuthorizedUserBuilder avatarUrl(String avatarUrl) {
      this.authorizedUser.setAvatarUrl(avatarUrl);
      return this;
    }

    public AuthorizedUserBuilder attributes(Map<String, Object> attributes) {
      this.authorizedUser.setAttributes(attributes);
      return this;
    }

    public AuthorizedUser build() {
      return this.authorizedUser;
    }
  }
}

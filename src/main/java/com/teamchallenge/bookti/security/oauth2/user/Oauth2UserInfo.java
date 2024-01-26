package com.teamchallenge.bookti.security.oauth2.user;

import java.util.Map;
import lombok.Getter;

/**
 * Determine what properties to extract from UserInfo.
 *
 * @author Maksym Reva
 */
@Getter
public abstract class Oauth2UserInfo {

  protected Map<String, Object> attributes;

  public Oauth2UserInfo(Map<String, Object> attributes) {
    this.attributes = attributes;
  }

  public abstract String getId();

  public abstract String getFullName();

  public abstract String getEmail();

  public abstract String getAvatarUrl();
}

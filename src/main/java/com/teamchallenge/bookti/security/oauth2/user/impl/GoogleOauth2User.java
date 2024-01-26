package com.teamchallenge.bookti.security.oauth2.user.impl;

import com.teamchallenge.bookti.security.oauth2.user.Oauth2UserInfo;
import java.util.Map;

/**
 * Implementation of {@link Oauth2UserInfo} with Google UserInfo.
 *
 * @author MinoUni
 */
public class GoogleOauth2User extends Oauth2UserInfo {

  public GoogleOauth2User(Map<String, Object> attributes) {
    super(attributes);
  }

  @Override
  public String getId() {
    return (String) attributes.get("sub");
  }

  @Override
  public String getFullName() {
    return (String) attributes.get("name");
  }

  @Override
  public String getEmail() {
    return (String) attributes.get("email");
  }

  @Override
  public String getAvatarUrl() {
    return (String) attributes.get("picture");
  }
}

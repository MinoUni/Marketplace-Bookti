package com.teamchallenge.bookti.security.oauth2.user;

import com.teamchallenge.bookti.exception.Oauth2AuthenticationProcessingException;
import com.teamchallenge.bookti.security.oauth2.user.impl.GoogleOauth2User;
import java.util.Map;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;

/**
 * Factory to build a proper object according to {@link OAuth2UserRequest#getClientRegistration()}.
 *
 * @author Maksym Reva
 */
public class Oauth2UserInfoFactory {

  private Oauth2UserInfoFactory() {}

  /**
   * Extract UserInfo from user attributes according to client registration id.
   *
   * @param clientRegistrationId the client registration id
   * @param attributes the attributes about the user
   * @return UserInfo according to client registration id
   */
  public static Oauth2UserInfo getInfo(
      String clientRegistrationId, Map<String, Object> attributes) {
    try {
      switch (AuthorizationProvider.valueOf(clientRegistrationId.toUpperCase())) {
        case GOOGLE -> {
          return new GoogleOauth2User(attributes);
        }
        case FACEBOOK ->
            throw new UnsupportedOperationException("Facebook client not supported yet");
        default ->
            throw new Oauth2AuthenticationProcessingException(
                String.format("Provider <%s> not supported", clientRegistrationId));
      }
    } catch (IllegalArgumentException e) {
      throw new Oauth2AuthenticationProcessingException(
          String.format("Provider <%s> not supported", clientRegistrationId));
    }
  }

  private enum AuthorizationProvider {
    GOOGLE("google"),
    FACEBOOK("facebook");

    private final String providerName;

    AuthorizationProvider(String providerName) {
      this.providerName = providerName;
    }

    @Override
    public String toString() {
      return providerName;
    }
  }
}

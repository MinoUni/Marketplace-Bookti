package com.teamchallenge.bookti.security.oauth2.service;

import static com.teamchallenge.bookti.model.Role.ROLE_USER;

import com.teamchallenge.bookti.exception.Oauth2AuthenticationProcessingException;
import com.teamchallenge.bookti.mapper.AuthorizedUserMapper;
import com.teamchallenge.bookti.model.UserEntity;
import com.teamchallenge.bookti.repository.UserRepository;
import com.teamchallenge.bookti.security.AuthorizedUser;
import com.teamchallenge.bookti.security.oauth2.user.Oauth2UserInfo;
import com.teamchallenge.bookti.security.oauth2.user.Oauth2UserInfoFactory;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

/**
 * Subclass of {@link DefaultOAuth2UserService}. Used to save UserInfo into database or update
 * already existed UserInfo.
 *
 * @author MinoUni
 * @see OAuth2UserRequest
 * @see OAuth2User
 * @see DefaultOAuth2UserService
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CustomOauth2UserService extends DefaultOAuth2UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  /**
   * The method returns object OAuth2User as result of successful authentication.
   *
   * @param userRequest request that contains {@link ClientRegistration client registration} and
   *     {@link OAuth2AccessToken access token}
   * @return {@link OAuth2User} custom user {@link AuthorizedUser}
   * @throws OAuth2AuthenticationException user authentication failed
   */
  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    OAuth2User user = super.loadUser(userRequest);
    try {
      return extractUserInfo(user, userRequest);
    } catch (OAuth2AuthenticationException e) {
      throw new InternalAuthenticationServiceException(e.getMessage(), e.getCause());
    }
  }

  /**
   * Extract UserInfo from {@link OAuth2UserRequest} and save it to database.
   *
   * @param auth2User A representation of a user {@code Principal} that is registered with an OAuth
   *     2.0 Provider.
   * @param userRequest the user request
   * @return {@link AuthorizedUser} custom representation of a user principal
   */
  private OAuth2User extractUserInfo(OAuth2User auth2User, OAuth2UserRequest userRequest)
      throws Oauth2AuthenticationProcessingException {
    String clientRegistrationId = userRequest.getClientRegistration().getRegistrationId();
    Map<String, Object> attributes = auth2User.getAttributes();
    Oauth2UserInfo userInfo = Oauth2UserInfoFactory.getInfo(clientRegistrationId, attributes);
    if (ObjectUtils.isEmpty(userInfo.getEmail())) {
      throw new Oauth2AuthenticationProcessingException("Email not found!");
    }
    var user = userRepository.findByEmail(userInfo.getEmail());
    return AuthorizedUserMapper.mapFrom(
        user.map(u -> update(u, userInfo)).orElseGet(() -> save(userInfo)),
        auth2User.getAttributes());
  }

  /**
   * Save new user into database.
   *
   * @param userInfo user info extracted from {@link OAuth2User}
   * @return {@link UserEntity}
   */
  private UserEntity save(Oauth2UserInfo userInfo) {
    UserEntity user = UserEntity.builder()
            .email(userInfo.getEmail())
            // todo: Fix issue with assigning random password
            .password(passwordEncoder.encode(UUID.randomUUID().toString()))
            .fullName(userInfo.getFullName())
            .avatarUrl(userInfo.getAvatarUrl())
            .role(ROLE_USER)
            .build();
    return userRepository.save(user);
  }

  /**
   * Update existed user info.
   *
   * @param user existed user
   * @param userInfo user info extracted from {@link OAuth2User}
   * @return {@link UserEntity}
   */
  private UserEntity update(UserEntity user, Oauth2UserInfo userInfo) {
    user.setFullName(userInfo.getFullName() + "sss");
    user.setAvatarUrl(userInfo.getAvatarUrl());
    return userRepository.save(user);
  }
}

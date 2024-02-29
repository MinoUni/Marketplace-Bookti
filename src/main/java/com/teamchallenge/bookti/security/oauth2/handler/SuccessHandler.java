package com.teamchallenge.bookti.security.oauth2.handler;

import static com.teamchallenge.bookti.security.oauth2.repository.CustomAuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;

import com.teamchallenge.bookti.exception.BadRequestException;
import com.teamchallenge.bookti.user.UserRepository;
import com.teamchallenge.bookti.security.jwt.TokenManager;
import com.teamchallenge.bookti.security.oauth2.repository.CustomAuthorizationRequestRepository;
import com.teamchallenge.bookti.security.oauth2.utils.CookieUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Handler that generate access and refresh JWT pair and configured with a default URL which users
 * should be sent to upon successful authentication.
 *
 * @see SimpleUrlAuthenticationSuccessHandler
 * @author MinoUni
 */
@Component
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  private final UserRepository userRepository;
  private final CustomAuthorizationRequestRepository authorizationRequestRepository;
  private final TokenManager tokenManager;

  @Value("#{'${application.authorized-redirect-uris}'.split(',')}")
  private List<String> authorizedRedirectUris;

  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain chain,
      Authentication authentication)
      throws IOException {
    String targetUrl = determineTargetUrl(request, response, authentication);
    if (response.isCommitted()) {
      log.debug("Response has already been committed. Unable to redirect to " + targetUrl);
      return;
    }
    clearAuthenticationAttributes(request, response);
    getRedirectStrategy().sendRedirect(request, response, targetUrl);
  }

  @Override
  protected String determineTargetUrl(
      HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
    Optional<String> redirectUri = CookieUtils
            .getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
            .map(Cookie::getValue);
    if (redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get())) {
      throw new BadRequestException(
          "Unauthorized Redirect URI, can't proceed with the authentication");
    }
    String targetUrl = redirectUri.orElse(getDefaultTargetUrl());
    var user = userRepository.findByEmail(authentication.getName())
            .orElseThrow(() -> new UsernameNotFoundException(
                        String.format(
                            "User with username <%s> not found.", authentication.getName()))
            );
    var tokenPair = tokenManager.generateTokenPair(authentication);
    return UriComponentsBuilder.fromUriString(targetUrl)
        .queryParam("accessToken", tokenPair.getAccessToken())
        .queryParam("refreshToken", tokenPair.getRefreshToken())
        .queryParam("id", user.getId())
        .build()
        .toUriString();
  }

  private void clearAuthenticationAttributes(
      HttpServletRequest request, HttpServletResponse response) {
    super.clearAuthenticationAttributes(request);
    authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
  }

  private boolean isAuthorizedRedirectUri(String uri) {
    URI clientRedirectUri = URI.create(uri);
    return authorizedRedirectUris.stream()
        .anyMatch(
            authorizedRedirectUri -> {
              URI authorizedUri = URI.create(authorizedRedirectUri);
              return authorizedUri.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
                  && authorizedUri.getPort() == clientRedirectUri.getPort();
            });
  }
}

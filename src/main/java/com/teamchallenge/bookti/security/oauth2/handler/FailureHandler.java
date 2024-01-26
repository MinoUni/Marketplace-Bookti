package com.teamchallenge.bookti.security.oauth2.handler;

import static com.teamchallenge.bookti.security.oauth2.repository.CustomAuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;

import com.teamchallenge.bookti.security.oauth2.repository.CustomAuthorizationRequestRepository;
import com.teamchallenge.bookti.security.oauth2.utils.CookieUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * AuthenticationFailureHandler which performs a redirect to the error page or provide JSON with
 * failure details.
 *
 * @see SimpleUrlAuthenticationFailureHandler
 * @author MinoUni
 */
@Component
@RequiredArgsConstructor
public class FailureHandler extends SimpleUrlAuthenticationFailureHandler {

  private final CustomAuthorizationRequestRepository authorizationRequestRepository;

  @Override
  public void onAuthenticationFailure(
      HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
      throws IOException {
    String targetUrl = CookieUtils
            .getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
            .map(Cookie::getValue)
            .orElse(("/"));
    targetUrl = UriComponentsBuilder
            .fromUriString(targetUrl)
            .queryParam("error", exception.getLocalizedMessage())
            .build()
            .toUriString();
    authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    getRedirectStrategy().sendRedirect(request, response, targetUrl);
  }
}

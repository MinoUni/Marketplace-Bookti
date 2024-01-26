package com.teamchallenge.bookti.security.oauth2.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.util.SerializationUtils;

/**
 * Utility class to serialize {@link OAuth2AuthorizationRequest} and store it a cookie.
 *
 * @author Maksym Reva
 */
public class CookieUtils {

  private CookieUtils() {}

  /**
   * Get cookie from {@link HttpServletRequest} by its name.
   *
   * @param request HTTP request information container
   * @param name Cookie name
   * @return {@link Cookie} or empty {@link Optional}
   */
  public static Optional<Cookie> getCookie(HttpServletRequest request, String name) {
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if (cookie.getName().equals(name)) {
          String decodedValue = URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8);
          cookie.setValue(decodedValue);
          return Optional.of(cookie);
        }
      }
    }

    return Optional.empty();
  }

  /**
   * Add a new cookie to {@link HttpServletResponse}.
   *
   * @param response Object to provide HTTP-functionality in sending response
   * @param name cookie name
   * @param value cookie value
   * @param maxAge cookie validity period(in seconds)
   */
  public static void addCookie(
      HttpServletResponse response, String name, String value, int maxAge) {
    String encodedValue = URLEncoder.encode(value, StandardCharsets.UTF_8);
    Cookie cookie = new Cookie(name, encodedValue);
    cookie.setPath("/");
    cookie.setHttpOnly(true);
    cookie.setMaxAge(maxAge);
    response.addCookie(cookie);
  }

  /**
   * Delete cookie from {@link HttpServletRequest} by its name.
   *
   * @param request HTTP request information container
   * @param response Object to provide HTTP-functionality in sending response
   * @param name Cookie name
   */
  public static void deleteCookie(
      HttpServletRequest request, HttpServletResponse response, String name) {
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if (cookie.getName().equals(name)) {
          cookie.setValue("");
          cookie.setPath("/");
          cookie.setMaxAge(0);
          response.addCookie(cookie);
        }
      }
    }
  }

  /**
   * Serialize the {@link OAuth2AuthorizationRequest} object to a byte array.
   *
   * @param object the object to serialize
   * @return String of encoded byte array
   */
  public static String serialize(Object object) {
    return Base64.getUrlEncoder().encodeToString(SerializationUtils.serialize(object));
  }

  /**
   * Deserialize the byte array into an {@link OAuth2AuthorizationRequest} object.
   *
   * @param cookie {@link Cookie}
   * @param clazz class to which object will be cast
   * @param <T> the type of the class modeled by this object
   * @return {@link OAuth2AuthorizationRequest} object
   */
  public static <T> T deserialize(Cookie cookie, Class<T> clazz) {
    return clazz.cast(
        SerializationUtils.deserialize(Base64.getUrlDecoder().decode(cookie.getValue())));
  }
}

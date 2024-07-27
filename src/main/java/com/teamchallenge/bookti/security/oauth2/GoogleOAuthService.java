package com.teamchallenge.bookti.security.oauth2;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.teamchallenge.bookti.exception.AuthorizedException;
import com.teamchallenge.bookti.mapper.AuthorizedMapper;
import com.teamchallenge.bookti.mapper.AuthorizedUserMapper;
import com.teamchallenge.bookti.security.AuthorizedUser;
import com.teamchallenge.bookti.user.User;
import com.teamchallenge.bookti.user.UserRepository;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@RequiredArgsConstructor
public class GoogleOAuthService {

  private final PasswordEncoder passwordEncoder;
  private final UserRepository userRepository;
  private final AuthorizedMapper authorizedMapper;

  private final String TOKEN_INFO_URL = "https://oauth2.googleapis.com/tokeninfo?access_token=";
  private final String USER_INFO_URL = "https://www.googleapis.com/oauth2/v3/userinfo";

  @Value("${GOOGLE_CLIENT_ID}")
  private String googleClientId;

  @Transactional
  public AuthorizedUser googleLogin(String accessToken) throws IOException {
    log.info("googleLogin accessToken : {}", accessToken);
    Map<String, Object> verifyGoogleAccessToken = verifyGoogleAccessToken(accessToken);
    boolean fullVerify = (Boolean) verifyGoogleAccessToken.get("boolean");
    log.info("googleLogin accessToken boolean : {}", fullVerify);
    if (fullVerify) {
      User user =
          getExistingOrSaveUserFromGoogle(
              accessToken, verifyGoogleAccessToken.get("sub").toString());
      log.info("GoogleOAuthService::googleLogin." + " Return verify Google User.");
      return AuthorizedUserMapper.mapFrom(user);
    } else {
      log.info(
          "GoogleOAuthService::googleLogin."
              + " Throw AuthorizedException with message "
              + "(AccessToken is not valid, full verify is : {})",
          fullVerify);
      throw new AuthorizedException(
          String.format("AccessToken is not valid, full verify is : /%s ", fullVerify),
          HttpStatus.BAD_REQUEST);
    }
  }

  @Transactional
  public User getExistingOrSaveUserFromGoogle(String accessToken, String socialIdentifier)
      throws IOException {
    Optional<User> user = userRepository.findBySocialIdentifier(socialIdentifier);
    if (user.isPresent()) {
      log.info(
          "GoogleOAuthService::getExistingOrSaveUserFromGoogle."
              + " Return user from google login. {}",
          user.get());
      return user.get();
    }
    User googleUser = getUserInfo(accessToken);
    User savedUser = userRepository.save(googleUser);
    log.info(
        "GoogleOAuthService::getExistingOrSaveUserFromGoogle."
            + " Saved new user from google login. {}",
        savedUser);

    return savedUser;
  }

  public Map<String, Object> verifyGoogleAccessToken(String accessToken) {
    try {
      RestTemplate restTemplate = new RestTemplate();
      String response = restTemplate.getForObject(TOKEN_INFO_URL + accessToken, String.class);
      log.info("RestTemplate response : {}", response);
      if (response != null) {
        JsonObject jsonResponse = JsonParser.parseString(response).getAsJsonObject();

        if (jsonResponse.has("error")) {
          throw new AuthorizedException(
              jsonResponse.get("error").getAsString(), HttpStatus.BAD_REQUEST);
        }

        long exp = jsonResponse.get("exp").getAsLong();

        LocalDateTime expiryDateTime =
            LocalDateTime.ofInstant(Instant.ofEpochSecond(exp), ZoneId.systemDefault());
        log.info("expiryDateTime verify: {}", expiryDateTime);
        LocalDateTime time = LocalDateTime.now();
        String audience = jsonResponse.get("aud").getAsString();

        boolean verifyGoogleTokenExp = expiryDateTime.isAfter(time);
        boolean verifyGoogleClientId = googleClientId.equals(audience);
        log.info("Access Token verify: {}", verifyGoogleClientId);
        log.info("verifyGoogleTokenExp {}", verifyGoogleTokenExp);

        boolean fullVerify = verifyGoogleTokenExp && verifyGoogleClientId;
        log.info(
            "GoogleOAuthService::verifyGoogleAccessToken." + " fullVerify AccessToken is : {}",
            fullVerify);
        String socialIdentifier = jsonResponse.get("sub").getAsString();
        Map<String, Object> mapResponse = new HashMap<>();
        mapResponse.put("boolean", fullVerify);
        mapResponse.put("sub", socialIdentifier);
        log.info(
            "GoogleOAuthService::verifyGoogleAccessToken."
                + " Return value full verify AccessToken ({}) and social identifier ({}).",
            fullVerify,
            socialIdentifier);
        return mapResponse;

      } else {
        log.info(
            "GoogleOAuthService::verifyGoogleAccessToken."
                + " Throw AuthorizedException with message (Problem with accessToken : {})",
            accessToken);
        throw new AuthorizedException(
            String.format("Problem with accessToken /%s ", accessToken), HttpStatus.BAD_REQUEST);
      }
    } catch (Exception e) {
      log.info(
          "GoogleOAuthService::verifyGoogleAccessToken."
              + " Throw AuthorizedException with message (Problem with accessToken : Message({})",
          e.getMessage());
      throw new AuthorizedException(
          String.format("Problem with accessToken. Message(/%s) ", e.getMessage()),
          HttpStatus.BAD_REQUEST);
    }
  }

  public User getUserInfo(String accessToken) throws IOException {

    NetHttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    GoogleCredential credential = new GoogleCredential().setAccessToken(accessToken);
    HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory(credential);
    try {
      com.google.api.client.http.HttpResponse response =
          requestFactory
              .buildGetRequest(new com.google.api.client.http.GenericUrl(USER_INFO_URL))
              .execute();

      String jsonIdentity = response.parseAsString();
      JsonObject jsonObject = JsonParser.parseString(jsonIdentity).getAsJsonObject();
      log.info("jsonObject: " + jsonObject);

      String name = jsonObject.get("name").getAsString();
      String email = jsonObject.get("email").getAsString();
      String avatarUrl = jsonObject.get("picture").getAsString();
      String socialIdentifier = jsonObject.get("sub").getAsString();
      String password = passwordEncoder.encode(name);
      String location = "";

      log.info("fullName: " + name);
      log.info("email: " + email);
      log.info("picture: " + avatarUrl);
      log.info("sub: " + socialIdentifier);

      User userInfo =
          authorizedMapper.toUser(email, name, avatarUrl, password, location, socialIdentifier);
      log.info("userInfo: " + userInfo);
      log.info("GoogleOAuthService::getUserInfo." + " Return User Info from google user.");

      return userInfo;
    } catch (Exception e) {
      log.info(
          "GoogleOAuthService::getUserInfo."
              + " Throw AuthorizedException with message (Problem with accessToken : Message({})",
          e.getMessage());
      throw new AuthorizedException(
          String.format("Problem with accessToken. Message(/%s) ", e.getMessage()),
          HttpStatus.BAD_REQUEST);
    }
  }
}

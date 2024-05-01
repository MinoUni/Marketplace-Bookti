package com.teamchallenge.bookti.security.jwt;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.teamchallenge.bookti.exception.user.RefreshTokenAlreadyRevokedException;
import com.teamchallenge.bookti.security.AuthorizedUser;
import com.teamchallenge.bookti.user.dto.TokenPair;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

@ExtendWith(MockitoExtension.class)
class TokenManagerTest {

  private static AuthorizedUser user;
  private static JwtClaimsSet expiredClaimSet;
  private static JwtClaimsSet validClaimSet;

  private final JwtEncoder refreshEncoder = mock(NimbusJwtEncoder.class);

  private final JwtEncoder accessEncoder = mock(NimbusJwtEncoder.class);

  private final TokenManager tokenManager = new TokenManager(accessEncoder, refreshEncoder, "test");

  @Mock
  private Authentication authentication;

  @BeforeAll
  static void setup() {
    user =
        AuthorizedUser.authorizedUserBuilder("username", "password", List.of())
            .id(1)
            .build();
    Instant now = Instant.now();
    expiredClaimSet =
        JwtClaimsSet.builder()
            .issuer("test-app")
            .issuedAt(now)
            .expiresAt(now.plus(2, ChronoUnit.DAYS))
            .subject(user.getId().toString())
            .build();
    validClaimSet =
        JwtClaimsSet.builder()
            .issuer("test-app")
            .issuedAt(now)
            .expiresAt(now.plus(7, ChronoUnit.DAYS))
            .subject(user.getId().toString())
            .build();
  }

  @BeforeEach
  void resetRevokedTokenMap() {
    tokenManager.getRevokedTokens().clear();
  }

  @Test
  @DisplayName("When user authenticate, then generate and return JWT key pair")
  void whenUserAuthenticateThenGenerateJwtTokenPair() {
    var jwt =
        new Jwt(
            "token-value",
            validClaimSet.getIssuedAt(),
            validClaimSet.getExpiresAt(),
            JwsHeader.with(SignatureAlgorithm.RS256).build().getHeaders(),
            validClaimSet.getClaims());

    when(authentication.getPrincipal()).thenReturn(user);
    when(authentication.getCredentials()).thenReturn(String.class);
    when(accessEncoder.encode(any())).thenReturn(jwt);
    when(refreshEncoder.encode(any())).thenReturn(jwt);

    TokenPair tokenPair = assertDoesNotThrow(() -> tokenManager.generateTokenPair(authentication));

    verify(authentication, times(3)).getPrincipal();
    verify(authentication, times(1)).getCredentials();
    verify(accessEncoder, times(1)).encode(any());
    verify(refreshEncoder, times(1)).encode(any());
    assertAll(
        "tokenPair",
        () -> assertNotNull(tokenPair),
        () -> assertEquals(user.getId(), tokenPair.getUserId()),
        () -> assertNotNull(tokenPair.getAccessToken()),
        () -> assertNotNull(tokenPair.getRefreshToken()),
        () -> assertEquals(tokenPair.getAccessToken(), jwt.getTokenValue()),
        () -> assertEquals(tokenPair.getRefreshToken(), jwt.getTokenValue()),
        () -> assertTrue(tokenManager.getRevokedTokens().isEmpty()));
  }

  @Test
  @DisplayName("When principal is not of type <AuthorizedUser>, then throw BadCredentialsException")
  void whenPrincipalIsNotAuthorizedUserTypeThenThrowBadCredentialsException() {
    when(authentication.getPrincipal()).thenReturn(new Object());

    var e = assertThrows(BadCredentialsException.class,
            () -> tokenManager.generateTokenPair(authentication));

    verify(authentication, times(1)).getPrincipal();
    assertAll(
        "exception",
        () -> assertNotNull(e),
        () -> assertTrue(e.getMessage().startsWith(String.format("Principal <%s>", Object.class))),
        () -> assertTrue(e.getMessage().endsWith(String.format("<%s> type", AuthorizedUser.class))),
        () -> assertTrue(tokenManager.getRevokedTokens().isEmpty()));
  }

  @Test
  @DisplayName("When user refresh access token with valid non-expired refresh token, then generate new access token and return it with old refresh token")
  void whenUserRefreshAccessTokenWithValidAndNonExpiredRefreshTokenThenReturnNewAccessTokenAndOldRefreshToken() {
    var validToken =
        new Jwt(
            "token-value",
            validClaimSet.getIssuedAt(),
            validClaimSet.getExpiresAt(),
            JwsHeader.with(SignatureAlgorithm.RS256).build().getHeaders(),
            validClaimSet.getClaims());

    when(authentication.getPrincipal()).thenReturn(user);
    when(authentication.getCredentials()).thenReturn(validToken);
    when(accessEncoder.encode(any())).thenReturn(validToken);

    TokenPair tokenPair = assertDoesNotThrow(() -> tokenManager.generateTokenPair(authentication));

    verify(authentication, times(2)).getPrincipal();
    verify(authentication, times(1)).getCredentials();
    verify(accessEncoder, times(1)).encode(any());
    verify(refreshEncoder, never()).encode(any());

    assertAll(
        "tokenPair",
        () -> assertNotNull(tokenPair),
        () -> assertEquals(user.getId(), tokenPair.getUserId()),
        () -> assertNotNull(tokenPair.getAccessToken()),
        () -> assertNotNull(tokenPair.getRefreshToken()),
        () -> assertEquals(validToken.getTokenValue(), tokenPair.getRefreshToken()),
        () -> assertTrue(tokenManager.getRevokedTokens().isEmpty()));
  }

  @Test
  @DisplayName("When user refresh access token with valid almost expired refresh token, then revoke refresh token and return new JWT token pair")
  void whenUserRefreshAccessTokenWithValidAndAlmostExpiredRefreshTokenThenRevokeRefreshTokenAndGenerateNewTokenPair() {
    Jwt expiredToken =
        new Jwt(
            "old-token-value",
            expiredClaimSet.getIssuedAt(),
            expiredClaimSet.getExpiresAt(),
            JwsHeader.with(SignatureAlgorithm.RS256).build().getHeaders(),
            expiredClaimSet.getClaims());
    var validToken =
        new Jwt(
            "new-token-value",
            validClaimSet.getIssuedAt(),
            validClaimSet.getExpiresAt(),
            JwsHeader.with(SignatureAlgorithm.RS256).build().getHeaders(),
            validClaimSet.getClaims());
    String expiredRefreshToken = expiredToken.getTokenValue();

    when(authentication.getPrincipal()).thenReturn(user);
    when(authentication.getCredentials()).thenReturn(expiredToken);
    when(accessEncoder.encode(any())).thenReturn(validToken);
    when(refreshEncoder.encode(any())).thenReturn(validToken);

    TokenPair tokenPair = assertDoesNotThrow(() -> tokenManager.generateTokenPair(authentication));

    verify(authentication, times(3)).getPrincipal();
    verify(authentication, times(1)).getCredentials();
    verify(accessEncoder, times(1)).encode(any());
    verify(refreshEncoder, times(1)).encode(any());
    assertAll(
        () -> assertNotNull(tokenPair),
        () -> assertEquals(user.getId(), tokenPair.getUserId()),
        () -> assertNotNull(tokenPair.getAccessToken()),
        () -> assertNotNull(tokenPair.getRefreshToken()),
        () -> assertNotEquals(expiredRefreshToken, tokenPair.getRefreshToken()),
        () -> assertTrue(tokenManager.getRevokedTokens().containsKey(expiredRefreshToken)));
  }

  @Test
  @DisplayName("When user revoke valid non-expired refresh token, then add this token to revokedToken list")
  void whenUserRevokeValidAndNonExpiredRefreshTokenThenAddTokenToRevokedList() {
    var tokenToRevoke =
        new Jwt(
            "token-to-revoke",
            validClaimSet.getIssuedAt(),
            validClaimSet.getExpiresAt(),
            JwsHeader.with(SignatureAlgorithm.RS256).build().getHeaders(),
            validClaimSet.getClaims());

    when(authentication.getCredentials()).thenReturn(tokenToRevoke);

    assertDoesNotThrow(() -> tokenManager.revokeToken(authentication));

    verify(authentication, times(1)).getCredentials();
    assertTrue(tokenManager.getRevokedTokens().containsKey(tokenToRevoke.getTokenValue()));
  }

  @Test
  @DisplayName("When user revoke refresh token that not a <Jwt.class>, than throw BadCredentialsException")
  void whenUserRevokeRefreshTokenIsNotJwtTypeThenThrowBadCredentialsException() {
    when(authentication.getCredentials()).thenReturn("");

    var e = assertThrows(BadCredentialsException.class,
            () -> tokenManager.revokeToken(authentication));

    verify(authentication, times(1)).getCredentials();
    assertAll(
        () -> assertNotNull(e),
        () -> assertTrue(e.getMessage().startsWith(String.format("Credentials <%s>", String.class))),
        () -> assertTrue(e.getMessage().endsWith(String.format("<%s> type", Jwt.class))),
        () -> assertTrue(tokenManager.getRevokedTokens().isEmpty()));
  }

  @Test
  @DisplayName("When user revoke refresh token that already revoked, than throw RefreshTokenAlreadyRevokedException")
  void whenUserRevokeAlreadyRevokedRefreshTokenThenThrowRefreshTokenAlreadyRevokedException() {
    var revokedToken =
        new Jwt(
            "token-to-revoke",
            validClaimSet.getIssuedAt(),
            validClaimSet.getExpiresAt(),
            JwsHeader.with(SignatureAlgorithm.RS256).build().getHeaders(),
            validClaimSet.getClaims());
    String tokenValue = revokedToken.getTokenValue();
    tokenManager.getRevokedTokens().put(tokenValue, 1);

    when(authentication.getCredentials()).thenReturn(revokedToken);

    var e = assertThrows(RefreshTokenAlreadyRevokedException.class,
            () -> tokenManager.revokeToken(authentication));
    verify(authentication, times(1)).getCredentials();
    assertAll(
        () -> assertNotNull(e),
        () -> assertTrue(e.getMessage().startsWith(String.format("Token <%s>", tokenValue))),
        () -> assertTrue(e.getMessage().endsWith("revoked")),
        () -> assertTrue(tokenManager.getRevokedTokens().containsKey(tokenValue)));
  }
}

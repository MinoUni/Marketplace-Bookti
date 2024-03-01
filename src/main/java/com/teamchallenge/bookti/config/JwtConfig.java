package com.teamchallenge.bookti.config;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.teamchallenge.bookti.security.jwt.JwtToAuthorizedUserConverter;
import com.teamchallenge.bookti.security.jwt.KeyPairUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;

/**
 * Class with jwt configurations.
 *
 * @author Maksym Reva
 */
@Configuration
@RequiredArgsConstructor
class JwtConfig {

  private final KeyPairUtils keyPairUtils;
  private final JwtToAuthorizedUserConverter jwtToUserConverter;

  /**
   * Creates {@link JwtDecoder jwtAccessTokenDecoder}.
   *
   * @return {@link NimbusJwtDecoder}
   */
  @Bean
  @Primary
  public JwtDecoder jwtAccessTokenDecoder() {
    return NimbusJwtDecoder
        .withPublicKey(keyPairUtils.getAccessTokenPublicKey())
        .build();
  }

  /**
   * Creates {@link JwtEncoder jwtAccessTokenEncoder}.
   *
   * @return {@link NimbusJwtEncoder}
   */
  @Bean
  @Primary
  public JwtEncoder jwtAccessTokenEncoder() {
    JWK jwk = new RSAKey
        .Builder(keyPairUtils.getAccessTokenPublicKey())
        .privateKey(keyPairUtils.getAccessTokenPrivateKey())
        .build();
    JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(new JWKSet(jwk));
    return new NimbusJwtEncoder(jwkSource);
  }

  /**
   * Creates {@link JwtDecoder jwtRefreshTokenDecoder}.
   *
   * @return {@link NimbusJwtDecoder}
   */
  @Bean
  @Qualifier("jwtRefreshTokenDecoder")
  public JwtDecoder jwtRefreshTokenDecoder() {
    return NimbusJwtDecoder
        .withPublicKey(keyPairUtils.getRefreshTokenPublicKey())
        .build();
  }

  /**
   * Creates {@link JwtEncoder jwtRefreshTokenEncoder}.
   *
   * @return {@link NimbusJwtEncoder}
   */
  @Bean
  @Qualifier("jwtRefreshTokenEncoder")
  public JwtEncoder jwtRefreshTokenEncoder() {
    JWK jwk = new RSAKey
        .Builder(keyPairUtils.getRefreshTokenPublicKey())
        .privateKey(keyPairUtils.getRefreshTokenPrivateKey())
        .build();
    JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(new JWKSet(jwk));
    return new NimbusJwtEncoder(jwkSource);
  }

  /**
   * Creates {@link JwtAuthenticationProvider jwtRefreshTokenAuthenticationProvider}.
   *
   * @return {@link JwtAuthenticationProvider}
   */
  @Bean
  @Qualifier("jwtRefreshTokenAuthenticationProvider")
  public JwtAuthenticationProvider jwtRefreshTokenAuthenticationProvider() {
    JwtAuthenticationProvider provider = new JwtAuthenticationProvider(jwtRefreshTokenDecoder());
    provider.setJwtAuthenticationConverter(jwtToUserConverter);
    return provider;
  }
}

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

@Configuration
@RequiredArgsConstructor
public class JwtConfig {

    private final KeyPairUtils keyPairUtils;
    private final JwtToAuthorizedUserConverter jwtToUserConverter;

    @Bean
    @Primary
    public JwtDecoder jwtAccessTokenDecoder() {
        return NimbusJwtDecoder
                .withPublicKey(keyPairUtils.getAccessTokenPublicKey())
                .build();
    }

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

    @Bean
    @Qualifier("jwtRefreshTokenDecoder")
    public JwtDecoder jwtRefreshTokenDecoder() {
        return NimbusJwtDecoder
                .withPublicKey(keyPairUtils.getRefreshTokenPublicKey())
                .build();
    }

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

    @Bean
    @Qualifier("jwtRefreshTokenAuthenticationProvider")
    public JwtAuthenticationProvider jwtRefreshTokenAuthenticationProvider() {
        JwtAuthenticationProvider provider = new JwtAuthenticationProvider(jwtRefreshTokenDecoder());
        provider.setJwtAuthenticationConverter(jwtToUserConverter);
        return provider;
    }
}

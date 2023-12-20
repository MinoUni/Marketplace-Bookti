package com.teamchallenge.bookti.security.jwt;

import com.teamchallenge.bookti.dto.authorization.TokenPair;
import com.teamchallenge.bookti.security.AuthorizedUser;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

@Component
public class TokenGeneratorService {

    private final JwtEncoder accessTokenEncoder;
    @Qualifier("jwtRefreshTokenEncoder")
    private final JwtEncoder refreshTokenEncoder;
    private final String issuer;

    public TokenGeneratorService(JwtEncoder accessTokenEncoder,
                                 JwtEncoder refreshTokenEncoder,
                                 @Value("${spring.application.name}") String issuer) {
        this.accessTokenEncoder = accessTokenEncoder;
        this.refreshTokenEncoder = refreshTokenEncoder;
        this.issuer = issuer;
    }

    public TokenPair generateTokenPair(Authentication authentication) {
        if (!(authentication.getPrincipal() instanceof AuthorizedUser user)) {
            throw new BadCredentialsException(
                    MessageFormat.format("Principal <{0}> is not of <{1}> type",
                            authentication.getPrincipal().getClass(),
                            AuthorizedUser.class
                    )
            );
        }
        return TokenPair
                .builder()
                .timestamp(LocalDateTime.now())
                .userId(String.valueOf(user.getId()))
                .accessToken(generateAccessToken(authentication))
                .refreshToken(generateRefreshToken(authentication))
                .build();
    }

    private String generateAccessToken(Authentication authentication) {
        AuthorizedUser user = (AuthorizedUser) authentication.getPrincipal();
        Instant now = Instant.now();
        // TODO: Include user scopes, roles, not include sensitive info
        String scope = user.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(", "));
        JwtClaimsSet claimsSet = JwtClaimsSet
                .builder()
                .issuer(issuer)
                .issuedAt(now)
                .expiresAt(now.plus(30, ChronoUnit.MINUTES))
                .subject(String.valueOf(user.getId()))
                .claim("scope", scope)
                .build();
        return accessTokenEncoder
                .encode(JwtEncoderParameters.from(claimsSet))
                .getTokenValue();
    }

    private String generateRefreshToken(Authentication authentication) {
        AuthorizedUser user = (AuthorizedUser) authentication.getPrincipal();
        Instant now = Instant.now();
        JwtClaimsSet claimsSet = JwtClaimsSet
                .builder()
                .issuer(issuer)
                .issuedAt(now)
                .expiresAt(now.plus(7, ChronoUnit.DAYS))
                .subject(String.valueOf(user.getId()))
                .build();
        return refreshTokenEncoder
                .encode(JwtEncoderParameters.from(claimsSet))
                .getTokenValue();
    }
}

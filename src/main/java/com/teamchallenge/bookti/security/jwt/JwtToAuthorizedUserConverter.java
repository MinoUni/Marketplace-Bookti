package com.teamchallenge.bookti.security.jwt;

import com.teamchallenge.bookti.mapper.AuthorizedUserMapper;
import com.teamchallenge.bookti.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtToAuthorizedUserConverter implements Converter<Jwt, UsernamePasswordAuthenticationToken> {

    private final UserRepository userRepository;

    @Override
    public UsernamePasswordAuthenticationToken convert(Jwt jwt) {
        var userEntity = userRepository.findById(UUID.fromString(jwt.getSubject()))
                .orElseThrow(() -> new UsernameNotFoundException(MessageFormat.format("User with id <{0}> not found.", jwt.getSubject())));
        var user = AuthorizedUserMapper.mapFrom(userEntity);
        return new UsernamePasswordAuthenticationToken(user, jwt, user.getAuthorities());
    }
}

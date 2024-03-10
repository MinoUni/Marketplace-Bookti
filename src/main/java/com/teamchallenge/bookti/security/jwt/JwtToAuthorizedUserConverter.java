package com.teamchallenge.bookti.security.jwt;

import com.teamchallenge.bookti.user.UserRepository;
import com.teamchallenge.bookti.utils.AuthorizedUserMapper;
import java.text.MessageFormat;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

/**
 * Class that converts {@link UsernamePasswordAuthenticationToken} from {@link Jwt}.
 *
 * @author Maksym Reva
 */
@Component
@RequiredArgsConstructor
public class JwtToAuthorizedUserConverter
    implements Converter<Jwt, UsernamePasswordAuthenticationToken> {

  private final UserRepository userRepository;

  @Override
  public UsernamePasswordAuthenticationToken convert(Jwt jwt) {
    var userEntity = userRepository.findById(UUID.fromString(jwt.getSubject()))
        .orElseThrow(() -> new UsernameNotFoundException(
            MessageFormat.format("User with id <{0}> not found.", jwt.getSubject())));
    var user = AuthorizedUserMapper.mapFrom(userEntity);
    return new UsernamePasswordAuthenticationToken(user, jwt, user.getAuthorities());
  }
}

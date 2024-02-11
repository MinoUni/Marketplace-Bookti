package com.teamchallenge.bookti.model;

import static com.teamchallenge.bookti.model.Authority.USER_READ;
import static com.teamchallenge.bookti.model.Authority.USER_WRITE;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@RequiredArgsConstructor
@Getter
public enum Role {
  ROLE_USER(Set.of(USER_READ, USER_WRITE));

  private final Set<Authority> authorities;

  /**
   * Returns the authorities granted to the user and assigned role.
   *
   * @return the list of user authorities and his role
   */
  public List<SimpleGrantedAuthority> getAuthorities() {
    var authorityList = authorities.stream()
            .map(authority -> new SimpleGrantedAuthority(authority.getAuthority()))
            .collect(Collectors.toList());
    authorityList.add(new SimpleGrantedAuthority(this.name()));
    return authorityList;
  }
}

package com.teamchallenge.bookti.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enum that describes authorities.
 *
 * @author MinoUni
 * @version 1.0
 */
@Getter
@RequiredArgsConstructor
enum Authority {
  USER_READ("user:read"),
  USER_WRITE("user:write");

  private final String authority;
}

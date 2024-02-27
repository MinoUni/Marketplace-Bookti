package com.teamchallenge.bookti.model;

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
public enum Authority {
  USER_READ("user:read"),
  USER_WRITE("user:write");

  private final String authority;
}

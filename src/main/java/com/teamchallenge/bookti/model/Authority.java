package com.teamchallenge.bookti.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Authority {
  USER_READ("user:read"),
  USER_WRITE("user:write");

  private final String authority;
}

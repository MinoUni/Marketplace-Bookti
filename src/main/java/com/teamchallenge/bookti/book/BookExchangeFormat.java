package com.teamchallenge.bookti.book;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BookExchangeFormat {
  GIFT("gift"),
  EXCHANGE("exchange");

  private final String format;
}

package com.teamchallenge.bookti.book;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BookStatus {
  PENDING_APPROVAL("pending_approval"),
  APPROVED("approved");

  private final String status;
}

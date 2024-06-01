package com.teamchallenge.bookti.book;

import com.teamchallenge.bookti.constant.BookConstant;
import com.teamchallenge.bookti.exception.book.BookException;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BookStatus {
  PENDING_APPROVAL("pending_approval"),
  APPROVED("approved");

  private final String status;

  public static BookStatus findStatus(String status) {
    return Arrays.stream(BookStatus.values())
        .filter(s -> s.getStatus().equals(status))
        .findFirst()
        .orElseThrow(
            () -> new BookException(String.format(BookConstant.BOOK_STATUS_NOT_FOUND, status)));
  }
}

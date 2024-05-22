package com.teamchallenge.bookti.converter;

import com.teamchallenge.bookti.book.BookStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class BookStatusConverter implements AttributeConverter<BookStatus, String> {

  @Override
  public String convertToDatabaseColumn(BookStatus bookStatus) {
    if (bookStatus != null) {
      return bookStatus.getStatus();
    }
    return null;
  }

  @Override
  public BookStatus convertToEntityAttribute(String dbBookStatus) {
    if (dbBookStatus == null) {
      return null;
    }
    return Stream.of(BookStatus.values())
        .filter(e -> e.getStatus().equals(dbBookStatus))
        .findFirst()
        .orElseThrow(
            () -> new IllegalArgumentException(String.format("<%s> not exists", dbBookStatus)));
  }
}

package com.teamchallenge.bookti.converter;

import com.teamchallenge.bookti.book.BookExchangeFormat;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.stream.Stream;

/**
 * Class to convert {@link BookExchangeFormat} into {@link String}.
 *
 * @author MinoUni
 * @version 1.0
 */
@Converter(autoApply = true)
public class BookExchangeFormatConverter implements AttributeConverter<BookExchangeFormat, String> {

  @Override
  public String convertToDatabaseColumn(BookExchangeFormat exchangeFormat) {
    if (exchangeFormat == null) {
      return null;
    }
    return exchangeFormat.getFormat();
  }

  @Override
  public BookExchangeFormat convertToEntityAttribute(String exchangeFormat) {
    if (exchangeFormat == null) {
      return null;
    }
    return Stream.of(BookExchangeFormat.values())
        .filter(e -> e.getFormat().equals(exchangeFormat))
        .findFirst()
        .orElseThrow(
            () -> new IllegalArgumentException(String.format("<%s> not exists", exchangeFormat)));
  }
}

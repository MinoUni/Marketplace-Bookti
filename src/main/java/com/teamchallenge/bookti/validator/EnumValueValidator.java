package com.teamchallenge.bookti.validator;

import com.teamchallenge.bookti.annotation.EnumValue;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.stream.Stream;

public class EnumValueValidator implements ConstraintValidator<EnumValue, String> {

  private List<String> validValues;

  @Override
  public void initialize(EnumValue annotation) {
    validValues = Stream.of(annotation.enumClass().getEnumConstants()).map(Enum::name).toList();
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null) {
      return true;
    }
    return validValues.contains(value);
  }
}

package com.pureplate.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Unit {
  GRAMS("Grams"),
  ML("Milliliters"),
  PPM("Ppm"),
  PERCENTAGE("Percentage");

  private final String value;

}

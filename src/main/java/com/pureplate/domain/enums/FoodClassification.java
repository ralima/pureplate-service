package com.pureplate.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@AllArgsConstructor
@Getter
public enum FoodClassification {
  ULTRA_PROCESSED("Ultra Processed"),
  PROCESSED("Processed"),
  PROCESSED_CULINARY_INGREDIENTS("Processed Culinary Ingredients"),
  UNPROCESSED("Unprocessed");

  private final String value;
  public static Optional<String> getKey(String input) {
    return Arrays.stream(IngredientType.values())
            .filter(foodClassification -> foodClassification.name().equalsIgnoreCase(input) ||
                    foodClassification.getValue().equalsIgnoreCase(input))
            .findFirst()
            .map(Enum::name);
  }

}

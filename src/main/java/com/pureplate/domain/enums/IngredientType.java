package com.pureplate.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@AllArgsConstructor
@Getter
public enum IngredientType {
  COSMETIC("Cosmetic"),
  INDUSTRIALIZED("Industrialized"),
  MINIMALLY("Minimally Processed"),
  NATURAL("Natural");

  private final String value;
  public static Optional<String> getKey(String input) {
    return Arrays.stream(IngredientType.values())
            .filter(ingredientType -> ingredientType.name().equalsIgnoreCase(input) ||
                    ingredientType.getValue().equalsIgnoreCase(input))
            .findFirst()
            .map(Enum::name);
  }

}

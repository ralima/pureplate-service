package com.pureplate.domain;

import com.pureplate.domain.enums.IngredientType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

@Entity
@Table(name = "ingredients")
@Data
@ToString
public class Ingredient extends AbstractEntity {
    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    private IngredientType ingredientType;

    @Transient
    boolean isUltraProcessed() {
        return this.ingredientType.equals(IngredientType.COSMETIC) ? true : false;
    }

    @Transient
    boolean isIndustrialized() {
        return this.ingredientType.equals(IngredientType.INDUSTRIALIZED) ? true : false;
    }

    @Transient
    boolean isMinimally() {
        return this.ingredientType.equals(IngredientType.MINIMALLY) ? true : false;
    }

    @Transient
    boolean isNatural() {
        return this.ingredientType.equals(IngredientType.NATURAL) ? true : false;
    }
}

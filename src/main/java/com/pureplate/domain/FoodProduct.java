package com.pureplate.domain;

import com.pureplate.domain.enums.FoodClassification;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.ToString;

import java.util.Set;

@Entity
@Table(name = "food_products")
@Data
@ToString
public class FoodProduct extends AbstractEntity {


  @NotBlank(message = "Name is mandatory")
  private String name;
  @NotBlank(message = "Description is mandatory")
  private String description;


  @ManyToMany(cascade = {CascadeType.REFRESH}, fetch = FetchType.EAGER)
  @JoinTable(name = "food_products_ingredients",
          joinColumns = @JoinColumn(name = "food_product_id"),
          inverseJoinColumns = @JoinColumn(name = "ingredient_id"))
  private Set<Ingredient> ingredients;

  @Enumerated(EnumType.STRING)
  private FoodClassification score;
  private String scoreReason;

}

package com.pureplate.service;

import com.pureplate.domain.Ingredient;
import com.pureplate.domain.enums.IngredientType;
import com.pureplate.repository.IngredientRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class IngredientService {

    private final IngredientRepository ingredientRepository;

    public List<Ingredient> findAllIngredients(String stringFilter) {
        if (stringFilter == null || stringFilter.isEmpty()) {
            return ingredientRepository.findAll();
        } else {
            return ingredientRepository.search(stringFilter);
        }
    }

    public Integer countIngredientsPerType(IngredientType type){
        return ingredientRepository.countIngredientsByIngredientType(type);
    }

    public long countIngredients() {
        return ingredientRepository.count();
    }

    public void deleteIngredient(Ingredient ingredient) {
        ingredientRepository.delete(ingredient);
    }

    public void saveIngredient(Ingredient ingredient) {
        if (ingredient == null) {
            System.err.println("Ingredient is null. Are you sure you have connected your form to the application?");
            return;
        }
        ingredientRepository.save(ingredient);
    }
}

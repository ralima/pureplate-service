package com.pureplate.service;

import com.pureplate.domain.Ingredient;
import com.pureplate.domain.enums.IngredientType;
import com.pureplate.domain.enums.QuestionType;
import com.pureplate.repository.IngredientRepository;
import com.pureplate.utils.GptUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class IngredientService {

    private final IngredientRepository ingredientRepository;

    private final GPTService gptService;


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
        String score = "";
        if (ingredient.getIngredientType() == null) {
            String ingredientTypes = Arrays.stream(IngredientType.values())
                    .map(Enum::name)
                    .collect(Collectors.joining(", "));
            String question = GptUtil.getQuestion(QuestionType.INGREDIENT_SCORE, ingredientTypes, ingredient.getName());

            score = gptService.chat(question);
            ingredient.setIngredientType(IngredientType.valueOf(score));
        }

        String reason = "";
        if(ingredient.getReason() == null || ingredient.getReason().equals("")) {
            String question = GptUtil.getQuestion(QuestionType.INGREDIENT_SCORE_REASON, ingredient.getName(), score);
            reason = gptService.chat(question);
            ingredient.setReason(reason);
        }
        ingredientRepository.save(ingredient);
    }
}

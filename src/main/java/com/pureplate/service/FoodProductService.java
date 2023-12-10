package com.pureplate.service;

import com.pureplate.domain.FoodProduct;
import com.pureplate.domain.Ingredient;
import com.pureplate.domain.enums.FoodClassification;
import com.pureplate.domain.enums.IngredientType;
import com.pureplate.domain.enums.QuestionType;
import com.pureplate.repository.FoodProductRepository;
import com.pureplate.utils.GptUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class FoodProductService {

    private final FoodProductRepository foodProductRepository;

    private final GPTService gptService;

    public List<FoodProduct> findAllFoodProducts(String stringFilter) {
        if (stringFilter == null || stringFilter.isEmpty()) {
            return foodProductRepository.findAll();
        } else {
            return foodProductRepository.search(stringFilter);
        }
    }
    public long countFoodProducts() {
        return foodProductRepository.count();
    }

    public void deleteFoodProduct(FoodProduct foodProduct) {
        foodProductRepository.delete(foodProduct);
    }
    public void saveFoodProduct(FoodProduct foodProduct) {
        if (foodProduct == null) {
            System.err.println("Food product is null. Are you sure you have connected your form to the application?");
            return;
        }
        String score = "";
        if (foodProduct.getScore() == null) {
            foodProduct.setScore(classify(foodProduct.getIngredients()));
        }

        String reason = "";
        if(foodProduct.getScoreReason() == null || foodProduct.getScoreReason().equals("")) {
            String question = GptUtil.getQuestion(QuestionType.FOOD_CLASSIFICATION_REASON, foodProduct.getName(), score);
            reason = gptService.chat(question);
            foodProduct.setScoreReason(reason);
        }

        foodProductRepository.save(foodProduct);
    }

    public FoodProduct classifyFoodProduct(FoodProduct food) {

        FoodClassification foodClassification = classify(food.getIngredients());

        food.setScore(foodClassification);

        return foodProductRepository.save(food);
    }

    private FoodClassification classify(Set<Ingredient> ingredients){
        long totalIngredients = ingredients.size();

        long numCosmetic = countIngredientsOfType(ingredients, IngredientType.COSMETIC);
        long numIndustrialized = countIngredientsOfType(ingredients, IngredientType.INDUSTRIALIZED);
        long numMinimally = countIngredientsOfType(ingredients, IngredientType.MINIMALLY);

        if (percentage(numCosmetic, totalIngredients) >= 25) {
            return FoodClassification.ULTRA_PROCESSED;
        } else if (percentage(numIndustrialized, totalIngredients) >= 25) {
            return FoodClassification.PROCESSED;
        } else if (percentage(numCosmetic, totalIngredients) < 25 && percentage(numIndustrialized, totalIngredients) < 25 && percentage(numMinimally, totalIngredients) >= 25) {
            return FoodClassification.PROCESSED_CULINARY_INGREDIENTS;
        } else {
            return FoodClassification.UNPROCESSED;
        }
    }

    private long countIngredientsOfType(Set<Ingredient> ingredients, IngredientType type) {
        return ingredients.stream().filter(ingredient -> ingredient.getIngredientType() == type).count();
    }

    private double percentage(long part, long total) {
        return (part * 100.0) / total;
    }

}

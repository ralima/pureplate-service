package com.pureplate.utils;

import com.pureplate.domain.enums.QuestionType;
import org.springframework.stereotype.Component;

@Component
public class GptUtil {

    public static String getQuestion(QuestionType questionType, String... args) {
        switch (questionType) {
            case INGREDIENT_SCORE:
                return String.format(INGREDIENT_TYPE, args);
            case INGREDIENT_SCORE_REASON:
                return String.format(INGREDIENT_SCORE_REASON, args);
            case FOOD_CLASSIFICATION_REASON:
                return String.format(FOOD_CLASSIFICATION_REASON, args);
            // ... handle other cases
            default:
                throw new IllegalArgumentException("Unknown Question Type: " + questionType);
        }
    }

    public static final String INGREDIENT_TYPE = "Choose one of the values: %s without an explanation. Based on the NOVA food classification system what's the classification of %s?";
    public static final String INGREDIENT_SCORE_REASON = "If %s is considered %s, can you write a 100 words maximum reason justifying why?";
    public static final String FOOD_CLASSIFICATION_REASON = "Give me just the reason. If I say the food %s is %s, can you write a 100 words maximum reason justifying adjective?";

}

package com.pureplate.service;

import com.pureplate.domain.FoodProduct;
import com.pureplate.repository.FoodProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class FoodProductService {

    private final FoodProductRepository foodProductRepository;

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
        foodProductRepository.save(foodProduct);
    }

}

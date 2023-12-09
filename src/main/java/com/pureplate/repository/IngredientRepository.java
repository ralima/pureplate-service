package com.pureplate.repository;

import com.pureplate.domain.Ingredient;
import com.pureplate.domain.enums.IngredientType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {

    @Query("select i from Ingredient i " +
            "where lower(i.name) like lower(concat('%', :searchTerm, '%')) " +
            "or lower(i.description) like lower(concat('%', :searchTerm, '%'))")
    List<Ingredient> search(@Param("searchTerm") String searchTerm);

    public List<Ingredient> findIngredientsByIngredientType(IngredientType type);

    public Integer countIngredientsByIngredientType(IngredientType type);
}

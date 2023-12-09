package com.pureplate.repository;

import com.pureplate.domain.FoodProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FoodProductRepository  extends JpaRepository<FoodProduct, Long> {
    @Query("select f from FoodProduct f " +
            "where lower(f.name) like lower(concat('%', :searchTerm, '%')) " +
            "or lower(f.description) like lower(concat('%', :searchTerm, '%'))")
    List<FoodProduct> search(@Param("searchTerm") String searchTerm);
}

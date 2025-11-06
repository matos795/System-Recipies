package com.MyRecipies.recipies.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.MyRecipies.recipies.entities.Ingredient;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {

    @Query("SELECT i FROM Ingredient i WHERE i.client.id = :clientId")
    Page<Ingredient> findByClientId(Long clientId, Pageable pageable);

}

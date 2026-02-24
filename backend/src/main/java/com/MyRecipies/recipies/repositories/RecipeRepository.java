package com.MyRecipies.recipies.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

import com.MyRecipies.recipies.entities.Recipe;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    @Query("SELECT r FROM Recipe r WHERE r.client.id = :clientId AND r.deleted = false")
    Page<Recipe> findByClientId(Long clientId, Pageable pageable);

    @Query("SELECT r FROM Recipe r WHERE r.deleted = false")
    @NonNull
    Page<Recipe> findAll(@NonNull Pageable pageable);
}

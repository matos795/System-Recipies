package com.MyRecipies.recipies.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.MyRecipies.recipies.entities.RecipeItemVersion;

public interface RecipeItemVersionRepository extends JpaRepository<RecipeItemVersion, Long> {
}
package com.MyRecipies.recipies.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.MyRecipies.recipies.entities.RecipeVersion;

public interface RecipeVersionRepository extends JpaRepository<RecipeVersion, Long> {
}


